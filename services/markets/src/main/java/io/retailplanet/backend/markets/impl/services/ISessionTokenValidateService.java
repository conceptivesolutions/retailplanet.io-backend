package io.retailplanet.backend.markets.impl.services;

import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/validate")
@RegisterRestClient
@URL(targetModule = URL.ETarget.BUSINESSTOKEN)
public interface ISessionTokenValidateService
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
