package io.retailplanet.backend.products.impl.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * @author w.glanzer, 21.06.2019
 */
public class ProductAvailability
{

  @JsonProperty
  private TYPE type;

  @JsonProperty
  private int quantity;

  // Default constructor for reflection purposes
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
