package io.retailplanet.backend.products.impl.events;

import io.reactivex.*;
import io.retailplanet.backend.common.events.AbstractEventFacade;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.market.*;
import io.retailplanet.backend.common.events.search.SearchProductsResultEvent;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 17.07.2019
 */
@ApplicationScoped
class EventFacade extends AbstractEventFacade implements IEventFacade
{

  @Stream(IEvents.OUT_INDEX_DOCUMENT_UPSERT)
  protected Emitter<DocumentUpsertEvent> upsertProductsInIndex;

  @Stream(IEvents.OUT_SEARCH_MARKETS)
  protected Emitter<SearchMarketsEvent> searchMarketsEmitter;

  @Stream(IEvents.IN_SEARCH_MARKETS_RESULT)
  protected Flowable<SearchMarketsResultEvent> searchMarketsResultFlowable;

  @Stream(IEvents.OUT_INDEX_DOCUMENT_SEARCH)
  protected Emitter<DocumentSearchEvent> searchInIndex;

  @Stream(IEvents.IN_INDEX_DOCUMENT_SEARCHRESULT)
  protected Flowable<DocumentSearchResultEvent> searchInIndexResult;

  @Stream(IEvents.OUT_SEARCH_PRODUCTS_RESULT)
  protected Emitter<SearchProductsResultEvent> resultEmitter;

  @NotNull
  public Single<SearchMarketsResultEvent> sendSearchMarketsEvent(@NotNull SearchMarketsEvent pEvent)
  {
    return send(pEvent, searchMarketsEmitter, searchMarketsResultFlowable);
  }

  @Override
  public void sendDocumentUpsertEvent(@NotNull DocumentUpsertEvent pEvent)
  {
    send(pEvent, upsertProductsInIndex);
  }

  @NotNull
  @Override
  public Single<DocumentSearchResultEvent> sendDocumentSearchEvent(@NotNull DocumentSearchEvent pEvent)
  {
    return send(pEvent, searchInIndex, searchInIndexResult);
  }

  @Override
  public void sendSearchProductsResultEvent(@NotNull SearchProductsResultEvent pEvent)
  {
    send(pEvent, resultEmitter);
  }

}
