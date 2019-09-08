package io.retailplanet.backend.common.objects.index;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Simple POJO for the searchQuery in API Calls
 *
 * @author w.glanzer, 08.09.2019
 */
public class Query
{
  /**
   * This map contains all "match" queries.
   * If no matches are given, the "match_all" term is used.
   */
  @JsonProperty
  List<Match> matches;

  /**
   * This list contains all filters which should be used to filter the result type
   */
  @JsonProperty
  List<Filter> filters;

  /**
   * Adds a new match to be used in this query term
   *
   * @param pMatch Match
   * @return Builder
   */
  @NotNull
  public Query matches(@NotNull Match pMatch)
  {
    if (matches == null)
      matches = new ArrayList<>();
    matches.add(pMatch);
    return this;
  }

  /**
   * @return value of 'matches' field
   */
  @Nullable
  public List<Match> matches()
  {
    return matches;
  }

  /**
   * Adds a new filter to be used in this query term
   *
   * @param pFilter Filter instance
   * @return Builder
   */
  @NotNull
  public Query filter(@NotNull Filter pFilter)
  {
    if (filters == null)
      filters = new ArrayList<>();
    filters.add(pFilter);
    return this;
  }

  /**
   * @return value of 'filters' field
   */
  @Nullable
  public List<Filter> filters()
  {
    return filters;
  }
}
