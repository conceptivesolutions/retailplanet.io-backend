package io.retailplanet.backend.search.impl.services;

import io.retailplanet.backend.common.comm.products.IProductSearchResource;
import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Path;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("internal/products/search")
@RegisterRestClient
@URL(targetModule = URL.ETarget.PRODUCTS)
public interface IProductSearchService extends IProductSearchResource
{
}
