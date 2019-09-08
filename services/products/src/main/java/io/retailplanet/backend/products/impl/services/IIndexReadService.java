package io.retailplanet.backend.products.impl.services;

import io.retailplanet.backend.common.objects.index.*;
import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/elasticsearch")
@RegisterRestClient
@URL(targetModule = URL.ETarget.ELASTICSEARCH)
public interface IIndexReadService
{

  /**
   * Executes a search on the elasticsearch backend
   *
   * @param pIndexTypes IndexTypes that should be queried
   * @param pOffset     Offset to start the search, or <tt>null</tt>
   * @param pLength     Length of the result
   * @param pQuery      Query
   */
  @POST
  SearchResult search(@QueryParam("types") List<String> pIndexTypes, @QueryParam("offset") Integer pOffset,
                      @QueryParam("length") Integer pLength, Query pQuery);

}
