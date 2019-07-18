package io.retailplanet.backend.common.events;

import org.jetbrains.annotations.*;

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
  void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull Throwable pThrowable);

  /**
   * Notifies an error in emitter and own logger
   *
   * @param pMessage   additional error message
   * @param pThrowable error
   */
  void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull String pMessage, @NotNull Throwable pThrowable);

}
