package io.retailplanet.backend.common.events;

import org.jetbrains.annotations.*;

import java.util.function.Supplier;

/**
 * @author w.glanzer, 18.07.2019
 */
public interface IAbstractEventFacade
{

  /**
   * Trace (mainly incoming) events, so that our OpenTracing-API can use it
   *
   * @param pEvent Event
   * @param pFn    Function to execute, traced
   */
  default void trace(@NotNull AbstractEvent<?> pEvent, @NotNull Runnable pFn)
  {
    trace(pEvent, () -> {
      pFn.run();
      return null;
    });
  }

  /**
   * Trace (mainly incoming) events, so that our OpenTracing-API can use it
   *
   * @param pEvent Event
   * @param pFn    Function to execute, traced
   */
  <Out> Out trace(@NotNull AbstractEvent<?> pEvent, @NotNull Supplier<Out> pFn);

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
