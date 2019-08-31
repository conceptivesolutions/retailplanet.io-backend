package io.retailplanet.backend.clients.base.api;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * @author w.glanzer, 31.08.2019
 */
public class CrawledProduct
{

  public static final String CATEGORY_DELIMITER = "/";

  private String name;
  private String id;
  private String category;
  private String url;
  private float price;
  private Set<String> previews;
  private Map<String, String> additionalInfos;
  private Map<String, Pair<Availability, Integer>> availability;

  public CrawledProduct()
  {
  }

  @NotNull
  public String getName()
  {
    return name;
  }

  @NotNull
  public CrawledProduct name(@NotNull String pName)
  {
    name = pName;
    return this;
  }

  @NotNull
  public String getID()
  {
    return id;
  }

  @NotNull
  public CrawledProduct id(@NotNull String pID)
  {
    id = pID;
    return this;
  }

  @Nullable
  public String getCategory()
  {
    return category;
  }

  @NotNull
  public CrawledProduct category(@Nullable String pCategory)
  {
    category = pCategory;
    return this;
  }

  @Nullable
  public String getURL()
  {
    return url;
  }

  @NotNull
  public CrawledProduct url(@Nullable String pUrl)
  {
    url = pUrl;
    return this;
  }

  public float getPrice()
  {
    return price;
  }

  @NotNull
  public CrawledProduct price(float pPrice)
  {
    price = pPrice;
    return this;
  }

  @NotNull
  public Set<String> getPreviews()
  {
    return previews == null ? new HashSet<>() : previews;
  }

  @NotNull
  public CrawledProduct previews(@Nullable Set<String> pPreviews)
  {
    previews = pPreviews;
    return this;
  }

  @NotNull
  public Map<String, String> getAdditionalInfos()
  {
    return additionalInfos == null ? new HashMap<>() : additionalInfos;
  }

  @NotNull
  public CrawledProduct additionalInfos(@Nullable Map<String, String> pAdditionalInfos)
  {
    additionalInfos = pAdditionalInfos;
    return this;
  }

  @NotNull
  public Map<String, Pair<Availability, Integer>> getAvailability()
  {
    return availability == null ? new HashMap<>() : availability;
  }

  @NotNull
  public CrawledProduct availability(@NotNull String pMarket, @NotNull Availability pAvailability, @Nullable Integer pQuantity)
  {
    if (availability == null)
      availability = new HashMap<>();
    availability.put(pMarket, Pair.of(pAvailability, pQuantity));
    return this;
  }

  @Override
  public String toString()
  {
    return "CrawledProduct{" +
        "name='" + name + '\'' +
        ", id='" + id + '\'' +
        ", category='" + category + '\'' +
        ", url='" + url + '\'' +
        ", price=" + price +
        ", previews=" + previews +
        ", additionalInfos=" + additionalInfos +
        ", availability=" + availability +
        '}';
  }

  /**
   * Availability, if a product is available or not
   */
  public enum Availability
  {
    AVAILABLE,
    ORDERABLE,
    NOT_AVAILABLE
  }

}
