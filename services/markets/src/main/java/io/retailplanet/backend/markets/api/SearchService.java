package io.retailplanet.backend.markets.api;

import io.reactivex.Flowable;
import io.retailplanet.backend.common.api.AbstractService;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.market.*;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.markets.impl.IEvents;
import io.retailplanet.backend.markets.impl.struct.IIndexStructure;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonArray;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

/**
 * Service: Market search
 *
 * @author w.glanzer, 14.07.2019
 */
@ApplicationScoped
public class SearchService extends AbstractService
{

  @Stream(IEvents.OUT_INDEX_DOCUMENT_SEARCH)
  Emitter<DocumentSearchEvent> searchMarketsInIndex;

  @Stream(IEvents.IN_INDEX_DOCUMENT_SEARCHRESULT)
  Flowable<DocumentSearchResultEvent> searchMarketsInIndexResults;

  @Stream(IEvents.OUT_MARKETS_SEARCHRESULT)
  Emitter<SearchMarketsResultEvent> marketSearchResults;

  @Incoming(IEvents.IN_MARKETS_SEARCH)
  public void searchMarkets(@Nullable SearchMarketsEvent pEvent)
  {
    if (pEvent == null)
      return;

    DocumentSearchEvent searchEvent;

    if (pEvent.geoSearch != null)
      searchEvent = _createGeoSearchEvent(pEvent, pEvent.geoSearch);
    else
    {
      notifyError(new IllegalArgumentException("Failed to parse search event " + pEvent + ". No search pattern matched."));
      return;
    }

    searchMarketsInIndex.send(searchEvent);

    searchEvent.waitForAnswer(errorsFlowable, searchMarketsInIndexResults)
        .subscribe(pResult -> marketSearchResults.send(_createResultEvent(pEvent, pResult)));
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
