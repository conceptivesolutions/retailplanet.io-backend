package io.retailplanet.backend.businessapi.api.rest.business;

import io.retailplanet.backend.businessapi.impl.IEvents;
import io.retailplanet.backend.common.util.ZipUtility;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Resource for all markets requests
 *
 * @author w.glanzer, 21.06.2019
 */
@Path("/business/market")
public class MarketResource
{

  @Stream(IEvents.OUT_MARKET_UPSERT_UNAUTH)
  Emitter<JsonObject> marketUpsertedUnauthEmitter;

  /**
   * Put markets with a given session token
   *
   * @param pToken    SessionToken to validate put request
   * @param pJsonBody Body
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putMarkets(@HeaderParam("session_token") String pToken, String pJsonBody)
  {
    // send event
    marketUpsertedUnauthEmitter.send(new JsonObject()
                                         .put("session_token", pToken)
                                         .put("content", ZipUtility.compressedBase64(pJsonBody)));

    // return 200
    return Response.ok().build();
  }

}
