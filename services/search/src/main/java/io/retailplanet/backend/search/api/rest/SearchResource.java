package io.retailplanet.backend.search.api.rest;

import io.retailplanet.backend.common.events.search.SearchProductsEvent;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.search.impl.events.IEventFacade;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.*;
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
  private IEventFacade eventFacade;

  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public void search(@HeaderParam("Authorization") String pUserToken, @QueryParam("query") String pQuery, @QueryParam("sort") String pSorting,
                     @QueryParam("offset") Integer pOffset, @QueryParam("length") Integer pLength, @QueryParam("filter") JsonObject pFilter,
                     @Suspended AsyncResponse pResponse)
  {
    int offset = pOffset == null ? 0 : pOffset;
    int length = pLength == null ? 20 : pLength;

    // validate request
    if (offset < 0 || length <= 0 || length > 100 || Utility.isNullOrEmptyTrimmedString(pQuery))
    {
      pResponse.resume(Response.status(Response.Status.BAD_REQUEST));
      return;
    }

    SearchProductsEvent event = new SearchProductsEvent()
        .query(pQuery)
        .sorting(pSorting)
        .offset(offset)
        .length(length)
        .filter(pFilter);

    // send request
    eventFacade.sendSearchProductsEvent(event)
        .map(pResult -> new SearchResult()
            .offset(offset)
            .length(length)
            .maxSize(pResult.maxSize())
            .filters(pResult.filters())
            .elements(pResult.elements()))
        .subscribe(pResponse::resume, pResponse::resume);
  }

}
