package io.retailplanet.backend.metrics.client.metrics;

import io.retailplanet.backend.common.events.metric.KafkaMetricEvent;
import io.retailplanet.backend.metrics.client.MetricEventFacade;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.slf4j.*;

import javax.enterprise.context.*;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Instant;

/**
 * Collectors metrics for interaction with kafka
 *
 * @author w.glanzer, 29.08.2019
 */
@ApplicationScoped
public class KafkaMetricCollector
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(KafkaMetricCollector.class);

  @Inject
  private MetricEventFacade eventFacade;

  @SuppressWarnings("unused")
  public void init(@Observes @Initialized(ApplicationScoped.class) Object pInit)
  {
    // We need this init method to force quarkus to initialize this class.
    // Can we do this anyhow else?
  }

  /**
   * Calculates the current roundtrip time
   */
  @Gauge(name = "roundtripTime", description = "Describes how long a kafka messages takes from a service to kafka and back", unit = MetricUnits.MILLISECONDS)
  public long getRoundtripTime()
  {
    KafkaMetricEvent answerEvent = eventFacade.sendMetricsEvent(new KafkaMetricEvent().started(Instant.now())).blockingGet();
    long result = System.currentTimeMillis() - answerEvent.started().toEpochMilli();
    _LOGGER.info("RoundtripTime: " + result + "ms");
    return result;
  }

}
