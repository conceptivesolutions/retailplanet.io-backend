package io.retailplanet.backend.businessapi.api.rest.business;

import io.retailplanet.backend.businessapi.impl.IEvents;
import io.retailplanet.backend.common.util.ZipUtility;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Resource for all product requests
 *
 * @author w.glanzer, 21.06.2019
 */
@Path("/business/product")
public class ProductResource
{

  @SuppressWarnings("WeakerAccess")
  @Stream(IEvents.OUT_PRODUCT_UPSERT_UNAUTH)
  Emitter<JsonObject> productUpsertedUnauthEmitter;

  /**
   * Put products with a given session token
   *
   * @param pToken    SessionToken to validate put request
   * @param pJsonBody Body
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putProducts(@HeaderParam("session_token") String pToken, String pJsonBody)
  {
    // send event
    productUpsertedUnauthEmitter.send(new JsonObject()
                                          .put("session_token", pToken)
                                          .put("content", ZipUtility.compressedBase64(pJsonBody)));

    // return 200
    return Response.ok().build();
  }

}
