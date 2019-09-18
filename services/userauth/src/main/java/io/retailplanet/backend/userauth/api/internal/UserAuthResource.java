package io.retailplanet.backend.userauth.api.internal;

import io.retailplanet.backend.common.comm.userauth.IUserAuthResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/userauth")
public class UserAuthResource implements IUserAuthResource
{

  /**
   * Authenticates a clientID and returns 200, if the client is allowed to use the features within the given scope
   *
   * @param pClientID clientID
   * @param pScope    Scope
   * @return Response, if allowed or not
   */
  @GET
  public Response validate(@QueryParam("clientID") String pClientID, @QueryParam("scope") String pScope)
  {
    return Response.ok().build(); //todo
  }

}
