package io.retailplanet.backend.businesstoken.api.internal;

import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;
import io.retailplanet.backend.common.util.Utility;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Service for validating requests that were issued with a session token
 *
 * @author w.glanzer, 21.06.2019
 */
@Path("/internal/validate")
public class SessionTokenValidateService
{

  @Inject
  private TokenCache tokenCache;

  /**
   * Retrieve the issuer (clientID), who issued the session_token
   *
   * @param pSessionToken Token
   */
  @GET
  @Path("issuer")
  public Response issuer(@PathParam("session_token") String pSessionToken)
  {
    if (Utility.isNullOrEmptyTrimmedString(pSessionToken))
      return Response.status(Response.Status.NOT_FOUND).build();

    // validate token
    TokenCache.STATE valid = tokenCache.validateToken(pSessionToken);
    if (valid != TokenCache.STATE.VALID)
      return Response.status(Response.Status.NOT_FOUND).build();

    // find issuer
    String issuer = tokenCache.findIssuer(pSessionToken);
    if (Utility.isNullOrEmptyTrimmedString(issuer))
      return Response.status(Response.Status.NOT_FOUND).build();

    return Response.ok(issuer).build();
  }

}
