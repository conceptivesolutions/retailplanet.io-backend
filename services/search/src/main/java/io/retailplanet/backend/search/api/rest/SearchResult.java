package io.retailplanet.backend.search.api.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.*;

/**
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public final class SearchResult
{

  /**
   * Offset of the result page
   */
  @JsonProperty
  public int offset;

  /**
   * Length / Size of the result page
   */
  @JsonProperty
  public int length;

  /**
   * Maximum count of all results
   */
  @JsonProperty
  public int maxSize;

  /**
   * Map containing all possible filters on the client side (with additional information)
   */
  @JsonProperty
  public Map<String, String[]> filters;

  /**
   * Current result page
   */
  @JsonProperty
  public List<Product> elements;

  /**
   * Describes a product in this result page
   */
  @RegisterForReflection
  public final class Product
  {
    /**
     * Displayable name of the product
     */
    @JsonProperty
    public String name;

    /**
     * Price
     */
    @JsonProperty
    public Float price;

    /**
     * List containing all URLs for the preview pictures
     */
    @JsonProperty
    public List<String> previews;

    /**
     * The markets object gives information about the availability of this product.
     * It contains only those markets, where the products were found and the filters matched.
     */
    @JsonProperty
    public List<Market> markets;

    /**
     * Additional information about this product
     */
    @JsonProperty
    public Map<String, String> infos;
  }

  /**
   * Describes a market where a product was found
   */
  @RegisterForReflection
  public final class Market
  {
    /**
     * Market type, something like "MediaMarkt", "Saturn", etc.
     */
    @JsonProperty
    public String _type;

    /**
     * Displayable name
     */
    @JsonProperty
    public String name;

    /**
     * GPS coordinates in format: "LAT,LNG"
     */
    @JsonProperty
    public String location;

    /**
     * Displayable address
     */
    @JsonProperty
    public String address;

    /**
     * Product availability in this market
     */
    @JsonProperty
    public String availability;

    /**
     * Quantity to define how many products are currently in stock
     */
    @JsonProperty
    public Integer quantity;
  }

}
