package io.retailplanet.backend.businesstoken.impl.services;

import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/userauth")
@RegisterRestClient
@URL(targetModule = URL.ETarget.USERAUTH)
public interface IUserAuthService
{

  /**
   * Authenticates a clientID and returns 200, if the client is allowed to use the features within the given scope
   *
   * @param pClientID clientID
   * @param pScope    Scope
   * @return Response, if allowed or not
   */
  @GET
  Response validate(@PathParam("clientID") String pClientID, @PathParam("scope") String pScope);

}
