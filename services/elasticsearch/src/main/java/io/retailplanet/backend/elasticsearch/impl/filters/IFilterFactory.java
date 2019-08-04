package io.retailplanet.backend.elasticsearch.impl.filters;

import io.retailplanet.backend.common.events.index.DocumentSearchEvent;
import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.jetbrains.annotations.*;
import org.slf4j.LoggerFactory;

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
  default List<IQueryBuilder> interpretFilters(@Nullable List<DocumentSearchEvent.Filter> pFilters) throws Exception
  {
    if (pFilters == null)
      return Collections.emptyList();
    List<IQueryBuilder> result = new ArrayList<>();
    for (DocumentSearchEvent.Filter filter : pFilters)
    {
      String name = filter.name();
      String[] content = filter.content();
      if (name == null || content == null)
        LoggerFactory.getLogger(IFilterFactory.class).info("Skipping filter, because name or content were NULL " + filter);
      else
        result.add(interpretFilter(name, content));
    }
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
