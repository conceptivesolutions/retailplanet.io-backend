package io.retailplanet.backend.elasticsearch.api.internal;

import io.retailplanet.backend.common.comm.index.IIndexReadResource;
import io.retailplanet.backend.common.objects.index.*;
import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import io.retailplanet.backend.elasticsearch.impl.facades.IIndexFacade;
import io.retailplanet.backend.elasticsearch.impl.filters.IFilterFactory;
import io.retailplanet.backend.elasticsearch.impl.matches.IMatchFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.*;

/**
 * Service: Read operations on Index
 *
 * @author w.glanzer, 16.07.2019
 */
@Path("/internal/elasticsearch")
public class IndexReadResource implements IIndexReadResource
{

  @Inject
  private IIndexFacade indexFacade;

  @Inject
  private IFilterFactory filterFactory;

  @Inject
  private IMatchFactory matchFactory;

  /**
   * Executes a search on the elasticsearch backend
   *
   * @param pIndexTypes IndexTypes that should be queried
   * @param pOffset     Offset to start the search, or <tt>null</tt>
   * @param pLength     Length of the result
   * @param pQuery      Query
   */
  @POST
  public SearchResult search(@QueryParam("types") List<String> pIndexTypes, @QueryParam("offset") Integer pOffset,
                             @QueryParam("length") Integer pLength, Query pQuery)
  {
    try
    {
      List<IQueryBuilder> filters = filterFactory.interpretFilters(pQuery.filters());
      List<IQueryBuilder> matches = matchFactory.interpretMatches(pQuery.matches());
      IIndexFacade.ISearchResult result = indexFacade.search(pIndexTypes, matches, filters, pOffset, pLength);

      return new SearchResult()
          .hits(Collections.unmodifiableList(result.getElements()))
          .count(result.getMaxSize());
    }
    catch (Exception e)
    {
      throw new InternalServerErrorException(e);
    }
  }

}
