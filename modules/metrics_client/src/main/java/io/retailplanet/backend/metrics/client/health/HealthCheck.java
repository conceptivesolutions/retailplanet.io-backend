package io.retailplanet.backend.metrics.client.health;

import io.retailplanet.backend.metrics.client.IMetricServerService;
import org.eclipse.microprofile.health.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

/**
 * HealthCheck which sends messages and waits for an answer from the corresponding answer service
 *
 * @author w.glanzer, 28.08.2019
 */
@Readiness
@ApplicationScoped
public class HealthCheck implements org.eclipse.microprofile.health.HealthCheck
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(HealthCheck.class);

  @Inject
  @RestClient
  private IMetricServerService metricServerService;

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
   * @return the roundtrip time, from here to the answer service and back
   */
  @Nullable
  protected Duration getRoundtripTime()
  {
    try
    {
      return Duration.ofMillis(metricServerService.ping());
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to calculate roundtrip time", e);
      return null;
    }
  }

}
