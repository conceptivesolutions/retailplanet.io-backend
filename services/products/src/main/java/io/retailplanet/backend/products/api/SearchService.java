package io.retailplanet.backend.products.api;

import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.search.*;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.common.util.i18n.ListUtil;
import io.retailplanet.backend.products.impl.events.*;
import io.retailplanet.backend.products.impl.filter.*;
import io.retailplanet.backend.products.impl.struct.*;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service: Product search
 *
 * @author w.glanzer, 12.07.2019
 */
@ApplicationScoped
public class SearchService
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(SearchService.class);

  @Inject
  private IEventFacade eventFacade;

  @Inject
  private ISearchFilterFactory filterFactory;

  /**
   * Executes product search
   *
   * @param pEvent Search event
   */
  @Incoming(IEvents.IN_SEARCH_PRODUCTS)
  public void searchProducts(@Nullable SearchProductsEvent pEvent)
  {
    if (pEvent == null)
      return;

    eventFacade.trace(pEvent, () -> {
      // validate event
      if (Utility.isNullOrEmptyTrimmedString(pEvent.query) || pEvent.offset == null || pEvent.length == null)
      {
        eventFacade.notifyError(pEvent, "Invalid Event");
        return;
      }

      // Build request
      DocumentSearchEvent searchEvent = pEvent.createAnswer(DocumentSearchEvent.class)
          .indices(IIndexStructure.INDEX_TYPE)
          .query(_buildIndexQuery(pEvent))
          .offset(pEvent.offset)
          .length(pEvent.length);

      // send
      // noinspection ResultOfMethodCallIgnored
      eventFacade.sendDocumentSearchEvent(searchEvent)
          .subscribe(pResult -> _answerSearchProductsEvent(pEvent, pResult),
                     pEx -> eventFacade.notifyError(pEvent, pEx));
    });
  }

  /**
   * Answers the SearchProductsEvent with the results of the given DocumentSearchResultEvent
   *
   * @param pSourceEvent                 Source event the user started
   * @param pSearchProductsInIndexResult Search in index
   */
  private void _answerSearchProductsEvent(@NotNull SearchProductsEvent pSourceEvent, @NotNull DocumentSearchResultEvent pSearchProductsInIndexResult)
  {
    long count = pSearchProductsInIndexResult.hits() != null ? Math.max(0, pSearchProductsInIndexResult.count()) : 0;
    List<Object> collect = Utility.notNull(pSearchProductsInIndexResult.hits(), ListUtil::of).stream()
        .map(this::_searchResultToProduct)
        .collect(Collectors.toList());

    // send answer
    eventFacade.sendSearchProductsResultEvent(pSourceEvent.createAnswer(SearchProductsResultEvent.class)
                                                  .filters(new HashMap<>()) //todo filters
                                                  .maxSize(count)
                                                  .elements(collect));
  }

  /**
   * Creates a product of a search result hit
   *
   * @param pSearchResultObj SearchResult-Object
   * @return Product
   */
  @NotNull
  private Product _searchResultToProduct(@NotNull Object pSearchResultObj)
  {
    if (pSearchResultObj instanceof Map)
      return Product.fromIndexJSON((Map<String, Object>) pSearchResultObj);
    throw new IllegalArgumentException("pSearchResult is not an instance of Map");
  }

  /**
   * Creates the request index query to request specific products in index
   *
   * @param pEvent Source event
   * @return Query to use in index request
   */
  @NotNull
  private DocumentSearchEvent.Query _buildIndexQuery(@NotNull SearchProductsEvent pEvent)
  {
    DocumentSearchEvent.Query query = new DocumentSearchEvent.Query()
        .matches(DocumentSearchEvent.Match.equal(IIndexStructure.IProduct.NAME, pEvent.query, DocumentSearchEvent.Operator.OR));
    _enrichWithFilters(query, pEvent.filter);
    return query;
  }

  /**
   * Handles the filters object in SearchProductsEvent
   *
   * @param pQuery   Query to enrich
   * @param pFilters Filters object
   */
  private void _enrichWithFilters(@NotNull DocumentSearchEvent.Query pQuery, @Nullable Map<String, Object> pFilters)
  {
    if (pFilters == null)
      return;

    for (Map.Entry<String, Object> filter : pFilters.entrySet())
    {
      try
      {
        ISearchFilter searchFilter = filterFactory.create(filter.getKey(), filter.getValue());
        searchFilter.enrichQuery(pQuery);
      }
      catch (Exception e)
      {
        _LOGGER.warn("Filter could not be interpreted", e);
      }
    }
  }

}
