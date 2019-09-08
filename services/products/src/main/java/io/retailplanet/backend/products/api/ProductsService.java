package io.retailplanet.backend.products.api;

import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.products.impl.services.*;
import io.retailplanet.backend.products.impl.struct.Product;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import static io.retailplanet.backend.products.impl.struct.IIndexStructure.INDEX_TYPE;

/**
 * Service: Products
 *
 * @author w.glanzer, 20.06.2019
 */
@Path("/business/product")
public class ProductsService
{

  @Inject
  @RestClient
  private ISessionTokenValidateService sessionTokenValidateService;

  @Inject
  @RestClient
  private IIndexWriteService indexWriteService;

  /**
   * Put products with a given session token
   *
   * @param pToken   SessionToken to validate put request
   * @param pContent content
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putProducts(@HeaderParam("session_token") String pToken, Product[] pContent)
  {
    String clientID = sessionTokenValidateService.findIssuerByToken(pToken);
    if (pContent == null || Utility.isNullOrEmptyTrimmedString(clientID))
      return Response.status(Response.Status.BAD_REQUEST).build();

    // store in index
    indexWriteService.upsertDocument(clientID, INDEX_TYPE, Arrays.stream(pContent)
        .map(pProduct -> pProduct.toIndexJSON(clientID))
        .collect(Collectors.toList()));

    // return 200
    return Response.ok().build();
  }

}
