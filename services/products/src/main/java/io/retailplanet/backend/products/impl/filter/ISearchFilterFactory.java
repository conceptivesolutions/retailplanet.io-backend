package io.retailplanet.backend.products.impl.filter;

import org.jetbrains.annotations.NotNull;

/**
 * Factory to create ISearchFilters, especially for the SearchService
 *
 * @author w.glanzer, 17.07.2019
 * @see ISearchFilter
 * @see io.retailplanet.backend.products.api.SearchService
 */
public interface ISearchFilterFactory
{

  /**
   * Creates a new SearchFilter instance
   *
   * @param pType           Filter-Type
   * @param pArgumentObject Object, from SearchProductsEvent
   * @return the Filter
   * @see io.retailplanet.backend.common.events.search.SearchProductsEvent
   */
  @NotNull
  ISearchFilter create(@NotNull String pType, @NotNull Object pArgumentObject) throws Exception;

}
