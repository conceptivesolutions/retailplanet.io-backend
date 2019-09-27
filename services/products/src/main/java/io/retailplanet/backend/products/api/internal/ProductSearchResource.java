package io.retailplanet.backend.products.api.internal;

import io.retailplanet.backend.common.comm.products.IProductSearchResource;
import io.retailplanet.backend.common.objects.index.*;
import io.retailplanet.backend.common.objects.products.SearchResult;
import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.common.util.i18n.ListUtil;
import io.retailplanet.backend.products.impl.filter.*;
import io.retailplanet.backend.products.impl.services.IIndexReadService;
import io.retailplanet.backend.products.impl.struct.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service: Product search
 *
 * @author w.glanzer, 12.07.2019
 */
@Path("/internal/products/search")
public class ProductSearchResource implements IProductSearchResource
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(ProductSearchResource.class);

  @Inject
  @RestClient
  private IIndexReadService indexReadService;

  @Inject
  private ISearchFilterFactory filterFactory;

  /**
   * Executes product search
   */
  @POST
  public SearchResult searchProducts(@QueryParam("query") String pQuery, @QueryParam("sorting") String pSorting,
                                     @QueryParam("offset") Integer pOffset, @QueryParam("length") Integer pLength, @QueryParam("filter") String pFilter)
  {
    if (Utility.isNullOrEmptyTrimmedString(pQuery))
      throw new BadRequestException();

    if (pOffset == null || pOffset < 0 || pOffset == Integer.MAX_VALUE)
      pOffset = 0;

    if (pLength == null || pLength <= 0 || pLength > 100)
      pLength = 20;

    Query query = new Query().matches(Match.equal(IIndexStructure.IProduct.NAME, pQuery, Match.Operator.OR));
    _enrichWithFilters(query, pFilter);

    io.retailplanet.backend.common.objects.index.SearchResult searchResult = indexReadService.search(ListUtil.of(IIndexStructure.INDEX_TYPE), pOffset, pLength, query);

    long count = searchResult.hits() != null ? Math.max(0, searchResult.count()) : 0;
    List<Object> collect = Utility.notNull(searchResult.hits(), ListUtil::of).stream()
        .map(this::_searchResultToProduct)
        .collect(Collectors.toList());

    _LOGGER.info("Search with term '" + pQuery + "' returned " + collect.size() + " (" + count + ") results");

    // send answer
    return new SearchResult()
        .filters(new HashMap<>()) //todo filters
        .maxSize(count)
        .elements(collect);
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
   * Handles the filters object
   *
   * @param pQuery   Query to enrich
   * @param pFilters Filters object
   */
  private void _enrichWithFilters(@NotNull Query pQuery, @Nullable String pFilters)
  {
    if (pFilters == null)
      return;

    Map<String, Object> filters = _parseFilters(pFilters);
    if (filters == null)
      return;

    //todo remove specialhandling
    if (filters.containsKey("availability"))
    {
      List<String> availability = (List<String>) filters.remove("availability");
      if (filters.containsKey("geo"))
      {
        ((List) filters.get("geo")).add(availability.stream()
                                            .map(ProductAvailability.TYPE::valueOf)
                                            .collect(Collectors.toList()));
      }
    }

    for (Map.Entry<String, Object> filter : filters.entrySet())
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

  /**
   * Parses the json string to a map
   *
   * @param pFilters Filters-JSON
   * @return Map, or <tt>null</tt> if invalid json
   */
  @Nullable
  private Map<String, Object> _parseFilters(@NotNull String pFilters)
  {
    try
    {
      return new HashMap<>(JsonbBuilder.create().fromJson(pFilters, Map.class));
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to parse filters: " + pFilters, e);
      return null;
    }
  }

}
