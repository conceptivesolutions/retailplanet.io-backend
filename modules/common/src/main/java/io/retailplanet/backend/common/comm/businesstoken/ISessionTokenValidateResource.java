package io.retailplanet.backend.common.comm.businesstoken;

import javax.ws.rs.*;

/**
 * @author w.glanzer, 18.09.2019
 */
public interface ISessionTokenValidateResource
{

  /**
   * Retrieve the issuer (clientID), who issued the session_token
   *
   * @param pSessionToken Token
   */
  @GET
  @Path("issuer")
  String findIssuerByToken(@QueryParam("session_token") String pSessionToken);

}
