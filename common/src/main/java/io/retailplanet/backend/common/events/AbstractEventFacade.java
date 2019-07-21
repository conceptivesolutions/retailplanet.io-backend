package io.retailplanet.backend.common.events;

import io.opentracing.*;
import io.opentracing.propagation.*;
import io.reactivex.*;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.*;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * @author w.glanzer, 18.07.2019
 */
public abstract class AbstractEventFacade implements IAbstractEventFacade
{

  /**
   * Flowable for incoming error messages
   */
  @Stream("ERRORS_IN")
  protected Flowable<ErrorEvent> errorsFlowable;

  /**
   * Emitter to emit error messages
   */
  @Stream("ERRORS_OUT")
  protected Emitter<ErrorEvent> errorsEmitter;

  @Inject
  protected Tracer tracer;

  @Override
  public void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull Throwable pThrowable)
  {
    notifyError(pSourceEvent, "", pThrowable);
  }

  @Override
  public void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull String pMessage, @NotNull Throwable pThrowable)
  {
    ErrorEvent event = pSourceEvent == null ? new ErrorEvent() : pSourceEvent.createAnswer(ErrorEvent.class);
    event = event.error(new Exception(pMessage, pThrowable));

    // log
    LoggerFactory.getLogger(getClass()).error(pMessage, pThrowable);

    // send
    errorsEmitter.send(event);
  }

  @SafeVarargs
  @NotNull
  protected final <In extends AbstractEvent<In>, Out extends AbstractEvent<Out>> Single<Out> send(@NotNull In pEvent, @NotNull Emitter<In> pEmitter, Flowable<? extends Out>... pAnswerFlowables)
  {
    Span span = tracer.buildSpan("SEND:" + pEvent.getClass().getSimpleName())
        .asChildOf(tracer.activeSpan())
        .start();

    // Inject current TraceContext for context propagation
    pEvent.traceContext = new HashMap<>();
    pEvent.traceContext.put("__CTX_TYPE", pAnswerFlowables.length == 0 ? References.CHILD_OF : References.FOLLOWS_FROM);
    tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMapInjectAdapter(pEvent.traceContext));

    pEmitter.send(pEvent);

    if (pAnswerFlowables.length == 0)
      return Single.never();

    return pEvent.waitForAnswer(errorsFlowable, pAnswerFlowables);
  }

}
