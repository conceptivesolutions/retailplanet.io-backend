package io.retailplanet.backend.businesstoken.api;

import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;
import io.retailplanet.backend.businesstoken.impl.events.IEvents;
import io.retailplanet.backend.common.util.Utility;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.*;
import java.util.UUID;

/**
 * Service to generate / list all currently available businesstokens
 *
 * @author w.glanzer, 10.06.2019
 */
@ApplicationScoped
public class BusinessTokenService
{

  /* Represents how long a token will be active by default */
  private static final Duration _TOKEN_LIFESPAN = Duration.ofHours(48);
  private static final Logger _LOGGER = LoggerFactory.getLogger(BusinessTokenService.class);

  @Stream(IEvents.OUT_BUSINESSTOKEN_INVALIDATED)
  Emitter<JsonObject> businessTokenInvalidatedEmitter;

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
    if (Utility.isNullOrEmptyTrimmedString(clientid))
      return null;

    // Invalidate previous tokens from this client
    businessTokenInvalidatedEmitter.send(new JsonObject().put("clientid", clientid));

    // Generate new and send
    return new JsonObject(pJsonObject.getMap())
        .put("valid_until", Instant.now().plus(_TOKEN_LIFESPAN))
        .put("session_token", UUID.randomUUID().toString());
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
    Instant validUntil = pJsonObject.getInstant("valid_until", Instant.MIN);
    if (Utility.isNullOrEmptyTrimmedString(clientid) || Utility.isNullOrEmptyTrimmedString(token))
      return;

    // Only add valid tokens, because invalid are useless
    if (validUntil.isAfter(Instant.now()))
      tokenCache.putToken(clientid, token, validUntil);
  }

  /**
   * Gets called, when a businesstoken was invalidated (mostly by user)
   *
   * @param pJsonObject Token
   */
  @Incoming(IEvents.IN_BUSINESSTOKEN_INVALIDATED)
  public void invalidateBusinessToken(@NotNull JsonObject pJsonObject)
  {
    // Invalidate given session_token
    String token = pJsonObject.getString("session_token");
    if (token != null)
      tokenCache.invalidateToken(token);
    else
    {
      // Invalidate all tokens for a specific client
      String clientid = pJsonObject.getString("clientid");
      if (clientid != null)
        tokenCache.invalidateAllTokens(clientid);
      else
        _LOGGER.warn("Failed to invalidate token for request: " + pJsonObject.toString());
    }
  }

}
