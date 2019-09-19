package io.retailplanet.backend.common.comm.products;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.objects.products.SearchResult;

import javax.ws.rs.*;

/**
 * @author w.glanzer, 05.09.2019
 */
@RegisterForReflection
public interface IProductSearchResource
{

  /**
   * Executes product search
   */
  @POST
  SearchResult searchProducts(@QueryParam("query") String pQuery, @QueryParam("sorting") String pSorting,
                              @QueryParam("offset") Integer pOffset, @QueryParam("length") Integer pLength);

}
