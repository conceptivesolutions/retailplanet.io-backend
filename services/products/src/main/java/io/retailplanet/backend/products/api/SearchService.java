package io.retailplanet.backend.products.api;

import io.retailplanet.backend.common.events.search.*;
import io.retailplanet.backend.products.impl.IEvents;
import io.retailplanet.backend.products.impl.index.IIndexFacade;
import io.smallrye.reactive.messaging.annotations.*;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Service: Product search
 *
 * @author w.glanzer, 12.07.2019
 */
@ApplicationScoped
public class SearchService
{

  @Inject
  private IIndexFacade indexFacade;

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

    // search in index
    indexFacade.searchProducts(pEvent.query, pEvent.sorting, pEvent.offset, pEvent.length, pEvent.filter)
        .subscribe(pResult -> resultEmitter.send(pEvent.createAnswer(SearchProductsResultEvent.class)
                                                     .maxSize(pResult.maxSize)
                                                     .filters(pResult.filters)
                                                     .elements(pResult.elements == null ? null : new ArrayList<>(pResult.elements))));
  }

}
