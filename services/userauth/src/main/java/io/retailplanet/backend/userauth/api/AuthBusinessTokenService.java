package io.retailplanet.backend.userauth.api;

import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.userauth.impl.IEvents;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service to authenticate BusinessToken Events
 *
 * @author w.glanzer, 17.06.2019
 */
@ApplicationScoped
public class AuthBusinessTokenService
{

  /**
   * Authenticates a given BusinessToken-Create-Event
   *
   * @param pCreateEvent Event
   * @return Authorized Event
   */
  @Incoming(IEvents.IN_BUSINESSTOKEN_CREATE)
  @Outgoing(IEvents.OUT_BUSINESSTOKEN_CREATE_AUTH)
  @Broadcast
  public JsonObject authenticateBusinessTokenCreationEvent(@NotNull JsonObject pCreateEvent)
  {
    String clientid = pCreateEvent.getString("clientid");
    String token = pCreateEvent.getString("token");

    // validate
    if(Utility.isNullOrEmptyTrimmedString(clientid) || Utility.isNullOrEmptyTrimmedString(token))
      return null;

    // todo check permissions of the user

    // remove token for security and return result
    pCreateEvent.remove("token");
    return pCreateEvent;
  }

}
