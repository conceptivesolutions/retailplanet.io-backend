package io.retailplanet.backend.common.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.reactivex.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public abstract class AbstractEvent<S extends AbstractEvent<S>>
{

  @JsonProperty
  public String chainID = UUID.randomUUID().toString();

  /* Context for OpenTracing propagation */
  @JsonProperty
  public Map<String, String> traceContext;

  /* Timestamp when this event was received */
  Long receivedTimeMillis;

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
