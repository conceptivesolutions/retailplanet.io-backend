package io.retailplanet.backend.health.service.impl.events;

import io.retailplanet.backend.common.processor.*;

/**
 * @author w.glanzer, 28.08.2019
 */
@EventContainer(groupID = "shealth")
public interface IEvents
{

  /**
   * HealthCheck-Event received, has to be answered
   */
  @IncomingEvent
  String IN_HEALTHCHECK_REQUEST = "Health_REQUEST_IN";

  /**
   * Answer the HealthCheck-Event
   */
  @OutgoingEvent
  String OUT_HEALTHCHECK_RESPONSE = "Health_RESPONSE_OUT";

}
