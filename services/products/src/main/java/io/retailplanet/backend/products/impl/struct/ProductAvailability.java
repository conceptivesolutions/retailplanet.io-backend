package io.retailplanet.backend.products.impl.struct;

import com.google.common.collect.ImmutableMap;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jetbrains.annotations.NotNull;

import javax.json.bind.annotation.JsonbCreator;
import java.util.Map;

/**
 * @author w.glanzer, 21.06.2019
 */
@RegisterForReflection
public class ProductAvailability
{

  public TYPE type;

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
  public Map<String, Object> toJSON()
  {
    return ImmutableMap.<String, Object>builder()
        .put(IIndexStructure.IAvailability.TYPE, type)
        .put(IIndexStructure.IAvailability.QUANTITY, quantity)
        .build();
  }

}
