package io.retailplanet.backend.products.impl.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collector;

import static io.retailplanet.backend.products.impl.struct.IIndexStructure.IProduct.*;

/**
 * Contains all information about a single product (incl. all availability information)
 *
 * @author w.glanzer, 21.06.2019
 */
public class Product
{

  /**
   * Name of the product
   */
  @JsonProperty
  public String name;

  /**
   * Product ID, not application unique - just within a market
   */
  @JsonProperty
  public String id;

  /**
   * Category
   */
  @JsonProperty
  public String category;

  /**
   * URL for more information
   */
  @JsonProperty
  public String url;

  /**
   * Current price
   */
  @JsonProperty
  public float price;

  /**
   * A list of all preview urls
   */
  @JsonProperty
  public List<String> previews;

  /**
   * Additional information about this product
   */
  @JsonProperty
  public Map<String, String> additionalInfos;

  /**
   * Availability in a specific market
   */
  @JsonProperty
  public Map<String, ProductAvailability> availability;

  /**
   * Timestamp to identify the date, when the product was created
   */
  private long created = System.currentTimeMillis();

  /**
   * Transforms this product to a elasticsearch readable json document object
   *
   * @param pClientID id of the client this product comes from
   * @return the content as json object
   */
  @NotNull
  public JsonObject toIndexJSON(@NotNull String pClientID)
  {
    JsonObject productObj = new JsonObject();

    productObj.put(NAME, name);
    productObj.put(ID, id);
    productObj.put(CLIENTID, pClientID);
    productObj.put(PRICE, price);
    productObj.put(UPDATED, created);

    if (url != null)
      productObj.put(URL, url);

    if (category != null)
      productObj.put(CATEGORY, category);

    if (previews != null)
      productObj.put(PREVIEWS, previews);

    if (additionalInfos != null)
      productObj.put(ADDITIONAL_INFO, additionalInfos);

    productObj.put(AVAILABILITY, availability.entrySet().stream()
        .map(pEntry -> pEntry.getValue().toJSON()
            .put(IIndexStructure.IAvailability.MARKETID, pEntry.getKey()))
        .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

    return productObj;
  }
}
