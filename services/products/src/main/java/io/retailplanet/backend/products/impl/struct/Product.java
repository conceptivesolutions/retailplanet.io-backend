package io.retailplanet.backend.products.impl.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
  private String name;

  /**
   * Product ID, not application unique - just within a market
   */
  @JsonProperty
  private String id;

  /**
   * Category
   */
  @JsonProperty
  private String category;

  /**
   * URL for more information
   */
  @JsonProperty
  private String url;

  /**
   * Current price
   */
  @JsonProperty
  private float price;

  /**
   * A list of all preview urls
   */
  @JsonProperty
  private List<String> previews;

  /**
   * Additional information about this product
   */
  @JsonProperty
  private Map<String, String> additionalInfos;

  /**
   * Availability in a specific market
   */
  @JsonProperty
  private Map<String, ProductAvailability> availability;

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
  public JsonObject toJSON(@NotNull String pClientID)
  {
    JsonObject productObj = new JsonObject();

    productObj.put(NAME, name);
    productObj.put(ID, id);
    productObj.put(CLIENT_ID, pClientID);
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

    JsonObject availObj = new JsonObject();
    availability.forEach((pMarketID, pAvailability) -> availObj.put(pMarketID, pAvailability.toJSON()));
    productObj.put(AVAILABILITY, availObj);

    return productObj;
  }
}
