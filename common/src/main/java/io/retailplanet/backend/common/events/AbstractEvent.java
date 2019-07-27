package io.retailplanet.backend.common.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.opentracing.*;
import io.opentracing.propagation.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.reactivex.*;
import io.retailplanet.backend.common.util.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public abstract class AbstractEvent<S extends AbstractEvent<S>>
{
  private static final boolean _TRACE_ENABLED = !Utility.isNullOrEmptyTrimmedString(System.getenv("OPENTRACING_SERVERS"));
  private static final String _REFERENCE_TYPE_KEY = "__REF_TYPE";

  @JsonProperty
  public String chainID = UUID.randomUUID().toString();

  /* Current execution context for OpenTracing propagation */
  @JsonProperty
  public Map<String, String> traceContext;

  /* Topic, where this event comes from */
  String receivedTopic;

  private Span computingSpan;

  public <T extends AbstractEvent> T createAnswer(Class<T> pClazz)
  {
    try
    {
      T event = pClazz.newInstance();
      event.chainID = chainID;
      return event;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Starts to trace the current event computation
   *
   * @param pTracer Tracer
   */
  void startTrace(@NotNull Tracer pTracer)
  {
    if (!_TRACE_ENABLED)
      return;

    Tracer.SpanBuilder builder = pTracer.buildSpan(getClass().getSimpleName());
    if (traceContext != null)
    {
      String refType = traceContext.get(_REFERENCE_TYPE_KEY);
      if (refType != null)
        builder = builder.addReference(refType, pTracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(traceContext)));
    }

    if (receivedTopic != null)
      builder = builder.withTag("topic", receivedTopic);

    computingSpan = builder.startActive(true).span();
  }

  /**
   * Sets the current active span as trace to this event
   *
   * @param pTracer Tracer to get the span from
   */
  void injectCurrentTrace(@NotNull Tracer pTracer)
  {
    if (!_TRACE_ENABLED)
      return;

    traceContext = new HashMap<>();

    Span span = pTracer.activeSpan();
    if (span != null)
    {
      traceContext.put(_REFERENCE_TYPE_KEY, References.CHILD_OF);
      pTracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMapInjectAdapter(traceContext));
    }
  }

  /**
   * Finishes the computation of this event
   */
  void finishTrace()
  {
    if (!_TRACE_ENABLED)
      return;

    if (computingSpan != null)
      computingSpan.finish();
  }

  /**
   * Creates a new flowable which waits for a result (and error) with the given chainID on the given flowables
   *
   * @return Flowable with filtered chainID and set timeout
   */
  @SafeVarargs
  @NotNull
  final <T extends AbstractEvent> Single<T> waitForAnswer(@NotNull Flowable<ErrorEvent> pErrors, @NotNull Flowable<? extends T>... pFlowables)
  {
    return Flowable.merge(Flowable.merge(Arrays.asList(pFlowables)), pErrors)
        .timeout(1500, TimeUnit.MILLISECONDS)
        .filter(pEvent -> pEvent.chainID.trim().equals(chainID))
        .map(pEvent -> {
          if (pEvent instanceof ErrorEvent)
            throw ((ErrorEvent) pEvent).error();
          return (T) pEvent;
        })
        .firstOrError();
  }

}
