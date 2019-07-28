package io.retailplanet.backend.products.impl.events;

import io.reactivex.Single;
import io.retailplanet.backend.common.events.IAbstractEventFacade;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.market.*;
import io.retailplanet.backend.common.events.search.SearchProductsResultEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Facade to send events
 *
 * @author w.glanzer, 17.07.2019
 */
public interface IEventFacade extends IAbstractEventFacade
{

  /**
   * Send event "SearchMarketsEvent" and return "SearchMarketsResultEvent" as answer
   *
   * @param pEvent Event to search markets
   */
  @NotNull
  Single<SearchMarketsResultEvent> sendSearchMarketsEvent(@NotNull SearchMarketsEvent pEvent);

  /**
   * Send event "DocumentUpsertEvent"
   *
   * @param pEvent event
   */
  void sendDocumentUpsertEvent(@NotNull DocumentUpsertEvent pEvent);

  /**
   * Send event "DocumentSearchEvent" and return "DocumentSearchResultEvent" as answer
   *
   * @param pEvent event
   */
  @NotNull
  Single<DocumentSearchResultEvent> sendDocumentSearchEvent(@NotNull DocumentSearchEvent pEvent);

  /**
   * Send event "SearchProductsResultEvent"
   *
   * @param pEvent event
   */
  void sendSearchProductsResultEvent(@NotNull SearchProductsResultEvent pEvent);

}
