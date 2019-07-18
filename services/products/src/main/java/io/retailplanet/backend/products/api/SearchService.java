package io.retailplanet.backend.products.api;

import io.retailplanet.backend.common.events.index.DocumentSearchEvent;
import io.retailplanet.backend.common.events.search.*;
import io.retailplanet.backend.products.impl.events.*;
import io.retailplanet.backend.products.impl.filter.*;
import io.retailplanet.backend.products.impl.struct.IIndexStructure;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

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

    // Build request
    DocumentSearchEvent searchEvent = pEvent.createAnswer(DocumentSearchEvent.class)
        .indices(IIndexStructure.INDEX_TYPE)
        .query(_buildIndexQuery(pEvent))
        .offset(pEvent.offset)
        .length(pEvent.length);

    // send
    eventFacade.sendDocumentSearchEvent(searchEvent)
        .map(pResult -> pResult.createAnswer(SearchProductsResultEvent.class) //todo create "real" answer
            .maxSize(pResult.count())
            .elements(pResult.hits()))
        .subscribe(eventFacade::sendSearchProductsResultEvent, pEx -> eventFacade.notifyError(pEvent, pEx));
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
        .matches(DocumentSearchEvent.Match.equal(IIndexStructure.IProduct.NAME, pEvent.query));
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
