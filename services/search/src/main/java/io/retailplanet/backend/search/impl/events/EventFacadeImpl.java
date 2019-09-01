package io.retailplanet.backend.search.impl.events;

import io.reactivex.*;
import io.retailplanet.backend.common.events.AbstractEventFacade;
import io.retailplanet.backend.common.events.search.*;
import io.retailplanet.backend.common.util.EventUtility;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 18.07.2019
 */
@ApplicationScoped
class EventFacadeImpl extends AbstractEventFacade implements IEventFacade
{

  @Stream(IEvents.OUT_SEARCH_PRODUCTS)
  protected Emitter<SearchProductsEvent> searchProductsEmitter;

  @Stream(IEvents.IN_SEARCH_PRODUCTS_RESULT)
  protected Flowable<SearchProductsResultEvent> searchProductsResultFlowable;

  @NotNull
  @Override
  public Single<SearchProductsResultEvent> sendSearchProductsEvent(@NotNull SearchProductsEvent pEvent)
  {
    return send(pEvent, searchProductsEmitter, searchProductsResultFlowable);
  }

  @Override
  @PostConstruct
  public void init()
  {
    super.init();
    searchProductsResultFlowable = searchProductsResultFlowable.as(EventUtility::replayEvents);
  }
}
