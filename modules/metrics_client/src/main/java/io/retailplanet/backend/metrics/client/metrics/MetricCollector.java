package io.retailplanet.backend.metrics.client.metrics;

import io.retailplanet.backend.metrics.client.IMetricServerService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.*;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Collectors metrics for interaction with other services
 *
 * @author w.glanzer, 29.08.2019
 */
@SuppressWarnings("unused") // Just for initialisation purposes todo maybe really useless?
@ApplicationScoped
public class MetricCollector
{

  @Inject
  @RestClient
  private IMetricServerService metricServerService;

  @SuppressWarnings("unused")
  public void init(@Observes @Initialized(ApplicationScoped.class) Object pInit)
  {
    // We need this init method to force quarkus to initialize this class.
    // Can we do this anyhow else?
  }

}
