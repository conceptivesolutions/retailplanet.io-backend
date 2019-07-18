package io.retailplanet.backend.common.events;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.*;
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
  public void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull Throwable pThrowable)
  {
    notifyError(pSourceEvent, "", pThrowable);
  }

  @Override
  public void notifyError(@Nullable AbstractEvent<?> pSourceEvent, @NotNull String pMessage, @NotNull Throwable pThrowable)
  {
    // log
    LoggerFactory.getLogger(getClass()).error(pMessage, pThrowable);

    // send
    ErrorEvent event = pSourceEvent == null ? new ErrorEvent() : pSourceEvent.createAnswer(ErrorEvent.class);
    errorsEmitter.send(event.error(pThrowable));
  }

}
