package io.retailplanet.backend.common.events.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.*;

/**
 * Event which will be fired, when markets are searched
 *
 * @author w.glanzer, 14.07.2019
 */
@RegisterForReflection
public class SearchMarketsEvent extends AbstractEvent<SearchMarketsEvent>
{

  /**
   * should be set, if markets are searched by geo location search
   */
  @JsonProperty
  Geo geoSearch;

  @NotNull
  public SearchMarketsEvent withGeoSearch(Geo pCoordinates)
  {
    geoSearch = pCoordinates;
    return this;
  }

  /**
   * @return value of 'geoSearch' field
   */
  @Nullable
  public Geo geoSearch()
  {
    return geoSearch;
  }

  /**
   * Geo Location object, containing latitude, longitude and distance
   */
  @RegisterForReflection
  public static class Geo
  {

    /**
     * Latitude
     */
    @JsonProperty
    double lat;

    /**
     * Longitude
     */
    @JsonProperty
    double lon;

    /**
     * Distance in kilometers
     */
    @JsonProperty
    int distance;

    @NotNull
    public Geo lat(double pLat)
    {
      lat = pLat;
      return this;
    }

    /**
     * @return value of 'lat' field
     */
    public double lat()
    {
      return lat;
    }

    @NotNull
    public Geo lon(double pLng)
    {
      lon = pLng;
      return this;
    }

    /**
     * @return value of 'lon' field
     */
    public double lon()
    {
      return lon;
    }

    @NotNull
    public Geo distance(int pDistance)
    {
      distance = pDistance;
      return this;
    }

    /**
     * @return value of 'distance' field
     */
    public int distance()
    {
      return distance;
    }
  }

}
