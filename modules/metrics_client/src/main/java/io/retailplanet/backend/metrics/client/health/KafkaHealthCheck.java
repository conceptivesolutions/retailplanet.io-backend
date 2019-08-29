package io.retailplanet.backend.metrics.client.health;

import io.retailplanet.backend.common.events.metric.KafkaMetricEvent;
import io.retailplanet.backend.metrics.client.MetricEventFacade;
import org.eclipse.microprofile.health.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.*;

/**
 * HealthCheck which sends kafka messages and waits for an answer from the corresponding answer service
 *
 * @author w.glanzer, 28.08.2019
 */
@Readiness
@ApplicationScoped
public class KafkaHealthCheck implements HealthCheck
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(KafkaHealthCheck.class);

  @Inject
  private MetricEventFacade eventFacade;

  @Override
  public HealthCheckResponse call()
  {
    Duration roundtripTime = getRoundtripTime();
    HealthCheckResponseBuilder builder = HealthCheckResponse.named("RoundtripCheck");

    if (roundtripTime == null)
      return builder
          .down()
          .build();

    return builder
        .up()
        .withData("roundtrip", roundtripTime.toMillis())
        .build();
  }


  /**
   * Calculates the current roundtrip time
   *
   * @return the roundtrip time, from here to kafka answer service and back
   */
  @Nullable
  protected Duration getRoundtripTime()
  {
    try
    {
      KafkaMetricEvent source = new KafkaMetricEvent()
          .started(Instant.now());
      KafkaMetricEvent result = eventFacade.sendMetricsEvent(source).blockingGet();
      return Duration.between(source.started(), result.answered());
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to calculate roundtrip time", e);
      return null;
    }
  }

}
