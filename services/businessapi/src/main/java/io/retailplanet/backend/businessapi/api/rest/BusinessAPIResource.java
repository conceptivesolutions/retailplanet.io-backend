package io.retailplanet.backend.businessapi.api.rest;

import io.reactivex.Flowable;
import io.retailplanet.backend.businessapi.impl.*;
import io.retailplanet.backend.common.api.events.EventChain;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;

/**
 * Public REST Resource: BusinessAPI
 *
 * @author w.glanzer, 16.06.2019
 */
@SuppressWarnings("WeakerAccess")
@Path("/business")
public class BusinessAPIResource
{

  @Stream(IEvents.OUT_BUSINESSTOKEN_CREATE)
  Emitter<JsonObject> tokenCreateEmitter;

  @Stream(IEvents.IN_BUSINESSTOKEN_CREATED)
  Flowable<JsonObject> tokenCreatedFlowable;

  /**
   * Generates a new session token for a specific client.
   * If a previous session token was issued, it will be invalidated.
   *
   * @param pClientID ID of the requesting client
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/token/generate")
  public void generateToken(@QueryParam("clientid") String pClientID, @Suspended AsyncResponse pResponse)
  {
    // Create a ChainID
    String chainID = EventChain.createID();

    // Fire BusinessToken_CREATE-Event
    tokenCreateEmitter.send(Events.createBusinessTokenCreateEvent(chainID, pClientID));

    // Wait for BusinessToken_CREATED-Event, extract token and create response
    EventChain.waitForEvent(tokenCreatedFlowable, chainID)
        .map(pJson -> pJson.getString("session_token"))
        .map(pToken -> Response.ok(new JsonObject().put("session_token", pToken)).build())
        .subscribe(pResponse::resume, pResponse::resume);
  }

}
