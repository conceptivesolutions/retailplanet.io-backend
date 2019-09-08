package io.retailplanet.backend.common.objects.products;

import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Product search returned result
 *
 * @author w.glanzer, 12.07.2019
 */
public class SearchResult
{

  /**
   * Maximum count of all results
   */
  public long maxSize;

  /**
   * Map containing all possible filters on the client side (with additional information)
   */
  public Map<String, String[]> filters;

  /**
   * Current result page
   */
  public List<Object> elements;

  @NotNull
  public SearchResult maxSize(long pMaxSize)
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
  public SearchResult filters(Map<String, String[]> pFilters)
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
  public SearchResult elements(List<Object> pElements)
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
