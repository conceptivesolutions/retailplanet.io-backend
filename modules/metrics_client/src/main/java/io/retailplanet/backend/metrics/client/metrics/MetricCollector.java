package io.retailplanet.backend.metrics.client.metrics;

import io.retailplanet.backend.metrics.client.IMetricServerService;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.*;

import javax.enterprise.context.*;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Collectors metrics for interaction with other services
 *
 * @author w.glanzer, 29.08.2019
 */
@ApplicationScoped
public class MetricCollector
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(MetricCollector.class);

  @Inject
  @RestClient
  private IMetricServerService metricServerService;

  @SuppressWarnings("unused")
  public void init(@Observes @Initialized(ApplicationScoped.class) Object pInit)
  {
    // We need this init method to force quarkus to initialize this class.
    // Can we do this anyhow else?
  }

  /**
   * Calculates the current roundtrip time
   */
  @Gauge(name = "roundtripTime", description = "Describes how long a messages takes from a service to another and back", unit = MetricUnits.MILLISECONDS)
  public long getRoundtripTime()
  {
    long started = System.currentTimeMillis();
    metricServerService.ping();
    long result = System.currentTimeMillis() - started;
    _LOGGER.info("RoundtripTime: " + result + "ms");
    return result;
  }

}
