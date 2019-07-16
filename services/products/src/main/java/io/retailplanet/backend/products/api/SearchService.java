package io.retailplanet.backend.products.api;

import io.reactivex.Flowable;
import io.retailplanet.backend.common.api.AbstractService;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.search.*;
import io.retailplanet.backend.products.impl.IEvents;
import io.retailplanet.backend.products.impl.struct.IIndexStructure;
import io.smallrye.reactive.messaging.annotations.*;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service: Product search
 *
 * @author w.glanzer, 12.07.2019
 */
@ApplicationScoped
public class SearchService extends AbstractService
{

  @Stream(IEvents.OUT_INDEX_DOCUMENT_SEARCH)
  Emitter<DocumentSearchEvent> searchInIndex;

  @Stream(IEvents.IN_INDEX_DOCUMENT_SEARCHRESULT)
  Flowable<DocumentSearchResultEvent> searchInIndexResult;

  @Stream(IEvents.OUT_SEARCH_PRODUCTS_RESULT)
  Emitter<SearchProductsResultEvent> resultEmitter;

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
        .query(new DocumentSearchEvent.Query()
                   .matches(IIndexStructure.IProduct.NAME, pEvent.query))
        .offset(pEvent.offset)
        .length(pEvent.length);

    // send
    searchInIndex.send(searchEvent);

    // wait for answer
    searchEvent.waitForAnswer(searchInIndexResult)
        .map(pResult -> pResult.createAnswer(SearchProductsResultEvent.class))
        .subscribe(resultEmitter::send);
  }

}
