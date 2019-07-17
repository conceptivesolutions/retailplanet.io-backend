package io.retailplanet.backend.elasticsearch.impl.filters;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.jetbrains.annotations.*;

/**
 * Filter for the geo_distance field in elasticsearch
 *
 * @author w.glanzer, 16.07.2019
 */
class GeoDistanceFilter implements IQueryBuilder
{

  static final String TYPE = "geo_distance";

  private final String fieldName;
  private final double lat;
  private final double lon;
  private final int distance;

  GeoDistanceFilter(@NotNull String pFieldName, double pLat, double pLon, int pDistance)
  {
    fieldName = pFieldName;
    lat = pLat;
    lon = pLon;
    distance = pDistance;
  }

  @Nullable
  @Override
  public QueryBuilder toQueryBuilder()
  {
    return QueryBuilders.geoDistanceQuery(fieldName)
        .point(lat, lon)
        .distance(distance, DistanceUnit.KILOMETERS);
  }

}
