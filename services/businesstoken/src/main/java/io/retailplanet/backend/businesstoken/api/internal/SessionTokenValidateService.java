package io.retailplanet.backend.businesstoken.api.internal;

import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;
import io.retailplanet.backend.common.comm.businesstoken.ISessionTokenValidateResource;
import io.retailplanet.backend.common.util.Utility;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * Service for validating requests that were issued with a session token
 *
 * @author w.glanzer, 21.06.2019
 */
@Path("/internal/validate")
public class SessionTokenValidateService implements ISessionTokenValidateResource
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
  public String findIssuerByToken(@QueryParam("session_token") String pSessionToken)
  {
    if (Utility.isNullOrEmptyTrimmedString(pSessionToken))
      throw new NotFoundException();

    // validate token
    TokenCache.STATE valid = tokenCache.validateToken(pSessionToken);
    if (valid != TokenCache.STATE.VALID)
      throw new NotFoundException();

    // find issuer
    String issuer = tokenCache.findIssuer(pSessionToken);
    if (Utility.isNullOrEmptyTrimmedString(issuer))
      throw new NotFoundException();

    return issuer;
  }

}
