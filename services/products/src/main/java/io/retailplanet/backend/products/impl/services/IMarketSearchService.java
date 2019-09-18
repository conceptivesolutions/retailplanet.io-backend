package io.retailplanet.backend.products.impl.services;

import io.retailplanet.backend.common.comm.markets.IMarketSearchResource;
import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Path;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/markets/search")
@RegisterRestClient
@URL(targetModule = URL.ETarget.MARKETS)
public interface IMarketSearchService extends IMarketSearchResource
{
}
