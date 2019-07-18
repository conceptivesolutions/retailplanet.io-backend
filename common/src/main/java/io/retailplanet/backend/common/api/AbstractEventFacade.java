package io.retailplanet.backend.common.api;

import io.reactivex.Flowable;
import io.retailplanet.backend.common.events.ErrorEvent;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

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

  @Override
  public void notifyError(@NotNull Throwable pThrowable)
  {
    notifyError("", pThrowable);
  }

  @Override
  public void notifyError(@NotNull String pMessage, @NotNull Throwable pThrowable)
  {
    LoggerFactory.getLogger(getClass()).error(pMessage, pThrowable);
    errorsEmitter.send(new ErrorEvent().error(pThrowable));
  }

}
