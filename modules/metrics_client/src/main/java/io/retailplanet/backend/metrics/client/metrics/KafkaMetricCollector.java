package io.retailplanet.backend.metrics.client.metrics;

import io.retailplanet.backend.common.events.metric.KafkaMetricEvent;
import io.retailplanet.backend.metrics.client.MetricEventFacade;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.*;

import javax.enterprise.context.*;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.*;

/**
 * Collectors metrics for interaction with kafka
 *
 * @author w.glanzer, 29.08.2019
 */
@ApplicationScoped
public class KafkaMetricCollector implements Runnable
{

  private static final Duration _INTERVAL = Duration.ofSeconds(30);
  private static final Logger _LOGGER = LoggerFactory.getLogger(KafkaMetricCollector.class);

  @Inject
  private MetricEventFacade eventFacade;

  private Thread collectorThread;

  public void init(@SuppressWarnings("unused") @Observes @Initialized(ApplicationScoped.class) Object pInit)
  {
    if (collectorThread != null)
      return;
    collectorThread = new Thread(this, "tRoundtripMetricCollector");
    collectorThread.setDaemon(true);
    collectorThread.start();
  }

  @Override
  public void run()
  {
    while (!Thread.currentThread().isInterrupted())
    {
      try
      {
        Thread.sleep(_INTERVAL.toMillis());
        KafkaMetricCollector.this.getRoundtripTime();
      }
      catch (Exception ignored)
      {
      }
    }
  }

  /**
   * Calculates the current roundtrip time
   */
  @Timed(name = "roundtripTime", description = "Describes how long a kafka messages takes from a service to kafka and back", unit = MetricUnits.MILLISECONDS)
  protected void getRoundtripTime()
  {
    try
    {
      //noinspection ResultOfMethodCallIgnored
      eventFacade.sendMetricsEvent(new KafkaMetricEvent().started(Instant.now())).blockingGet();
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to calculate roundtrip time", e);
    }
  }

}
