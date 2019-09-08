package io.retailplanet.backend.products.api.internal;

import io.retailplanet.backend.common.events.index.DocumentSearchResultEvent;
import io.retailplanet.backend.common.events.search.*;
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
   *
   * @param pEvent Search event
   */
  @POST
  public Response searchProducts(@Nullable SearchProductsEvent pEvent) //todo refactor event
  {
    Integer offset = pEvent.offset();
    Integer length = pEvent.length();

    // validate event
    if (Utility.isNullOrEmptyTrimmedString(pEvent.query()) ||
        offset == null || offset < 0 || offset == Integer.MAX_VALUE ||
        length == null || length <= 0 || length > 100)
    {
      Response.status(Response.Status.BAD_REQUEST).build();
    }

    DocumentSearchResultEvent searchResult = indexReadService.search(ListUtil.of(IIndexStructure.INDEX_TYPE), offset, length, _buildIndexQuery(pEvent));

    return Response.ok(_answerSearchProductsEvent(pEvent, searchResult)).build();
  }

  /**
   * Answers the SearchProductsEvent with the results of the given DocumentSearchResultEvent
   *
   * @param pSourceEvent                 Source event the user started
   * @param pSearchProductsInIndexResult Search in index
   */
  private SearchProductsResultEvent _answerSearchProductsEvent(@NotNull SearchProductsEvent pSourceEvent, @NotNull DocumentSearchResultEvent pSearchProductsInIndexResult)
  {
    long count = pSearchProductsInIndexResult.hits() != null ? Math.max(0, pSearchProductsInIndexResult.count()) : 0;
    List<Object> collect = Utility.notNull(pSearchProductsInIndexResult.hits(), ListUtil::of).stream()
        .map(this::_searchResultToProduct)
        .collect(Collectors.toList());

    // send answer
    return pSourceEvent.createAnswer(SearchProductsResultEvent.class)
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
   * Creates the request index query to request specific products in index
   *
   * @param pEvent Source event
   * @return Query to use in index request
   */
  @NotNull
  private Query _buildIndexQuery(@NotNull SearchProductsEvent pEvent)
  {
    Query query = new Query()
        .matches(Match.equal(IIndexStructure.IProduct.NAME, Objects.requireNonNull(pEvent.query()), Match.Operator.OR));
    _enrichWithFilters(query, pEvent.filter());
    return query;
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
