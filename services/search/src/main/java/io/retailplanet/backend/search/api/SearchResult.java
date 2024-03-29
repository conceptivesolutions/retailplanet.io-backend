package io.retailplanet.backend.search.api;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jetbrains.annotations.NotNull;

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
  public Integer offset;

  /**
   * Length / Size of the result page
   */
  public Integer length;

  /**
   * Maximum count of all results
   */
  public Long maxSize;

  /**
   * Map containing all possible filters on the client side (with additional information)
   */
  public Map<String, String[]> filters;

  /**
   * Current result page
   */
  public List<Object> elements;

  @NotNull
  public SearchResult offset(Integer pOffset)
  {
    offset = pOffset;
    return this;
  }

  @NotNull
  public SearchResult length(Integer pLength)
  {
    length = pLength;
    return this;
  }

  @NotNull
  public SearchResult maxSize(Long pMaxSize)
  {
    maxSize = pMaxSize;
    return this;
  }

  @NotNull
  public SearchResult filters(Map<String, String[]> pFilters)
  {
    filters = pFilters;
    return this;
  }

  @NotNull
  public SearchResult elements(List<Object> pElements)
  {
    elements = pElements;
    return this;
  }

}
