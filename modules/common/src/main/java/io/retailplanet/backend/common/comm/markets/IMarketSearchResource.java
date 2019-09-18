package io.retailplanet.backend.common.comm.markets;

import javax.ws.rs.*;
import java.util.List;

/**
 * @author w.glanzer, 05.09.2019
 */
public interface IMarketSearchResource
{

  /**
   * Executes an geoSearch over all markets
   *
   * @param pLat      Latitude
   * @param pLon      Longitude
   * @param pDistance Distance in km
   */
  @POST
  List<String> geoSearch(@QueryParam("lat") double pLat, @QueryParam("lon") double pLon, @QueryParam("dist") int pDistance);

}
