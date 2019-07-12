package io.retailplanet.backend.common.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.reactivex.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author w.glanzer, 11.07.2019
 */
public abstract class AbstractEvent<S extends AbstractEvent<S>>
{

  @JsonProperty
  public String chainID = UUID.randomUUID().toString();

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
   * Creates a new flowable which waits for a result with the given chainID on the given flowables
   *
   * @return Flowable with filtered chainID and set timeout
   */
  @SafeVarargs
  @NotNull
  public final <T extends AbstractEvent> Single<T> waitForAnswer(@NotNull Flowable<? extends T>... pFlowables)
  {
    return Flowable.merge(Arrays.asList(pFlowables))
        .filter(pEvent -> pEvent.chainID.trim().equals(chainID))
        .timeout(30, TimeUnit.SECONDS)
        .firstOrError();
  }

}
