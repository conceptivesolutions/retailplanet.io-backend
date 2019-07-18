package io.retailplanet.backend.products.impl.events;

import io.reactivex.Flowable;
import io.retailplanet.backend.common.api.AbstractService;
import io.retailplanet.backend.common.events.market.*;
import io.retailplanet.backend.products.impl.IEvents;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.*;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 17.07.2019
 */
@ApplicationScoped
class EventFacade extends AbstractService implements IEventFacade
{

  @Stream(IEvents.OUT_SEARCH_MARKETS)
  Emitter<SearchMarketsEvent> searchMarketsEmitter;

  @Stream(IEvents.IN_SEARCH_MARKETS_RESULT)
  Flowable<SearchMarketsResultEvent> searchMarketsResultFlowable;

  @Nullable
  public SearchMarketsResultEvent searchMarkets(@NotNull SearchMarketsEvent pEvent)
  {
    searchMarketsEmitter.send(pEvent);
    return pEvent.waitForAnswer(errorsFlowable, searchMarketsResultFlowable)
        .blockingGet();
  }

}
