package io.retailplanet.backend.businessapi.impl.events;

import io.reactivex.Single;
import io.retailplanet.backend.common.events.IAbstractEventFacade;
import io.retailplanet.backend.common.events.market.MarketUpsertEvent;
import io.retailplanet.backend.common.events.product.ProductUpsertEvent;
import io.retailplanet.backend.common.events.token.*;
import org.jetbrains.annotations.NotNull;

/**
 * Facade to send events
 *
 * @author w.glanzer, 18.07.2019
 */
public interface IEventFacade extends IAbstractEventFacade
{

  /**
   * Send event "TokenCreateEvent"
   *
   * @param pEvent event
   * @return "TokenCreatedEvent" as answer
   */
  @NotNull
  Single<TokenCreatedEvent> sendTokenCreateEvent(@NotNull TokenCreateEvent pEvent);

  /**
   * Send event "MarketUpsertEvent"
   *
   * @param pEvent event
   */
  void sendMarketUpsertEvent(@NotNull MarketUpsertEvent pEvent);

  /**
   * Send event "ProductUpsertEvent"
   *
   * @param pEvent event
   */
  void sendProductUpsertEvent(@NotNull ProductUpsertEvent pEvent);

  /**
   * Send event "TokenInvalidatedEvent"
   *
   * @param pEvent event
   */
  void sendTokenInvalidatedEvent(@NotNull TokenInvalidatedEvent pEvent);

}
