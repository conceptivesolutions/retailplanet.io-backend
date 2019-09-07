package io.retailplanet.backend.products.impl.services;

import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/markets/search")
@RegisterRestClient
@URL(targetModule = URL.ETarget.MARKETS)
public interface IMarketSearchService
{

  /**
   * Executes an geoSearch over all markets
   *
   * @param pLat      Latitude
   * @param pLon      Longitude
   * @param pDistance Distance in km
   */
  @POST
  List<String> geoSearch(@PathParam("lat") double pLat, @PathParam("lon") double pLon, @PathParam("dist") int pDistance);

}
