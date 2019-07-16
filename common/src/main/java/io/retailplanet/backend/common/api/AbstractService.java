package io.retailplanet.backend.common.api;

import io.reactivex.Flowable;
import io.retailplanet.backend.common.events.ErrorEvent;
import io.smallrye.reactive.messaging.annotations.*;

/**
 * Abstrct service for all kafka services
 *
 * @author w.glanzer, 16.07.2019
 */
public abstract class AbstractService
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

}
