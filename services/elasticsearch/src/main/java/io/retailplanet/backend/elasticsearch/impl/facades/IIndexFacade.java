package io.retailplanet.backend.elasticsearch.impl.facades;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.stream.Stream;

/**
 * Facade to proxy all elasticsearch queries
 *
 * @author w.glanzer, 23.06.2019
 */
public interface IIndexFacade
{

  /**
   * Insert / Update a elasticsearch document
   *
   * @param pClientID       ID of the client
   * @param pIndexType      Type of the index
   * @param pContentBuilder Content to insert / update
   */
  void upsertDocument(@NotNull String pClientID, @NotNull String pIndexType, @NotNull Stream<Pair<String, XContentBuilder>> pContentBuilder) throws Exception;

  /**
   * Executes a search in elasticsearch
   *
   * @param pIndexTypes Search in specific indices
   * @param pMatches    Current query, "matches" term
   * @param pFilters    Filter for current query, "filters" term
   * @param pOffset     Page offset or <tt>null</tt>
   * @param pLength     Page length or <tt>null</tt>
   * @return results
   */
  @NotNull
  ISearchResult search(@Nullable List<String> pIndexTypes, @NotNull List<IQueryBuilder> pMatches, @NotNull List<IQueryBuilder> pFilters,
                       @Nullable Integer pOffset, @Nullable Integer pLength) throws Exception;

  /**
   * Inserts a new index. The index will be regenerated, if it already exists and force rebuild is set to <tt>true</tt>
   *
   * @param pClientID     ID of the client
   * @param pIndexType    Type of the index
   * @param pForceRebuild <tt>true</tt> if a rebuild is forced
   */
  void createIndex(@NotNull String pClientID, @NotNull String pIndexType, boolean pForceRebuild) throws Exception;

  /**
   * Determines, if a index with the given name already exists
   *
   * @param pClientID  ID of the client
   * @param pIndexType Type of the index
   * @return <tt>true</tt> if an index exists
   */
  boolean hasIndex(@NotNull String pClientID, @NotNull String pIndexType) throws Exception;

  /**
   * SearchResult
   *
   * @see IIndexFacade#search(List, List, List, Integer, Integer)
   */
  interface ISearchResult
  {
    /**
     * @return the current result page
     */
    @NotNull
    List<Object> getElements();

    /**
     * @return count of all elements (not only the current resultpage)
     */
    long getMaxSize();
  }
}
