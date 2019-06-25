package io.retailplanet.backend.markets.impl.struct;

import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.json.bind.annotation.JsonbProperty;

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
  @JsonbProperty
  public String id;

  /**
   * Displayable name of this market
   */
  @JsonbProperty
  public String name;

  /**
   * LAT-Part of the GPS address
   */
  @JsonbProperty
  public float lat;

  /**
   * LNG-Part of the GPS address
   */
  @JsonbProperty
  public float lng;

  /**
   * Displayable address
   */
  @JsonbProperty
  public String address;

  @NotNull
  public JsonObject toIndexJSON(@NotNull String pClientID)
  {
    JsonObject result = new JsonObject()
        .put("id", id)
        .put("clientid", pClientID)
        .put("location", new JsonObject()
            .put("lat", lat)
            .put("lon", lng));

    if (name != null)
      result.put("name", name);

    if (address != null)
      result.put("address", address);

    return result;
  }

}
