package io.retailplanet.backend.products.impl.struct;

import com.fasterxml.jackson.annotation.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 21.06.2019
 */
@RegisterForReflection
public class ProductAvailability
{

  @JsonProperty
  public TYPE type;

  @JsonProperty
  public int quantity;

  @JsonCreator
  public ProductAvailability()
  {
  }

  @SuppressWarnings("unused") // JSON
  public enum TYPE
  {
    AVAILABLE,
    ORDERABLE,
    NOT_AVAILABLE
  }

  /**
   * Transforms this availability to a elasticsearch readable json document object
   *
   * @return the content as json object
   */
  @NotNull
  public JsonObject toJSON()
  {
    return new JsonObject()
        .put(IIndexStructure.IAvailability.TYPE, type)
        .put(IIndexStructure.IAvailability.QUANTITY, quantity);
  }

}
