package io.retailplanet.backend.metrics.impl.events;

import io.retailplanet.backend.common.processor.*;

/**
 * @author w.glanzer, 28.08.2019
 */
@EventContainer(groupID = "smetric")
public interface IEvents
{

  /**
   * Metrics-Event received, has to be answered
   */
  @IncomingEvent
  String IN_METRICS_REQUEST = "Metrics_REQUEST_IN";

  /**
   * Answer the Metrics-Event
   */
  @OutgoingEvent
  String OUT_METRICS_RESPONSE = "Metrics_RESPONSE_OUT";

}
