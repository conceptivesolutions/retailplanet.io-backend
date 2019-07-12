package io.retailplanet.backend.businessapi.api.rest.business;

import io.retailplanet.backend.businessapi.impl.IEvents;
import io.retailplanet.backend.common.events.product.ProductUpsertEvent;
import io.retailplanet.backend.common.util.ZipUtility;
import io.smallrye.reactive.messaging.annotations.*;

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

  @Stream(IEvents.OUT_PRODUCT_UPSERT_UNAUTH)
  Emitter<ProductUpsertEvent> productUpsertedUnauthEmitter;

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
    productUpsertedUnauthEmitter.send(new ProductUpsertEvent()
                                          .session_token(pToken)
                                          .content(ZipUtility.compressedBase64(pJsonBody)));

    // return 200
    return Response.ok().build();
  }

}
