package io.retailplanet.backend.businessapi.impl.events;

import io.reactivex.*;
import io.retailplanet.backend.common.api.AbstractEventFacade;
import io.retailplanet.backend.common.events.market.MarketUpsertEvent;
import io.retailplanet.backend.common.events.product.ProductUpsertEvent;
import io.retailplanet.backend.common.events.token.*;
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

  @Stream(IEvents.OUT_MARKET_UPSERT_UNAUTH)
  protected Emitter<MarketUpsertEvent> marketUpsertedUnauthEmitter;

  @Stream(IEvents.OUT_PRODUCT_UPSERT_UNAUTH)
  protected Emitter<ProductUpsertEvent> productUpsertedUnauthEmitter;

  @Stream(IEvents.OUT_BUSINESSTOKEN_CREATE)
  protected Emitter<TokenCreateEvent> tokenCreateEmitter;

  @Stream(IEvents.OUT_BUSINESSTOKEN_INVALIDATED)
  protected Emitter<TokenInvalidatedEvent> tokenInvalidateEmitter;

  @Stream(IEvents.IN_BUSINESSTOKEN_CREATED)
  protected Flowable<TokenCreatedEvent> tokenCreatedFlowable;

  @NotNull
  @Override
  public Single<TokenCreatedEvent> sendTokenCreateEvent(@NotNull TokenCreateEvent pEvent)
  {
    tokenCreateEmitter.send(pEvent);
    return pEvent.waitForAnswer(errorsFlowable, tokenCreatedFlowable);
  }

  @Override
  public void sendMarketUpsertEvent(@NotNull MarketUpsertEvent pEvent)
  {
    marketUpsertedUnauthEmitter.send(pEvent);
  }

  @Override
  public void sendProductUpsertEvent(@NotNull ProductUpsertEvent pEvent)
  {
    productUpsertedUnauthEmitter.send(pEvent);
  }

  @Override
  public void sendTokenInvalidatedEvent(@NotNull TokenInvalidatedEvent pEvent)
  {
    tokenInvalidateEmitter.send(pEvent);
  }
}
