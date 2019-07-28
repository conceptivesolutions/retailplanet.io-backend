package io.retailplanet.backend.userauth.api;

import io.retailplanet.backend.common.events.token.TokenCreateEvent;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.userauth.impl.events.*;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Service to authenticate BusinessToken Events
 *
 * @author w.glanzer, 17.06.2019
 */
@ApplicationScoped
public class AuthBusinessTokenService
{

  @Inject
  private IEventFacade eventFacade;

  /**
   * Authenticates a given BusinessToken-Create-Event
   *
   * @param pCreateEvent Event
   * @return Authorized Event
   */
  @Incoming(IEvents.IN_BUSINESSTOKEN_CREATE)
  @Outgoing(IEvents.OUT_BUSINESSTOKEN_CREATE_AUTH)
  @Broadcast
  public TokenCreateEvent authenticateBusinessTokenCreationEvent(@Nullable TokenCreateEvent pCreateEvent)
  {
    if (pCreateEvent == null)
      return null;

    return eventFacade.trace(pCreateEvent, () -> {
      String clientid = pCreateEvent.clientID;
      String token = pCreateEvent.token;

      // validate
      if (Utility.isNullOrEmptyTrimmedString(clientid) || Utility.isNullOrEmptyTrimmedString(token))
        return null;

      // todo check permissions of the user

      return pCreateEvent.authorized(true);
    });
  }

}
