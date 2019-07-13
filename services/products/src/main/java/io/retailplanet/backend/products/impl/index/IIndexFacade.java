package io.retailplanet.backend.products.impl.index;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.reactivex.Single;
import io.retailplanet.backend.products.impl.struct.Product;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Facade to proxy all index requests
 *
 * @author w.glanzer, 22.06.2019
 */
public interface IIndexFacade
{

  /**
   * Inserts / Updates products in elasticsearch
   *
   * @param pClientID    Client from where the products came
   * @param pProductList List of all products to upsert
   */
  void upsertProducts(@NotNull String pClientID, @NotNull Product[] pProductList);

  /**
   * Queries products from elasticsearch
   *
   * @param pQuery   Search query
   * @param pSort    sorting
   * @param pOffset  offset
   * @param pLength  length
   * @param pFilters all available filters
   */
  @NotNull
  Single<SearchResult> searchProducts(@NotNull String pQuery, @Nullable String pSort, @Nullable Integer pOffset, @Nullable Integer pLength,
                                      @Nullable Map<String, Object> pFilters);

  /**
   * Result
   */
  @RegisterForReflection
  final class SearchResult
  {

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
    public List<Product> elements;

  }

}
