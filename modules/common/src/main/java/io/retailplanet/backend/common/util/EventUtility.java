package io.retailplanet.backend.common.util;

import io.reactivex.Flowable;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Utility containing all methods for handling events
 *
 * @author w.glanzer, 01.09.2019
 */
public class EventUtility
{

  /**
   * Makes the event flowable replaying a specific amount of previous events
   *
   * @param pNotConnected Flowable
   * @return Replayable flowable
   */
  @NotNull
  public static <T> Flowable<T> replayEvents(@NotNull Flowable<T> pNotConnected)
  {
    return pNotConnected
        .replay(AbstractEvent.TTL, TimeUnit.MILLISECONDS)
        .autoConnect(0);
  }

}
