package io.retailplanet.backend.health.client;

import io.retailplanet.backend.common.events.health.KafkaHealthCheckEvent;
import org.eclipse.microprofile.health.*;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

import javax.enterprise.context.*;
import javax.enterprise.event.Observes;
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

  private Thread collectorThread;

  public void init(@SuppressWarnings("unused") @Observes @Initialized(ApplicationScoped.class) Object pInit)
  {
    if (collectorThread != null)
      return;
    collectorThread = new Thread(new _MetricCollector(), "tRoundtripMetricCollector");
    collectorThread.setDaemon(true);
    collectorThread.start();
  }

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
  @Timed(name = "roundtripTime", description = "Describes how long a kafka messages takes from a service to kafka and back", unit = MetricUnits.MILLISECONDS)
  protected Duration getRoundtripTime()
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

  /**
   * Runnable that calls _getRoundtripTime to collect metrics
   */
  private class _MetricCollector implements Runnable
  {
    @Override
    public void run()
    {
      while (!Thread.currentThread().isInterrupted())
      {
        try
        {
          Thread.sleep(30_000);
          KafkaHealthCheck.this.getRoundtripTime();
        }
        catch (InterruptedException ignored)
        {
        }
      }
    }
  }
}
