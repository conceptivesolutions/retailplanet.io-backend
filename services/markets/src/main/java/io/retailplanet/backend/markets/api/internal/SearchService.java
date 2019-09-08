package io.retailplanet.backend.markets.api.internal;

import io.retailplanet.backend.common.objects.index.*;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.common.util.i18n.ListUtil;
import io.retailplanet.backend.markets.impl.services.IIndexReadService;
import io.retailplanet.backend.markets.impl.struct.IIndexStructure;
import io.vertx.core.json.JsonArray;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.*;

/**
 * Service: Market search
 *
 * @author w.glanzer, 14.07.2019
 */
@Path("internal/markets/search")
public class SearchService
{

  @Inject
  @RestClient
  private IIndexReadService indexReadService;

  /**
   * Executes an geoSearch over all markets
   *
   * @param pLat      Latitude
   * @param pLon      Longitude
   * @param pDistance Distance in km
   */
  @POST
  public List<String> geoSearch(@QueryParam("lat") double pLat, @QueryParam("lon") double pLon, @QueryParam("dist") int pDistance)
  {
    //search in index
    SearchResult searchResult = indexReadService.search(ListUtil.of(IIndexStructure.INDEX_TYPE), null, null, new Query()
        .filter(Filter.geoDistance(IIndexStructure.IMarket.LOCATION, pLat, pLon, pDistance)));

    // result
    List<String> marketIDs = Collections.emptyList();
    if (searchResult.hits() != null)
    {
      marketIDs = new ArrayList<>();
      JsonArray arr = new JsonArray(searchResult.hits());
      for (int i = 0; i < arr.size(); i++)
      {
        String id = arr.getJsonObject(i).getString(IIndexStructure.IMarket.ID);
        if (!Utility.isNullOrEmptyTrimmedString(id))
          marketIDs.add(id);
      }
    }

    return marketIDs;
  }

}
