package io.retailplanet.backend.health.client;

import io.retailplanet.backend.common.events.health.KafkaHealthCheckEvent;
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
  private HealthEventFacade eventFacade;

  @Override
  public HealthCheckResponse call()
  {
    Duration roundtripTime = _getRoundtripTime();
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
  private Duration _getRoundtripTime()
  {
    try
    {
      KafkaHealthCheckEvent source = new KafkaHealthCheckEvent()
          .started(Instant.now());
      KafkaHealthCheckEvent result = eventFacade.sendHealthCheckEvent(source).blockingGet();
      return Duration.between(source.started(), result.answered());
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to calculate roundtrip time", e);
      return null;
    }
  }

}
