package io.retailplanet.backend.products.impl.filter;

import io.retailplanet.backend.common.events.index.DocumentSearchEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a filter, which enriches the elasticsearch query
 *
 * @author w.glanzer, 17.07.2019
 */
public interface ISearchFilter
{

  /**
   * Enricht the index query
   *
   * @param pQuery index query
   */
  void enrichQuery(@NotNull DocumentSearchEvent.Query pQuery) throws Exception;

}
