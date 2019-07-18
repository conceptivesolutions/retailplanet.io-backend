package io.retailplanet.backend.search.api.rest;

import io.retailplanet.backend.common.events.search.SearchProductsEvent;
import io.retailplanet.backend.search.impl.events.IEventFacade;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.*;
import javax.ws.rs.core.MediaType;

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
    SearchProductsEvent event = new SearchProductsEvent()
        .query(pQuery)
        .sorting(pSorting)
        .offset(pOffset)
        .length(pLength)
        .filter(pFilter);

    // send request
    eventFacade.sendSearchProductsEvent(event)
        .map(pResult -> new SearchResult()
            .offset(pOffset)
            .length(pLength)
            .maxSize(pResult.maxSize)
            .filters(pResult.filters)
            .elements(pResult.elements))
        .subscribe(pResponse::resume, pResponse::resume);
  }

}
