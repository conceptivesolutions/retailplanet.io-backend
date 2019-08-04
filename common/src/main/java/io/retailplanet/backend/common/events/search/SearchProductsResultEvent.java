package io.retailplanet.backend.common.events.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Event: Product search returned result
 *
 * @author w.glanzer, 12.07.2019
 */
@RegisterForReflection
public class SearchProductsResultEvent extends AbstractEvent<SearchProductsResultEvent>
{

  /**
   * Maximum count of all results
   */
  @JsonProperty
  long maxSize;

  /**
   * Map containing all possible filters on the client side (with additional information)
   */
  @JsonProperty
  Map<String, String[]> filters;

  /**
   * Current result page
   */
  @JsonProperty
  List<Object> elements;

  @NotNull
  public SearchProductsResultEvent maxSize(long pMaxSize)
  {
    maxSize = pMaxSize;
    return this;
  }

  /**
   * @return value of 'maxSize' field
   */
  public long maxSize()
  {
    return maxSize;
  }

  @NotNull
  public SearchProductsResultEvent filters(Map<String, String[]> pFilters)
  {
    filters = pFilters;
    return this;
  }

  /**
   * @return value of 'filters' field
   */
  @Nullable
  public Map<String, String[]> filters()
  {
    return filters;
  }

  @NotNull
  public SearchProductsResultEvent elements(List<Object> pElements)
  {
    elements = pElements;
    return this;
  }

  /**
   * @return value of 'elements' field
   */
  @Nullable
  public List<Object> elements()
  {
    return elements;
  }

}
