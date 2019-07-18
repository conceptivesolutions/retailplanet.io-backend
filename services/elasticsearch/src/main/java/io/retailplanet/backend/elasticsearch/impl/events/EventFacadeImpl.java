package io.retailplanet.backend.elasticsearch.impl.events;

import io.retailplanet.backend.common.events.AbstractEventFacade;
import io.retailplanet.backend.common.events.index.DocumentSearchResultEvent;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 18.07.2019
 */
@ApplicationScoped
class EventFacadeImpl extends AbstractEventFacade implements IEventFacade
{

  @Stream(IEvents.OUT_INDEX_DOCUMENT_SEARCHRESULT)
  protected Emitter<DocumentSearchResultEvent> searchResultEmitter;

  @Override
  public void sendDocumentSearchResultEvent(@NotNull DocumentSearchResultEvent pEvent)
  {
    searchResultEmitter.send(pEvent);
  }

}
