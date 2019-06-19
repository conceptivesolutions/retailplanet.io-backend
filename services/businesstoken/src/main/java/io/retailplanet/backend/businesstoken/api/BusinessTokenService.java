package io.retailplanet.backend.businesstoken.api;

import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;
import io.retailplanet.backend.businesstoken.impl.events.IEvents;
import io.retailplanet.backend.common.util.Utility;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

/**
 * Service to generate / list all currently available businesstokens
 *
 * @author w.glanzer, 10.06.2019
 */
@ApplicationScoped
public class BusinessTokenService
{

  @Inject
  private TokenCache tokenCache;

  /**
   * Generates a new session_token for the BusinessToken_CREATE_AUTH-Event
   *
   * @param pJsonObject Event
   * @return Result
   */
  @Incoming(IEvents.IN_BUSINESSTOKEN_CREATE_AUTH)
  @Outgoing(IEvents.OUT_BUSINESSTOKEN_CREATED)
  @Broadcast
  public JsonObject generateTokenForBusinessTokenCreateAuthEvent(@NotNull JsonObject pJsonObject)
  {
    String clientid = pJsonObject.getString("clientid");
    if(Utility.isNullOrEmptyTrimmedString(clientid))
      return null;

    String token = UUID.randomUUID().toString();
    return new JsonObject(pJsonObject.getMap())
        .put("session_token", token);
  }

  /**
   * Processor for the BusinessToken_CREATED-Event
   *
   * @param pJsonObject Event
   */
  @Incoming(IEvents.IN_BUSINESSTOKEN_CREATED)
  public void inBusinessTokenCreated(@NotNull JsonObject pJsonObject)
  {
    String clientid = pJsonObject.getString("clientid");
    String token = pJsonObject.getString("session_token");
    if(Utility.isNullOrEmptyTrimmedString(clientid) || Utility.isNullOrEmptyTrimmedString(token))
      return;

    tokenCache.putToken(clientid, token);
  }

}
