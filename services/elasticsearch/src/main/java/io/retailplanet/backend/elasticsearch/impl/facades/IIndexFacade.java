package io.retailplanet.backend.elasticsearch.impl.facades;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.jetbrains.annotations.NotNull;

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
}
