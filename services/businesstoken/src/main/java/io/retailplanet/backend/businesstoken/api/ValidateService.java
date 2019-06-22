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

/**
 * Service for validating requests
 *
 * @author w.glanzer, 21.06.2019
 */
@ApplicationScoped
public class ValidateService
{

  @Inject
  TokenCache tokenCache;

  /**
   * Validate "put products" request
   *
   * @param pJsonObject Request
   * @return Validated request, or null
   */
  @Incoming(IEvents.IN_PRODUCT_UPSERT_UNAUTH)
  @Outgoing(IEvents.OUT_PRODUCT_UPSERT)
  @Broadcast
  public JsonObject validatePutProducts(@NotNull JsonObject pJsonObject)
  {
    String session_token = pJsonObject.getString("session_token");
    String content = pJsonObject.getString("content");
    if (Utility.isNullOrEmptyTrimmedString(session_token) || Utility.isNullOrEmptyTrimmedString(content))
      return null;

    // validate token
    TokenCache.STATE valid = tokenCache.validateToken(session_token);
    if (valid != TokenCache.STATE.VALID)
      return null;

    // find issuer
    String issuer = tokenCache.findIssuer(session_token);
    if (Utility.isNullOrEmptyTrimmedString(issuer))
      return null;

    return new JsonObject()
        .put("content", content)
        .put("clientid", issuer);
  }

}
