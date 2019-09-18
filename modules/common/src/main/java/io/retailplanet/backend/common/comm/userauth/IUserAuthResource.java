package io.retailplanet.backend.common.comm.userauth;

import javax.ws.rs.*;

/**
 * @author w.glanzer, 05.09.2019
 */
public interface IUserAuthResource
{

  /**
   * Authenticates a clientID and returns 200, if the client is allowed to use the features within the given scope
   *
   * @param pClientID clientID
   * @param pScope    Scope
   * @return Response, if allowed or not
   */
  @GET
  void validate(@QueryParam("clientID") String pClientID, @QueryParam("scope") String pScope);

}
