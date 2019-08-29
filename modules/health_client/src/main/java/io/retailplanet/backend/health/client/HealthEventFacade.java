package io.retailplanet.backend.health.client;

import io.reactivex.*;
import io.retailplanet.backend.common.events.AbstractEventFacade;
import io.retailplanet.backend.common.events.health.KafkaHealthCheckEvent;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 28.08.2019
 */
@ApplicationScoped
class HealthEventFacade extends AbstractEventFacade
{

  @Stream("Health_REQUEST_OUT")
  protected Emitter<KafkaHealthCheckEvent> healthCheckEventEmitter;

  @Stream("Health_RESPONSE_IN")
  protected Flowable<KafkaHealthCheckEvent> healthCheckEventFlowable;

  @NotNull
  public Single<KafkaHealthCheckEvent> sendHealthCheckEvent(@NotNull KafkaHealthCheckEvent pEvent)
  {
    return send(pEvent, healthCheckEventEmitter, healthCheckEventFlowable);
  }

}
