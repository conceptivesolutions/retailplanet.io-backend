package io.retailplanet.backend.health.service.api;

import io.retailplanet.backend.common.events.health.KafkaHealthCheckEvent;
import io.retailplanet.backend.health.service.impl.events.IEvents;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

/**
 * Service to answer health events, comparable to an echo service to get response times
 *
 * @author w.glanzer, 28.08.2019
 */
@ApplicationScoped
public class HealthAnswerService
{

  @Incoming(IEvents.IN_HEALTHCHECK_REQUEST)
  @Outgoing(IEvents.OUT_HEALTHCHECK_RESPONSE)
  @Broadcast
  public KafkaHealthCheckEvent answerHealthCheckEvent(@Nullable KafkaHealthCheckEvent pEvent)
  {
    if (pEvent == null)
      return null;

    return pEvent.answered(Instant.now());
  }


}
