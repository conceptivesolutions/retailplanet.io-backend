package io.retailplanet.backend.common.events.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.*;

import java.util.Map;

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
  String query;

  /**
   * Sorting
   */
  @JsonProperty
  String sorting;

  /**
   * Current page offset (count in elements, not pages)
   */
  @JsonProperty
  Integer offset;

  /**
   * Page size
   */
  @JsonProperty
  Integer length;

  /**
   * Desired filters
   */
  @JsonProperty
  Map<String, Object> filter;

  @NotNull
  public SearchProductsEvent query(String pQuery)
  {
    query = pQuery;
    return this;
  }

  /**
   * @return value of 'query' field
   */
  @Nullable
  public String query()
  {
    return query;
  }

  @NotNull
  public SearchProductsEvent sorting(String pSorting)
  {
    sorting = pSorting;
    return this;
  }

  /**
   * @return value of 'sorting' field
   */
  @Nullable
  public String sorting()
  {
    return sorting;
  }

  @NotNull
  public SearchProductsEvent offset(Integer pOffset)
  {
    offset = pOffset;
    return this;
  }

  /**
   * @return value of 'offset' field
   */
  @Nullable
  public Integer offset()
  {
    return offset;
  }

  @NotNull
  public SearchProductsEvent length(Integer pLength)
  {
    length = pLength;
    return this;
  }

  /**
   * @return value of 'length' field
   */
  @Nullable
  public Integer length()
  {
    return length;
  }

  @NotNull
  public SearchProductsEvent filter(JsonObject pFilter)
  {
    filter = pFilter == null ? null : pFilter.getMap();
    return this;
  }

  /**
   * @return value of 'filter' field
   */
  @Nullable
  public Map<String, Object> filter()
  {
    return filter;
  }
}
