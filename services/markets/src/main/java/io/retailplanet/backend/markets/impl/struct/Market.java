package io.retailplanet.backend.markets.impl.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

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
