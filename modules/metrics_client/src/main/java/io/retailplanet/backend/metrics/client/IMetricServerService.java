package io.retailplanet.backend.metrics.client;

import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;

/**
 * interface which calls the internal metrics service
 *
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/metrics")
@RegisterRestClient
@URL(targetModule = URL.ETarget.METRICS)
public interface IMetricServerService
{

  /**
   * @return the timestamp (utc millis) when the message was received on the server
   */
  @GET
  @Path("/ping")
  long ping();

}
