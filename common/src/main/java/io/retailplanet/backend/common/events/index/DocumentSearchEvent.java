package io.retailplanet.backend.common.events.index;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
     * We need to use a delimiter in match/filter type name, to define nested documents
     */
    public static final String NESTED_DELIMITER = "//";

    /**
     * This map contains all "match" queries.
     * If no matches are given, the "match_all" term is used.
     */
    @JsonProperty
    List<Map.Entry<String, String[]>> matches;

    /**
     * This list contains all filters which should be used to filter the result type
     */
    @JsonProperty
    List<Map.Entry<String, String[]>> filters;

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
      String id = pMatch.name;
      if (pMatch.nestedPath != null)
        id = pMatch.nestedPath + NESTED_DELIMITER + id;
      matches.add(new AbstractMap.SimpleEntry<>(id, pMatch.content));
      return this;
    }

    /**
     * @return value of 'matches' field
     */
    public List<Map.Entry<String, String[]>> matches()
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
      filters.add(new AbstractMap.SimpleEntry<>(pFilter.name, pFilter.content));
      return this;
    }

    /**
     * @return value of 'filters' field
     */
    public List<Map.Entry<String, String[]>> filters()
    {
      return filters;
    }
  }

  /**
   * Contains all necessary information about any match methods
   */
  public static class Match
  {
    private String name;
    private String[] content;
    private String nestedPath;

    private Match(@NotNull String pName, @NotNull String... pContent)
    {
      name = pName;
      content = pContent;
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
     * @return the match
     */
    @NotNull
    public static Match equal(@NotNull String pFieldName, @NotNull String pFieldValue)
    {
      return new Match("eq", pFieldName, pFieldValue);
    }
  }

  /**
   * Contains all necessary information about any filter methods
   */
  public static class Filter
  {
    private String name;
    private String[] content;

    private Filter(@NotNull String pName, @NotNull String... pContent)
    {
      name = pName;
      content = pContent;
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

}
