package io.retailplanet.backend.products.impl.filter;

import io.retailplanet.backend.common.objects.index.*;
import io.retailplanet.backend.products.impl.services.IMarketSearchService;
import io.retailplanet.backend.products.impl.struct.*;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.stream.Collectors;

import static io.retailplanet.backend.common.objects.index.Match.*;

/**
 * GeoSearch filters the index by lat and lon and availability
 *
 * @author w.glanzer, 14.07.2019
 */
class GeoSearchFilter implements ISearchFilter
{

  private final IMarketSearchService marketSearchService;
  private final List<ProductAvailability.TYPE> availabilities;
  private final double lat;
  private final double lon;
  private final int distance;

  GeoSearchFilter(@NotNull IMarketSearchService pMarketSearchService, @Nullable List<ProductAvailability.TYPE> pAvailabilities, double pLat, double pLon, int pDistance)
  {
    marketSearchService = pMarketSearchService;
    availabilities = pAvailabilities == null || pAvailabilities.isEmpty() ? null : pAvailabilities;
    lat = pLat;
    lon = pLon;
    distance = pDistance;
  }

  @Override
  public void enrichQuery(@NotNull Query pQuery) throws Exception
  {
    List<String> marketIDs = _getMarketIDsWithinCurrentLocation();
    if (marketIDs == null)
      return;

    pQuery.matches(combined(Operator.AND, _createMarketIDsMatch(marketIDs), _createAvailabilityMatch(availabilities))
                       .nested(IIndexStructure.IProduct.AVAILABILITY));
  }

  @Override
  public String toString()
  {
    return "GeoSearchFilter{" +
        "lat=" + lat +
        ", lon=" + lon +
        ", distance=" + distance +
        '}';
  }

  /**
   * @return the marketIDs in current location (within distance)
   */
  @Nullable
  private List<String> _getMarketIDsWithinCurrentLocation() throws Exception
  {
    try
    {
      // we have to block here, because we need the information
      return marketSearchService.geoSearch(lat, lon, distance);
    }
    catch (Exception e)
    {
      throw new Exception("Failed to retrieve market information for geo search filter (" + toString() + ")", e);
    }
  }

  /**
   * @return Creates a match to only query specific market ids
   */
  @NotNull
  private Match _createMarketIDsMatch(@NotNull List<String> pMarketIDs)
  {
    return or(IIndexStructure.IProduct.AVAILABILITY + "." + IIndexStructure.IAvailability.MARKETID, pMarketIDs);
  }

  /**
   * @return Creates an availability match
   */
  @Nullable
  private Match _createAvailabilityMatch(@Nullable List<ProductAvailability.TYPE> pAvailabilities)
  {
    if (pAvailabilities == null)
      return null;

    return or(IIndexStructure.IProduct.AVAILABILITY + "." + IIndexStructure.IAvailability.TYPE, pAvailabilities.stream()
        .map(Enum::name)
        .collect(Collectors.toList()));
  }
}
