package io.retailplanet.backend.businesstoken.impl.events;

import io.retailplanet.backend.common.api.IAbstractEventFacade;
import io.retailplanet.backend.common.events.token.TokenInvalidatedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Facade to send events
 *
 * @author w.glanzer, 18.07.2019
 */
public interface IEventFacade extends IAbstractEventFacade
{

  /**
   * Send event "TokenInvalidatedEvent"
   *
   * @param pEvent event
   */
  void sendTokenInvalidatedEvent(@NotNull TokenInvalidatedEvent pEvent);

}
