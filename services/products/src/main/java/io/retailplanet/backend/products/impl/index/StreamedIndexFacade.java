package io.retailplanet.backend.products.impl.index;

import io.reactivex.*;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.products.impl.IEvents;
import io.retailplanet.backend.products.impl.struct.*;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonArray;
import org.jetbrains.annotations.*;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collector;

/**
 * StreamedIndexFacade
 *
 * @author w.glanzer, 22.06.2019
 */
@Singleton
class StreamedIndexFacade implements IIndexFacade
{

  private static final String _INDEX_TYPE = "product";

  @Stream(IEvents.OUT_INDEX_DOCUMENT_UPSERT)
  Emitter<DocumentUpsertEvent> upsertProductsInIndex;

  @Stream(IEvents.OUT_INDEX_DOCUMENT_SEARCH)
  Emitter<DocumentSearchEvent> searchInIndex;

  @Stream(IEvents.IN_INDEX_DOCUMENT_SEARCHRESULT)
  Flowable<DocumentSearchResultEvent> searchResultsFlowable;

  @Override
  public void upsertProducts(@NotNull String pClientID, @NotNull Product[] pProductList)
  {
    // Build request to index facade
    DocumentUpsertEvent event = new DocumentUpsertEvent()
        .clientID(pClientID)
        .type(_INDEX_TYPE)
        .doc(Arrays.stream(pProductList)
                 .map(pProduct -> pProduct.toIndexJSON(pClientID))
                 .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

    // fire request
    upsertProductsInIndex.send(event);
  }

  @NotNull
  @Override
  public Single<SearchResult> searchProducts(@NotNull String pQuery, @Nullable String pSort, @Nullable Integer pOffset, @Nullable Integer pLength,
                                             @Nullable Map<String, Object> pFilters)
  {
    // Build request
    DocumentSearchEvent event = new DocumentSearchEvent()
        .query(new DocumentSearchEvent.Query()
                   .matches(IIndexStructure.IProduct.NAME, pQuery))
        .offset(pOffset)
        .length(pLength);

    // send
    searchInIndex.send(event);

    // wait for answer
    return event.waitForAnswer(searchResultsFlowable)
        .map(pEvent -> new SearchResult()); //todo transform
  }


}
