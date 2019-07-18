package io.retailplanet.backend.businessapi.api.rest.business;

import io.retailplanet.backend.businessapi.impl.events.IEventFacade;
import io.retailplanet.backend.common.events.token.*;
import io.retailplanet.backend.common.util.Utility;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
import java.time.Instant;

/**
 * Public REST Resource: Business / Token
 *
 * @author w.glanzer, 16.06.2019
 */
@Path("/business/token")
public class TokenResource
{

  @Inject
  private IEventFacade eventFacade;

  /**
   * Generates a new session token for a specific client.
   * If a previous session token was issued, it will be invalidated.
   *
   * @param pClientID    ID of the requesting client
   * @param pClientToken Token to authorize the client
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/generate")
  public void generateToken(@QueryParam("clientid") String pClientID, @QueryParam("token") String pClientToken, @Suspended AsyncResponse pResponse)
  {
    // validate request
    if (Utility.isNullOrEmptyTrimmedString(pClientID) || Utility.isNullOrEmptyTrimmedString(pClientToken))
    {
      pResponse.resume(Response.status(Response.Status.BAD_REQUEST).build());
      return;
    }

    // Create new event
    TokenCreateEvent event = new TokenCreateEvent()
        .clientID(pClientID)
        .token(pClientToken);

    // send event
    eventFacade.sendTokenCreateEvent(event)
        .map(pEvent -> {
          // Result token
          String token = pEvent.session_token;
          Instant validity = pEvent.valid_until;
          if (token != null)
            return Response.ok(new JsonObject()
                                   .put("session_token", token)
                                   .put("valid_until", validity == null ? Instant.MIN : validity)).build();
          return Response.status(Response.Status.BAD_REQUEST);
        })
        .subscribe(pResponse::resume, pEx -> Response.serverError().entity(pEx));
  }

  /**
   * Invalidates the given token
   *
   * @param pSessionToken Token to be removed
   */
  @DELETE
  @Path("{session_token}")
  public Response invalidateToken(@PathParam("session_token") String pSessionToken)
  {
    // validate request
    if (Utility.isNullOrEmptyTrimmedString(pSessionToken))
      return Response.status(Response.Status.BAD_REQUEST).build();

    eventFacade.sendTokenInvalidatedEvent(new TokenInvalidatedEvent().session_token(pSessionToken));
    return Response.ok().build();
  }

}
