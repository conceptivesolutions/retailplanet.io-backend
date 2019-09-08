package io.retailplanet.backend.search.api;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jetbrains.annotations.NotNull;

import javax.json.bind.annotation.JsonbProperty;
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
  @JsonbProperty
  public Integer offset;

  /**
   * Length / Size of the result page
   */
  @JsonbProperty
  public Integer length;

  /**
   * Maximum count of all results
   */
  @JsonbProperty
  public Long maxSize;

  /**
   * Map containing all possible filters on the client side (with additional information)
   */
  @JsonbProperty
  public Map<String, String[]> filters;

  /**
   * Current result page
   */
  @JsonbProperty
  public List<Object> elements;

  @NotNull
  SearchResult offset(Integer pOffset)
  {
    offset = pOffset;
    return this;
  }

  @NotNull
  SearchResult length(Integer pLength)
  {
    length = pLength;
    return this;
  }

  @NotNull
  SearchResult maxSize(Long pMaxSize)
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
