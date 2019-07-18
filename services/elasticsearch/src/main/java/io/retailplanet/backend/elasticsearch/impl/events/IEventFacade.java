package io.retailplanet.backend.elasticsearch.impl.events;

import io.retailplanet.backend.common.api.IAbstractEventFacade;
import io.retailplanet.backend.common.events.index.DocumentSearchResultEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Facade to send events
 *
 * @author w.glanzer, 18.07.2019
 */
public interface IEventFacade extends IAbstractEventFacade
{

  /**
   * Send event "DocumentSearchResultEvent"
   *
   * @param pEvent event
   */
  void sendDocumentSearchResultEvent(@NotNull DocumentSearchResultEvent pEvent);

}
