package io.retailplanet.backend.search.impl.services;

import io.retailplanet.backend.common.objects.products.SearchResult;
import io.retailplanet.backend.common.util.i18n.ListUtil;

import javax.ws.rs.*;

/**
 * @author w.glanzer, 12.09.2019
 */
@Path("internal/products/search")
public class MockedProductSearchService
{

  @POST
  public SearchResult searchProducts(@QueryParam("query") String pQuery, @QueryParam("sorting") String pSorting,
                                     @QueryParam("offset") Integer pOffset, @QueryParam("length") Integer pLength)
  {
    return new SearchResult()
        .elements(ListUtil.of("nix"))
        .maxSize(8000);
  }

}
