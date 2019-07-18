package io.retailplanet.backend.common.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 18.07.2019
 */
public interface IAbstractEventFacade
{

  /**
   * Notifies an error in emitter and own logger
   *
   * @param pThrowable error
   */
  void notifyError(@NotNull Throwable pThrowable);

  /**
   * Notifies an error in emitter and own logger
   *
   * @param pMessage   additional error message
   * @param pThrowable error
   */
  void notifyError(@NotNull String pMessage, @NotNull Throwable pThrowable);

}
