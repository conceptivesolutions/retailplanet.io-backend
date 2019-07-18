package io.retailplanet.backend.markets.impl.events;

import io.reactivex.*;
import io.retailplanet.backend.common.api.AbstractEventFacade;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.market.SearchMarketsResultEvent;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 18.07.2019
 */
@ApplicationScoped
class EventFacadeImpl extends AbstractEventFacade implements IEventFacade
{

  @Stream(IEvents.OUT_INDEX_DOCUMENT_UPSERT)
  protected Emitter<DocumentUpsertEvent> upsertMarketsInIndex;

  @Stream(IEvents.OUT_INDEX_DOCUMENT_SEARCH)
  protected Emitter<DocumentSearchEvent> searchMarketsInIndex;

  @Stream(IEvents.IN_INDEX_DOCUMENT_SEARCHRESULT)
  protected Flowable<DocumentSearchResultEvent> searchMarketsInIndexResults;

  @Stream(IEvents.OUT_MARKETS_SEARCHRESULT)
  protected Emitter<SearchMarketsResultEvent> marketSearchResults;

  @Override
  public void sendDocumentUpsertEvent(@NotNull DocumentUpsertEvent pEvent)
  {
    upsertMarketsInIndex.send(pEvent);
  }

  @NotNull
  @Override
  public Single<DocumentSearchResultEvent> sendDocumentSearchEvent(@NotNull DocumentSearchEvent pEvent)
  {
    searchMarketsInIndex.send(pEvent);
    return pEvent.waitForAnswer(errorsFlowable, searchMarketsInIndexResults);
  }

  @Override
  public void sendSearchMarketsResultEvent(@NotNull SearchMarketsResultEvent pEvent)
  {
    marketSearchResults.send(pEvent);
  }
}
