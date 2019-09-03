package io.retailplanet.backend.products.impl.struct;

import com.fasterxml.jackson.annotation.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.util.Utility;
import io.vertx.core.json.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.*;

import static io.retailplanet.backend.products.impl.struct.IIndexStructure.IProduct.*;

/**
 * Contains all information about a single product (incl. all availability information)
 *
 * @author w.glanzer, 21.06.2019
 */
@RegisterForReflection
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

  @JsonCreator
  public Product()
  {
  }

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
      productObj.put(ADDITIONAL_INFO, additionalInfos.entrySet().stream()
          .map(pEntry -> new JsonObject()
              .put(IIndexStructure.IAdditionalInfo.NAME, pEntry.getKey())
              .put(IIndexStructure.IAdditionalInfo.VALUE, pEntry.getValue()))
          .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

    if (availability != null)
      productObj.put(AVAILABILITY, availability.entrySet().stream()
          .map(pEntry -> pEntry.getValue().toJSON()
              .put(IIndexStructure.IAvailability.MARKETID, pEntry.getKey()))
          .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

    return productObj;
  }

  /**
   * Creates a product of a search result hit
   *
   * @param pIndexObj SearchResult-Object
   * @return Product
   */
  @NotNull
  public static Product fromIndexJSON(@NotNull Map<String, Object> pIndexObj)
  {
    JsonObject index = new JsonObject(pIndexObj);
    Product product = new Product();
    product.name = index.getString(NAME);
    product.id = index.getString(ID);
    product.price = index.getFloat(PRICE, 0F);
    product.created = index.getInteger(UPDATED, 0);
    product.url = index.getString(URL);
    product.category = index.getString(CATEGORY);
    product.previews = index.getJsonArray(PREVIEWS, new JsonArray()).getList();
    product.additionalInfos = index.getJsonArray(ADDITIONAL_INFO, new JsonArray()).stream()
        .map(JsonObject.class::cast)
        .map(pEntry -> {
          String name = pEntry.getString(IIndexStructure.IAdditionalInfo.NAME);
          String value = pEntry.getString(IIndexStructure.IAdditionalInfo.VALUE);
          if (Utility.isNullOrEmptyTrimmedString(name))
            return null;
          return new AbstractMap.SimpleImmutableEntry<>(name, value);
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    product.availability = index.getJsonArray(AVAILABILITY, new JsonArray()).stream()
        .map(JsonObject.class::cast)
        .map(pEntry -> {
          ProductAvailability avail = new ProductAvailability();
          avail.quantity = pEntry.getInteger(IIndexStructure.IAvailability.QUANTITY, 0);
          avail.type = ProductAvailability.TYPE.valueOf(pEntry.getString(IIndexStructure.IAvailability.TYPE));
          return new AbstractMap.SimpleImmutableEntry<>(pEntry.getString(IIndexStructure.IAvailability.MARKETID), avail);
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return product;
  }
}
