package io.retailplanet.backend.search.api.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
final class SearchResult
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
  public List<Object> elements;

  @NotNull
  SearchResult offset(int pOffset)
  {
    offset = pOffset;
    return this;
  }

  @NotNull
  SearchResult length(int pLength)
  {
    length = pLength;
    return this;
  }

  @NotNull
  SearchResult maxSize(int pMaxSize)
  {
    maxSize = pMaxSize;
    return this;
  }

  @NotNull
  SearchResult filters(Map<String, String[]> pFilters)
  {
    filters = pFilters;
    return this;
  }

  @NotNull
  SearchResult elements(List<Object> pElements)
  {
    elements = pElements;
    return this;
  }

}
