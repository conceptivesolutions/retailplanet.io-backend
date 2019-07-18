package io.retailplanet.backend.businessapi.api.rest.business;

import io.retailplanet.backend.businessapi.impl.events.IEventFacade;
import io.retailplanet.backend.common.events.market.MarketUpsertEvent;
import io.retailplanet.backend.common.util.ZipUtility;

import javax.inject.Inject;
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

  @Inject
  private IEventFacade eventFacade;

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
    eventFacade.sendMarketUpsertEvent(new MarketUpsertEvent()
                                          .session_token(pToken)
                                          .content(ZipUtility.compressedBase64(pJsonBody)));

    // return 200
    return Response.ok().build();
  }

}
