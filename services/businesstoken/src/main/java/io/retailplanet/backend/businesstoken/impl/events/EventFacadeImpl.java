package io.retailplanet.backend.businesstoken.impl.events;

import io.retailplanet.backend.common.events.AbstractEventFacade;
import io.retailplanet.backend.common.events.token.TokenInvalidatedEvent;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 18.07.2019
 */
@ApplicationScoped
class EventFacadeImpl extends AbstractEventFacade implements IEventFacade
{

  @Stream(IEvents.OUT_BUSINESSTOKEN_INVALIDATED)
  protected Emitter<TokenInvalidatedEvent> tokenInvalidatedEmitter;

  @Override
  public void sendTokenInvalidatedEvent(@NotNull TokenInvalidatedEvent pEvent)
  {
    send(pEvent, tokenInvalidatedEmitter);
  }

}
