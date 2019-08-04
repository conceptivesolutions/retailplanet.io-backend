package io.retailplanet.backend.markets.api;

import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.market.*;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.markets.impl.events.*;
import io.retailplanet.backend.markets.impl.struct.IIndexStructure;
import io.vertx.core.json.JsonArray;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

/**
 * Service: Market search
 *
 * @author w.glanzer, 14.07.2019
 */
@ApplicationScoped
public class SearchService
{

  @Inject
  private IEventFacade eventFacade;

  @Incoming(IEvents.IN_MARKETS_SEARCH)
  public void searchMarkets(@Nullable SearchMarketsEvent pEvent)
  {
    if (pEvent == null)
      return;

    eventFacade.trace(pEvent, () -> {
      DocumentSearchEvent searchEvent;

      if (pEvent.geoSearch() != null)
        searchEvent = _createGeoSearchEvent(pEvent, pEvent.geoSearch());
      else
      {
        eventFacade.notifyError(pEvent, new IllegalArgumentException("Failed to parse search event " + pEvent + ". No search pattern matched."));
        return;
      }

      eventFacade.sendDocumentSearchEvent(searchEvent)
          .subscribe(pResult -> eventFacade.sendSearchMarketsResultEvent(_createResultEvent(pEvent, pResult)), pEx -> eventFacade.notifyError(pEvent, pEx));
    });
  }

  /**
   * Creates a new resultEvent, if a DocumentSearchResult was successful
   *
   * @param pSourceEvent  Search event
   * @param pSearchResult Document search result
   * @return resultEvent
   */
  @NotNull
  private SearchMarketsResultEvent _createResultEvent(@NotNull SearchMarketsEvent pSourceEvent, @NotNull DocumentSearchResultEvent pSearchResult)
  {
    List<String> marketIDs = Collections.emptyList();
    if (pSearchResult.hits() != null)
    {
      marketIDs = new ArrayList<>();
      JsonArray arr = new JsonArray(pSearchResult.hits());
      for (int i = 0; i < arr.size(); i++)
      {
        String id = arr.getJsonObject(i).getString(IIndexStructure.IMarket.ID);
        if (!Utility.isNullOrEmptyTrimmedString(id))
          marketIDs.add(id);
      }
    }

    return pSourceEvent.createAnswer(SearchMarketsResultEvent.class)
        .marketIDs(marketIDs);
  }

  /**
   * Creates a new searchEvent with geo search parameters
   *
   * @param pSourceEvent Source event
   * @param pGeo         Geo information from request
   * @return the event to send
   */
  @NotNull
  private DocumentSearchEvent _createGeoSearchEvent(@NotNull SearchMarketsEvent pSourceEvent, @NotNull SearchMarketsEvent.Geo pGeo)
  {
    return pSourceEvent.createAnswer(DocumentSearchEvent.class)
        .indices(IIndexStructure.INDEX_TYPE)
        .query(new DocumentSearchEvent.Query()
                   .filter(DocumentSearchEvent.Filter.geoDistance(IIndexStructure.IMarket.LOCATION, pGeo.lat(), pGeo.lon(), pGeo.distance())));
  }

}
