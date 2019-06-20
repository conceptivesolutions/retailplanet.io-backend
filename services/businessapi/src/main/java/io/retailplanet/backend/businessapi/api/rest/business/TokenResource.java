package io.retailplanet.backend.businessapi.api.rest.business;

import io.reactivex.Flowable;
import io.retailplanet.backend.businessapi.impl.IEvents;
import io.retailplanet.backend.common.api.events.EventChain;
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
@SuppressWarnings("WeakerAccess")
@Path("/business/token")
public class TokenResource
{

  @Stream(IEvents.OUT_BUSINESSTOKEN_CREATE)
  Emitter<JsonObject> tokenCreateEmitter;

  @Stream(IEvents.OUT_BUSINESSTOKEN_INVALIDATED)
  Emitter<JsonObject> tokenInvalidateEmitter;

  @Stream(IEvents.IN_BUSINESSTOKEN_CREATED)
  Flowable<JsonObject> tokenCreatedFlowable;

  @Stream(IEvents.IN_BUSINESSTOKEN_CREATE_FAILED)
  Flowable<JsonObject> tokenCreateFailedFlowable;

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

    // Create a ChainID
    String chainID = EventChain.createID();

    // Fire BusinessToken_CREATE-Event
    tokenCreateEmitter.send(EventChain.createEvent(chainID)
                                .put("clientid", pClientID)
                                .put("token", pClientToken));

    // Wait for BusinessToken_CREATED-Event, extract token and create response
    EventChain.waitForEvent(chainID, tokenCreatedFlowable, tokenCreateFailedFlowable)
        .map(pJson -> {
          // Result token
          String token = pJson.getString("session_token");
          Instant validity = pJson.getInstant("valid_until", Instant.MIN);
          if (token != null)
            return Response.ok(new JsonObject()
                                   .put("session_token", token)
                                   .put("valid_until", validity)).build();

          // Error?
          String error = pJson.getString("error");
          if (error != null)
            return Response.status(500, error).build();

          // WTF has happened..
          LoggerFactory.getLogger(TokenResource.class).warn("Failed to interpret JSON: " + pJson.toString());
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
    if(Utility.isNullOrEmptyTrimmedString(pSessionToken))
      return Response.status(Response.Status.BAD_REQUEST).build();

    tokenInvalidateEmitter.send(new JsonObject().put("session_token", pSessionToken));
    return Response.ok().build();
  }

}
