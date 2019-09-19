package io.retailplanet.backend.common.comm.index;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.objects.index.*;

import javax.ws.rs.*;
import java.util.List;

/**
 * @author w.glanzer, 05.09.2019
 */
@RegisterForReflection
public interface IIndexReadResource
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
