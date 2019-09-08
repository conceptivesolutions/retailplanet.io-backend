package io.retailplanet.backend.search.impl.services;

import io.retailplanet.backend.common.objects.products.SearchResult;
import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("internal/products/search")
@RegisterRestClient
@URL(targetModule = URL.ETarget.PRODUCTS)
public interface IProductSearchService
{

  /**
   * Executes product search
   */
  @POST
  SearchResult searchProducts(@QueryParam("query") String pQuery, @QueryParam("sorting") String pSorting,
                              @QueryParam("offset") Integer pOffset, @QueryParam("length") Integer pLength);

}
