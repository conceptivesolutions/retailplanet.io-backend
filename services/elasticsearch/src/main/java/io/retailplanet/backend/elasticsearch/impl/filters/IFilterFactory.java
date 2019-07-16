package io.retailplanet.backend.elasticsearch.impl.filters;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * FilterFactory to create "real" filter objects
 *
 * @author w.glanzer, 16.07.2019
 */
public interface IFilterFactory
{

  /**
   * Interprets a list of filters
   *
   * @param pFilters Filters retrieved by event
   * @return list of "real" filter objects
   */
  @NotNull
  default List<IQueryBuilder> interpretFilters(@Nullable List<Map.Entry<String, String[]>> pFilters) throws Exception
  {
    if (pFilters == null)
      return Collections.emptyList();
    List<IQueryBuilder> result = new ArrayList<>();
    for (Map.Entry<String, String[]> filter : pFilters)
      result.add(interpretFilter(filter.getKey(), filter.getValue()));
    return result;
  }

  /**
   * Interprets a single filter
   *
   * @param pFilterType    Type of the filter
   * @param pFilterDetails Details for the given filter
   * @return Filter, nicht <tt>null</tt>
   */
  @NotNull
  IQueryBuilder interpretFilter(@NotNull String pFilterType, @NotNull String... pFilterDetails) throws Exception;

}
