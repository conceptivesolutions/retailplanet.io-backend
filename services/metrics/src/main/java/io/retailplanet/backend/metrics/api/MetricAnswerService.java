package io.retailplanet.backend.metrics.api;

import io.retailplanet.backend.common.events.metric.KafkaMetricEvent;
import io.retailplanet.backend.metrics.impl.events.IEvents;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

/**
 * Service to answer metric events, comparable to an echo service to get response times
 *
 * @author w.glanzer, 28.08.2019
 */
@ApplicationScoped
public class MetricAnswerService
{

  @Incoming(IEvents.IN_METRICS_REQUEST)
  @Outgoing(IEvents.OUT_METRICS_RESPONSE)
  @Broadcast
  public KafkaMetricEvent answerMetricsEvent(@Nullable KafkaMetricEvent pEvent)
  {
    if (pEvent == null)
      return null;

    return pEvent.answered(Instant.now());
  }


}
