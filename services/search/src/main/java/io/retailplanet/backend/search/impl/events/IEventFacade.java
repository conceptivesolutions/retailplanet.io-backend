package io.retailplanet.backend.search.impl.events;

import io.reactivex.Single;
import io.retailplanet.backend.common.events.IAbstractEventFacade;
import io.retailplanet.backend.common.events.search.*;
import org.jetbrains.annotations.NotNull;

/**
 * Facade to send events
 *
 * @author w.glanzer, 18.07.2019
 */
public interface IEventFacade extends IAbstractEventFacade
{

  /**
   * Send event "SearchProductsEvent" and wait for "SearchProductsResultEvent" as answer
   *
   * @param pEvent event
   */
  @NotNull
  Single<SearchProductsResultEvent> sendSearchProductsEvent(@NotNull SearchProductsEvent pEvent);

}
