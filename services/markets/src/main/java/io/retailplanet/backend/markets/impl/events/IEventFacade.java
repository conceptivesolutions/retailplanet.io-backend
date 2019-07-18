package io.retailplanet.backend.markets.impl.events;

import io.reactivex.Single;
import io.retailplanet.backend.common.events.IAbstractEventFacade;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.market.SearchMarketsResultEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Facade to send events
 *
 * @author w.glanzer, 18.07.2019
 */
public interface IEventFacade extends IAbstractEventFacade
{

  /**
   * Send event "DocumentUpsertEvent"
   *
   * @param pEvent event
   */
  void sendDocumentUpsertEvent(@NotNull DocumentUpsertEvent pEvent);

  /**
   * Send event "DocumentSearchEvent" and wait for "DocumentSearchResultEvent" event
   *
   * @param pEvent event
   */
  @NotNull
  Single<DocumentSearchResultEvent> sendDocumentSearchEvent(@NotNull DocumentSearchEvent pEvent);

  /**
   * Send event "SearchMarketsResultEvent"
   *
   * @param pEvent event
   */
  void sendSearchMarketsResultEvent(@NotNull SearchMarketsResultEvent pEvent);

}
