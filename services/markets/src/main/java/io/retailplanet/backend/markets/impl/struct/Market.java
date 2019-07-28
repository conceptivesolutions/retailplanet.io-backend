package io.retailplanet.backend.markets.impl.struct;

import com.fasterxml.jackson.annotation.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single market
 *
 * @author w.glanzer, 23.06.2019
 */
@RegisterForReflection
public class Market
{

  /**
   * Uniquie identifier
   */
  @JsonProperty
  public String id;

  /**
   * Displayable name of this market
   */
  @JsonProperty
  public String name;

  /**
   * LAT-Part of the GPS address
   */
  @JsonProperty
  public float lat;

  /**
   * LNG-Part of the GPS address
   */
  @JsonProperty
  public float lng;

  /**
   * Displayable address
   */
  @JsonProperty
  public String address;

  @JsonCreator
  public Market()
  {
  }

  @NotNull
  public JsonObject toIndexJSON(@NotNull String pClientID)
  {
    JsonObject result = new JsonObject()
        .put(IIndexStructure.IMarket.ID, id)
        .put(IIndexStructure.IMarket.CLIENTID, pClientID)
        .put(IIndexStructure.IMarket.LOCATION, new JsonObject()
            .put("lat", lat)
            .put("lon", lng));

    if (name != null)
      result.put(IIndexStructure.IMarket.NAME, name);

    if (address != null)
      result.put(IIndexStructure.IMarket.ADDRESS, address);

    return result;
  }

}
