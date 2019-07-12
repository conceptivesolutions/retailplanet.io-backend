package io.retailplanet.backend.businessapi.api.rest.business;

import io.reactivex.Flowable;
import io.retailplanet.backend.businessapi.impl.IEvents;
import io.retailplanet.backend.common.events.ErrorEvent;
import io.retailplanet.backend.common.events.token.*;
import io.retailplanet.backend.common.util.Utility;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonObject;
import org.slf4j.LoggerFactory;

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

  @Stream(IEvents.OUT_BUSINESSTOKEN_CREATE)
  Emitter<TokenCreateEvent> tokenCreateEmitter;

  @Stream(IEvents.OUT_BUSINESSTOKEN_INVALIDATED)
  Emitter<TokenInvalidatedEvent> tokenInvalidateEmitter;

  @Stream(IEvents.IN_BUSINESSTOKEN_CREATED)
  Flowable<TokenCreatedEvent> tokenCreatedFlowable;

  @Stream(IEvents.IN_BUSINESSTOKEN_CREATE_FAILED)
  Flowable<ErrorEvent> tokenCreateFailedFlowable;

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

    // Fire BusinessToken_CREATE-Event
    tokenCreateEmitter.send(event);

    // wait for answer
    event.waitForAnswer(tokenCreatedFlowable, tokenCreateFailedFlowable)
        .map(pEvent -> {
          // Result token
          if (pEvent instanceof TokenCreatedEvent)
          {
            String token = ((TokenCreatedEvent) pEvent).session_token;
            Instant validity = ((TokenCreatedEvent) pEvent).valid_until;
            if (token != null)
              return Response.ok(new JsonObject()
                                     .put("session_token", token)
                                     .put("valid_until", validity == null ? Instant.MIN : validity)).build();
          }

          // Error?
          if (pEvent instanceof ErrorEvent)
            return Response.status(500, ((ErrorEvent) pEvent).error).build();

          // WTF has happened..
          LoggerFactory.getLogger(TokenResource.class).warn("Failed to interpret event: " + pEvent.toString());
          return Response.serverError().build();
        })
        .subscribe(pResponse::resume, pResponse::resume);
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

    tokenInvalidateEmitter.send(new TokenInvalidatedEvent().session_token(pSessionToken));
    return Response.ok().build();
  }

}
