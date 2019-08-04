package io.retailplanet.backend.common.events.index;

import com.fasterxml.jackson.annotation.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

/**
 * Event: Document should be searched in index
 *
 * @author w.glanzer, 12.07.2019
 */
@RegisterForReflection
public class DocumentSearchEvent extends AbstractEvent<DocumentSearchEvent>
{

  /**
   * Query for the current search
   */
  @JsonProperty(required = true)
  Query query;

  /**
   * Optional list of all index types to search
   */
  @JsonProperty
  List<String> indexTypes;

  /**
   * Desired offset of the result page
   */
  @JsonProperty(defaultValue = "0")
  Integer offset;

  /**
   * Desired length of the result page
   */
  @JsonProperty(defaultValue = "20")
  Integer length;

  /**
   * Sets the "indexTypes" field
   *
   * @param pTypes Types for the indexTypes field
   * @return Builder
   */
  @NotNull
  public DocumentSearchEvent indices(String... pTypes)
  {
    indexTypes = Arrays.asList(pTypes);
    return this;
  }

  /**
   * @return value of 'indexTypes' field
   */
  @Nullable
  public List<String> indexTypes()
  {
    return indexTypes;
  }

  /**
   * Sets the "query" field
   *
   * @param pQuery Query instance
   * @return Builder
   */
  @NotNull
  public DocumentSearchEvent query(Query pQuery)
  {
    query = pQuery;
    return this;
  }

  /**
   * @return value of 'query' field
   */
  @Nullable
  public Query query()
  {
    return query;
  }

  /**
   * Sets the "offset" field
   *
   * @param pOffset Offset as integer
   * @return Builder
   */
  @NotNull
  public DocumentSearchEvent offset(Integer pOffset)
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

  /**
   * Sets the "length" field
   *
   * @param pLength length as integer
   * @return Builder
   */
  @NotNull
  public DocumentSearchEvent length(Integer pLength)
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

  /**
   * Query type
   */
  @RegisterForReflection
  public static class Query
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

  /**
   * Contains all necessary information about any match methods
   */
  @RegisterForReflection
  public static class Match
  {
    @JsonProperty
    String name;

    @JsonProperty
    String[] content;

    @JsonProperty
    String nestedPath;

    @JsonProperty
    List<Match> innerMatches;

    @JsonCreator
    Match()
    {
    }

    private Match(@NotNull String pName, @NotNull String... pContent)
    {
      name = pName;
      content = pContent;
    }

    /**
     * @return value of 'innerMatches' field
     */
    @Nullable
    public List<Match> innerMatches()
    {
      return innerMatches;
    }

    /**
     * @return value of 'name' field
     */
    @Nullable
    public String name()
    {
      return name;
    }

    /**
     * @return value of 'content' field
     */
    @Nullable
    public String[] content()
    {
      return content;
    }

    /**
     * @return value of 'nestedPath' field
     */
    @Nullable
    public String nestedPath()
    {
      return nestedPath;
    }

    /**
     * Mark this match as "nested document" match
     *
     * @param pPath specifies the nested path
     * @return Builder
     */
    @NotNull
    public Match nested(@NotNull String pPath)
    {
      nestedPath = pPath;
      return this;
    }

    /**
     * Creates an equals match to query elasticsearch by fieldname
     *
     * @param pFieldName  Name of the searched field
     * @param pFieldValue Searched value
     * @param pOperator   Operator to define how single words are handled. AND = all words must match, OR = One word must match
     * @return the match
     */
    @NotNull
    public static Match equal(@NotNull String pFieldName, @NotNull String pFieldValue, @NotNull Operator pOperator)
    {
      return new Match("eq", pFieldName, pFieldValue, pOperator.name());
    }

    /**
     * Creates an or match query to define something like FIELD=XX or FIELD=YY
     *
     * @param pFieldName   Name of the field
     * @param pFieldValues Possible values
     * @return Match
     */
    @NotNull
    public static Match or(@NotNull String pFieldName, @NotNull List<String> pFieldValues)
    {
      Match match = new Match("or");
      List<String> values = new ArrayList<>(pFieldValues);
      values.add(0, pFieldName);
      match.content = values.toArray(new String[0]);
      return match;
    }

    /**
     * Combine matches to a single match
     *
     * @param pOperator Operator to define, how combinations are combined
     * @param pMatches  All matches
     * @return Builder
     */
    @NotNull
    public static Match combined(@NotNull Operator pOperator, Match... pMatches)
    {
      Match match = new Match("combined", pOperator.name());
      match.innerMatches = Stream.of(pMatches)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
      return match;
    }
  }

  /**
   * Contains all necessary information about any filter methods
   */
  @RegisterForReflection
  public static class Filter
  {
    @JsonProperty
    String name;

    @JsonProperty
    String[] content;

    @JsonCreator
    Filter()
    {
    }

    private Filter(@NotNull String pName, @NotNull String... pContent)
    {
      name = pName;
      content = pContent;
    }

    /**
     * @return value of 'name' field
     */
    @Nullable
    public String name()
    {
      return name;
    }

    /**
     * @return value of 'content' field
     */
    @Nullable
    public String[] content()
    {
      return content;
    }

    /**
     * Creates a new geo_distance filter to filter hits by distance
     *
     * @param pLocationFieldName Name of the field in the index, that should be filtered
     * @param pLat               Latitude
     * @param pLon               Longitude
     * @param pDistance          Distance in kilometers
     * @return the filter
     */
    @NotNull
    public static Filter geoDistance(@NotNull String pLocationFieldName, double pLat, double pLon, int pDistance)
    {
      return new Filter("geo_distance", pLocationFieldName, String.valueOf(pLat), String.valueOf(pLon), String.valueOf(pDistance));
    }
  }

  /**
   * Common "operator", to define contexts
   */
  public enum Operator
  {
    AND,
    OR
  }

}
