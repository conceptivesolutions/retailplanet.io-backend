package io.retailplanet.backend.products.impl.filter;

import org.jetbrains.annotations.NotNull;

/**
 * Factory to create ISearchFilters, especially for the SearchService
 *
 * @author w.glanzer, 17.07.2019
 * @see ISearchFilter
 */
public interface ISearchFilterFactory
{

  /**
   * Creates a new SearchFilter instance
   *
   * @param pType           Filter-Type
   * @param pArgumentObject Object, from SearchProductsEvent
   * @return the Filter
   */
  @NotNull
  ISearchFilter create(@NotNull String pType, @NotNull Object pArgumentObject) throws Exception;

}
