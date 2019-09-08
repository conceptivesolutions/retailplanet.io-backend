package io.retailplanet.backend.common.objects.index;

import org.jetbrains.annotations.*;

import javax.json.bind.annotation.*;

/**
 * Contains all necessary information about any filter methods
 *
 * @author w.glanzer, 08.09.2019
 */
public class Filter
{
  @JsonbProperty
  String name;

  @JsonbProperty
  String[] content;

  @JsonbCreator
  Filter()
  {
  }

  private Filter(@NotNull String pName, @NotNull String... pContent)
  {
    name = pName;
    content = pContent;
  }

  /**
   * @return value of 'name' field
   */
  @Nullable
  public String name()
  {
    return name;
  }

  /**
   * @return value of 'content' field
   */
  @Nullable
  public String[] content()
  {
    return content;
  }

  /**
   * Creates a new geo_distance filter to filter hits by distance
   *
   * @param pLocationFieldName Name of the field in the index, that should be filtered
   * @param pLat               Latitude
   * @param pLon               Longitude
   * @param pDistance          Distance in kilometers
   * @return the filter
   */
  @NotNull
  public static Filter geoDistance(@NotNull String pLocationFieldName, double pLat, double pLon, int pDistance)
  {
    return new Filter("geo_distance", pLocationFieldName, String.valueOf(pLat), String.valueOf(pLon), String.valueOf(pDistance));
  }

}
