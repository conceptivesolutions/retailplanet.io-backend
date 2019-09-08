package io.retailplanet.backend.search.api;

import io.retailplanet.backend.common.events.search.SearchProductsResultEvent;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.search.impl.services.IProductSearchService;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Resource for all /search requests
 *
 * @author w.glanzer, 10.07.2019
 */
@Path("/search")
public class SearchResource
{

  @Inject
  @RestClient
  private IProductSearchService productSearchService;

  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public Response search(@HeaderParam("Authorization") String pUserToken, @QueryParam("query") String pQuery, @QueryParam("sort") String pSorting,
                         @QueryParam("offset") Integer pOffset, @QueryParam("length") Integer pLength, @QueryParam("filter") JsonObject pFilter)
  {
    int offset = pOffset == null ? 0 : pOffset;
    int length = pLength == null ? 20 : pLength;

    // validate request
    if (offset < 0 || length <= 0 || length > 100 || Utility.isNullOrEmptyTrimmedString(pQuery))
    {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

    SearchProductsResultEvent result = productSearchService.searchProducts(pQuery, pSorting, pOffset, pLength);

    return Response.ok(new SearchResult()
                           .offset(offset)
                           .length(length)
                           .maxSize(result.maxSize())
                           .filters(result.filters())
                           .elements(result.elements())).build();
  }

}
