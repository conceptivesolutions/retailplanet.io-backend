package io.retailplanet.backend.common.events.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * Event that will be fired, when a product search was started
 *
 * @author w.glanzer, 12.07.2019
 */
@RegisterForReflection
public class SearchProductsEvent extends AbstractEvent<SearchProductsEvent>
{

  /**
   * Search query
   */
  @JsonProperty
  public String query;

  /**
   * Sorting
   */
  @JsonProperty
  public String sorting;

  /**
   * Current page offset (count in elements, not pages)
   */
  @JsonProperty
  public Integer offset;

  /**
   * Page size
   */
  @JsonProperty
  public Integer length;

  /**
   * Desired filters
   */
  @JsonProperty
  public JsonObject filter;

  @NotNull
  public SearchProductsEvent query(String pQuery)
  {
    query = pQuery;
    return this;
  }

  @NotNull
  public SearchProductsEvent sorting(String pSorting)
  {
    sorting = pSorting;
    return this;
  }

  @NotNull
  public SearchProductsEvent offset(Integer pOffset)
  {
    offset = pOffset;
    return this;
  }

  @NotNull
  public SearchProductsEvent length(Integer pLength)
  {
    length = pLength;
    return this;
  }

  @NotNull
  public SearchProductsEvent filter(JsonObject pFilter)
  {
    filter = pFilter;
    return this;
  }
}
