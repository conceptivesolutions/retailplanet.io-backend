package io.retailplanet.backend.markets.api;

import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.markets.impl.services.*;
import io.retailplanet.backend.markets.impl.struct.*;
import io.vertx.core.json.JsonArray;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.stream.Collector;

/**
 * Resource for all markets requests
 *
 * @author w.glanzer, 23.06.2019
 */
@Path("/business/market")
public class MarketsService
{

  @Inject
  @RestClient
  private ISessionTokenValidateService sessionTokenValidateService;

  @Inject
  @RestClient
  private IIndexWriteService indexWriteService;

  /**
   * Put markets with a given session token
   *
   * @param pToken   SessionToken to validate put request
   * @param pContent content
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putMarkets(@HeaderParam("session_token") String pToken, Market[] pContent)
  {
    String clientID = sessionTokenValidateService.findIssuerByToken(pToken);
    if (pContent == null || Utility.isNullOrEmptyTrimmedString(clientID))
      return Response.status(Response.Status.BAD_REQUEST).build();

    // store in index
    indexWriteService.upsertDocument(clientID, IIndexStructure.INDEX_TYPE, Arrays.stream(pContent)
        .map(pMarket -> pMarket.toIndexJSON(clientID))
        .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

    // return 200
    return Response.ok().build();
  }
}
