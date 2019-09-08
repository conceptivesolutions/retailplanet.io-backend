package io.retailplanet.backend.metrics.api.internal;

import javax.ws.rs.*;

/**
 * Service to answer metric calls, comparable to an echo service to get response times
 *
 * @author w.glanzer, 28.08.2019
 */
@Path("/internal/metrics")
public class MetricAnswerService
{

  @GET
  @Path("ping")
  public long ping()
  {
    return System.currentTimeMillis();
  }


}
