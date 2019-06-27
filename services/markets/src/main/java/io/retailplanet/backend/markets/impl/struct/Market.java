package io.retailplanet.backend.markets.impl.struct;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.json.bind.annotation.*;

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

  @JsonbCreator
  public Market()
  {
  }

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
