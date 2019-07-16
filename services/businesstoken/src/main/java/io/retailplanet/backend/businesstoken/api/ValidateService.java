package io.retailplanet.backend.businesstoken.api;

import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;
import io.retailplanet.backend.businesstoken.impl.events.IEvents;
import io.retailplanet.backend.common.api.AbstractService;
import io.retailplanet.backend.common.events.market.MarketUpsertEvent;
import io.retailplanet.backend.common.events.product.ProductUpsertEvent;
import io.retailplanet.backend.common.util.Utility;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Service for validating requests
 *
 * @author w.glanzer, 21.06.2019
 */
@ApplicationScoped
public class ValidateService extends AbstractService
{

  @Inject
  TokenCache tokenCache;

  /**
   * Validate "put products" request
   *
   * @param pEvent Request
   * @return Validated request, or null
   */
  @Incoming(IEvents.IN_PRODUCT_UPSERT_UNAUTH)
  @Outgoing(IEvents.OUT_PRODUCT_UPSERT)
  @Broadcast
  public ProductUpsertEvent validatePutProducts(@Nullable ProductUpsertEvent pEvent)
  {
    if (pEvent == null)
      return null;

    String session_token = pEvent.session_token;
    if (Utility.isNullOrEmptyTrimmedString(session_token))
      return null;

    // validate token
    TokenCache.STATE valid = tokenCache.validateToken(session_token);
    if (valid != TokenCache.STATE.VALID)
      return null;

    // find issuer
    String issuer = tokenCache.findIssuer(session_token);
    if (Utility.isNullOrEmptyTrimmedString(issuer))
      return null;

    return pEvent
        .clientID(issuer)
        .authorized(true);
  }

  /**
   * Validate "put markets" request
   *
   * @param pEvent Request
   * @return Validated request, or null
   */
  @Incoming(IEvents.IN_MARKET_UPSERT_UNAUTH)
  @Outgoing(IEvents.OUT_MARKET_UPSERT)
  @Broadcast
  public MarketUpsertEvent validatePutMarkets(@Nullable MarketUpsertEvent pEvent)
  {
    if (pEvent == null)
      return null;

    String session_token = pEvent.session_token;
    if (Utility.isNullOrEmptyTrimmedString(session_token))
      return null;

    // validate token
    TokenCache.STATE valid = tokenCache.validateToken(session_token);
    if (valid != TokenCache.STATE.VALID)
      return null;

    // find issuer
    String issuer = tokenCache.findIssuer(session_token);
    if (Utility.isNullOrEmptyTrimmedString(issuer))
      return null;

    return pEvent
        .clientID(issuer)
        .authorized(true);
  }

}
