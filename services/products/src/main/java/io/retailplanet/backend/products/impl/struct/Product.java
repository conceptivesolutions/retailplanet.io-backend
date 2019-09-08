package io.retailplanet.backend.products.impl.struct;

import com.google.common.collect.ImmutableMap;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.common.util.i18n.ListUtil;
import org.jetbrains.annotations.NotNull;

import javax.json.bind.annotation.JsonbCreator;
import java.util.*;
import java.util.stream.Collectors;

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
  public String name;

  /**
   * Product ID, not application unique - just within a market
   */
  public String id;

  /**
   * Category
   */
  public String category;

  /**
   * URL for more information
   */
  public String url;

  /**
   * Current price
   */
  public float price;

  /**
   * A list of all preview urls
   */
  public List<String> previews;

  /**
   * Additional information about this product
   */
  public Map<String, String> additionalInfos;

  /**
   * Availability in a specific market
   */
  public Map<String, ProductAvailability> availability;

  /**
   * Timestamp to identify the date, when the product was created
   */
  private long created = System.currentTimeMillis();

  @JsonbCreator
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
  public Map<String, Object> toIndexJSON(@NotNull String pClientID)
  {
    ImmutableMap.Builder<String, Object> productObj = ImmutableMap.builder();

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
          .map(pEntry -> ImmutableMap.builder()
              .put(IIndexStructure.IAdditionalInfo.NAME, pEntry.getKey())
              .put(IIndexStructure.IAdditionalInfo.VALUE, pEntry.getValue())
              .build())
          .collect(Collectors.toList()));

    if (availability != null)
      productObj.put(AVAILABILITY, availability.entrySet().stream()
          .map(pEntry -> ImmutableMap.builder()
              .putAll(pEntry.getValue().toJSON())
              .put(IIndexStructure.IAvailability.MARKETID, pEntry.getKey())
              .build())
          .collect(Collectors.toList()));

    return productObj.build();
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
    Product product = new Product();
    product.name = Utility.getString(pIndexObj, NAME);
    product.id = Utility.getString(pIndexObj, ID);
    product.price = Utility.getFloat(pIndexObj, PRICE, 0F);
    product.created = Utility.getLong(pIndexObj, UPDATED, 0L);
    product.url = Utility.getString(pIndexObj, URL);
    product.category = Utility.getString(pIndexObj, CATEGORY);
    product.previews = Utility.getList(pIndexObj, PREVIEWS, ListUtil.of());
    product.additionalInfos = Utility.getList(pIndexObj, ADDITIONAL_INFO, ListUtil.of()).stream()
        .map(pEntry -> (Map<String, Object>) pEntry)
        .map(pEntry -> {
          String name = Utility.getString(pEntry, IIndexStructure.IAdditionalInfo.NAME);
          String value = Utility.getString(pEntry, IIndexStructure.IAdditionalInfo.VALUE);
          if (Utility.isNullOrEmptyTrimmedString(name))
            return null;
          return new AbstractMap.SimpleImmutableEntry<>(name, value);
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    product.availability = Utility.getList(pIndexObj, AVAILABILITY, ListUtil.of()).stream()
        .map(pEntry -> (Map<String, Object>) pEntry)
        .map(pEntry -> {
          ProductAvailability avail = new ProductAvailability();
          avail.quantity = Utility.getInteger(pEntry, IIndexStructure.IAvailability.QUANTITY, 0);
          avail.type = ProductAvailability.TYPE.valueOf(Utility.getString(pEntry, IIndexStructure.IAvailability.TYPE));
          return new AbstractMap.SimpleImmutableEntry<>(Utility.getString(pEntry, IIndexStructure.IAvailability.MARKETID), avail);
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return product;
  }
}
