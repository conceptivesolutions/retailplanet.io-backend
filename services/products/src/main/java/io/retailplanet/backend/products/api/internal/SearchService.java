package io.retailplanet.backend.products.api.internal;

import io.retailplanet.backend.common.events.search.SearchProductsResultEvent;
import io.retailplanet.backend.common.objects.index.*;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.common.util.i18n.ListUtil;
import io.retailplanet.backend.products.impl.filter.*;
import io.retailplanet.backend.products.impl.services.IIndexReadService;
import io.retailplanet.backend.products.impl.struct.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service: Product search
 *
 * @author w.glanzer, 12.07.2019
 */
@Path("/internal/products/search")
public class SearchService
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(SearchService.class);

  @Inject
  @RestClient
  private IIndexReadService indexReadService;

  @Inject
  private ISearchFilterFactory filterFactory;

  /**
   * Executes product search
   */
  @POST
  public Response searchProducts(@QueryParam("query") String pQuery, @QueryParam("sorting") String pSorting,
                                 @QueryParam("offset") Integer pOffset, @QueryParam("length") Integer pLength)
  {
    // validate event
    if (Utility.isNullOrEmptyTrimmedString(pQuery) ||
        pOffset == null || pOffset < 0 || pOffset == Integer.MAX_VALUE ||
        pLength == null || pLength <= 0 || pLength > 100)
    {
      Response.status(Response.Status.BAD_REQUEST).build();
    }

    Query query = new Query().matches(Match.equal(IIndexStructure.IProduct.NAME, pQuery, Match.Operator.OR));
    _enrichWithFilters(query, null); //todo filters

    SearchResult searchResult = indexReadService.search(ListUtil.of(IIndexStructure.INDEX_TYPE), pOffset, pLength, query);

    long count = searchResult.hits() != null ? Math.max(0, searchResult.count()) : 0;
    List<Object> collect = Utility.notNull(searchResult.hits(), ListUtil::of).stream()
        .map(this::_searchResultToProduct)
        .collect(Collectors.toList());

    // send answer
    return Response.ok(new SearchProductsResultEvent()
                           .filters(new HashMap<>()) //todo filters
                           .maxSize(count)
                           .elements(collect)).build();
  }

  /**
   * Creates a product of a search result hit
   *
   * @param pSearchResultObj SearchResult-Object
   * @return Product
   */
  @NotNull
  private Product _searchResultToProduct(@NotNull Object pSearchResultObj)
  {
    if (pSearchResultObj instanceof Map)
      return Product.fromIndexJSON((Map<String, Object>) pSearchResultObj);
    throw new IllegalArgumentException("pSearchResult is not an instance of Map");
  }

  /**
   * Handles the filters object in SearchProductsEvent
   *
   * @param pQuery   Query to enrich
   * @param pFilters Filters object
   */
  private void _enrichWithFilters(@NotNull Query pQuery, @Nullable Map<String, Object> pFilters)
  {
    if (pFilters == null)
      return;

    for (Map.Entry<String, Object> filter : pFilters.entrySet())
    {
      try
      {
        ISearchFilter searchFilter = filterFactory.create(filter.getKey(), filter.getValue());
        searchFilter.enrichQuery(pQuery);
      }
      catch (Exception e)
      {
        _LOGGER.warn("Filter could not be interpreted", e);
      }
    }
  }

}
