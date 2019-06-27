package io.retailplanet.backend.products.impl.struct;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.json.bind.annotation.*;

/**
 * @author w.glanzer, 21.06.2019
 */
@RegisterForReflection
public class ProductAvailability
{

  @JsonbProperty
  public TYPE type;

  @JsonbProperty
  public int quantity;

  @JsonbCreator
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
