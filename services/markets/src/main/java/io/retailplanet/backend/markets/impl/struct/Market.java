package io.retailplanet.backend.markets.impl.struct;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import javax.json.bind.annotation.JsonbCreator;
import java.util.*;

/**
 * Represents a single market
 *
 * @author w.glanzer, 23.06.2019
 */
public class Market
{

  /**
   * Uniquie identifier
   */
  public String id;

  /**
   * Displayable name of this market
   */
  public String name;

  /**
   * LAT-Part of the GPS address
   */
  public float lat;

  /**
   * LNG-Part of the GPS address
   */
  public float lng;

  /**
   * Displayable address
   */
  public String address;

  @JsonbCreator
  public Market()
  {
  }

  @NotNull
  public Map<String, Object> toIndexJSON(@NotNull String pClientID)
  {
    Map<String, Object> result = new HashMap<>(ImmutableMap.<String, Object>builder()
        .put(IIndexStructure.IMarket.ID, id)
        .put(IIndexStructure.IMarket.CLIENTID, pClientID)
        .put(IIndexStructure.IMarket.LOCATION, ImmutableMap.builder()
            .put("lat", lat)
            .put("lon", lng)
            .build()).build());

    if (name != null)
      result.put(IIndexStructure.IMarket.NAME, name);

    if (address != null)
      result.put(IIndexStructure.IMarket.ADDRESS, address);

    return result;
  }

}
