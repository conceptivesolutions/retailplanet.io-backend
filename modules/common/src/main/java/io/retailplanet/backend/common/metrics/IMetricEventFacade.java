package io.retailplanet.backend.common.metrics;

import org.jetbrains.annotations.NotNull;

/**
 * Facade to collect metrics from different events
 *
 * @author w.glanzer, 01.09.2019
 */
public interface IMetricEventFacade
{

  /**
   * Will be called, if the retreival of an answer of an event throws an exception
   *
   * @param pException Exception that was fired
   */
  void fireAnswerReceiveError(@NotNull Throwable pException);

}
