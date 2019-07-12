package io.retailplanet.backend.businesstoken.api;

import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;
import io.retailplanet.backend.businesstoken.impl.events.IEvents;
import io.retailplanet.backend.common.events.token.*;
import io.retailplanet.backend.common.util.Utility;
import io.smallrye.reactive.messaging.annotations.*;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jetbrains.annotations.Nullable;
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
  Emitter<TokenInvalidatedEvent> businessTokenInvalidatedEmitter;

  @Inject
  private TokenCache tokenCache;

  /**
   * Generates a new session_token for the BusinessToken_CREATE_AUTH-Event
   *
   * @param pEvent Event
   * @return Result
   */
  @Incoming(IEvents.IN_BUSINESSTOKEN_CREATE_AUTH)
  @Outgoing(IEvents.OUT_BUSINESSTOKEN_CREATED)
  @Broadcast
  public TokenCreatedEvent generateTokenForBusinessTokenCreateAuthEvent(@Nullable TokenCreateEvent pEvent)
  {
    if (pEvent == null)
      return null;

    String clientid = pEvent.clientID;
    if (Utility.isNullOrEmptyTrimmedString(clientid))
      return null;

    // Invalidate previous tokens from this client
    businessTokenInvalidatedEmitter.send(new TokenInvalidatedEvent().clientID(clientid));

    // Generate new and send
    return pEvent.createAnswer(TokenCreatedEvent.class)
        .clientID(clientid)
        .valid_until(Instant.now().plus(_TOKEN_LIFESPAN))
        .session_token(UUID.randomUUID().toString());
  }

  /**
   * Processor for the BusinessToken_CREATED-Event
   *
   * @param pEvent Event
   */
  @Incoming(IEvents.IN_BUSINESSTOKEN_CREATED)
  public void inBusinessTokenCreated(@Nullable TokenCreatedEvent pEvent)
  {
    if (pEvent == null)
      return;

    String clientid = pEvent.clientID;
    String token = pEvent.session_token;
    Instant validUntil = pEvent.valid_until == null ? Instant.MIN : pEvent.valid_until;
    if (Utility.isNullOrEmptyTrimmedString(clientid) || Utility.isNullOrEmptyTrimmedString(token))
      return;

    // Only add valid tokens, because invalid are useless
    if (validUntil.isAfter(Instant.now()))
      tokenCache.putToken(clientid, token, validUntil);
  }

  /**
   * Gets called, when a businesstoken was invalidated (mostly by user)
   *
   * @param pEvent Token
   */
  @Incoming(IEvents.IN_BUSINESSTOKEN_INVALIDATED)
  public void invalidateBusinessToken(@Nullable TokenInvalidatedEvent pEvent)
  {
    if (pEvent == null)
      return;

    // Invalidate given session_token
    String token = pEvent.session_token;
    if (token != null)
      tokenCache.invalidateToken(token);
    else
    {
      // Invalidate all tokens for a specific client
      String clientid = pEvent.clientID;
      if (clientid != null)
        tokenCache.invalidateAllTokens(clientid);
      else
        _LOGGER.warn("Failed to invalidate token for request: " + pEvent.toString());
    }
  }

}
