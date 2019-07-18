package io.retailplanet.backend.elasticsearch.impl.facades;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import io.retailplanet.backend.elasticsearch.impl.util.QueryUtility;
import io.vertx.core.json.*;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.*;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains only necessary functions for read actions on elasticsearch
 *
 * @author w.glanzer, 16.07.2019
 */
abstract class ElasticFacadeReadImpl implements IIndexFacade
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(ElasticFacadeReadImpl.class);

  @Inject
  protected RestHighLevelClient restClient;

  @NotNull
  @Override
  public ISearchResult search(@Nullable List<String> pIndexTypes, @NotNull List<IQueryBuilder> pMatches, @NotNull List<IQueryBuilder> pFilters,
                              @Nullable Integer pOffset, @Nullable Integer pLength) throws Exception
  {
    SearchRequest request = new SearchRequest();

    if (pIndexTypes != null)
      request = request
          .indices(pIndexTypes.stream()
                       // we use the "prefix"-search to define the specific indices
                       .map(pType -> pType + "*")
                       .toArray(String[]::new));

    request = request.source(new SearchSourceBuilder()
                                 .query(QueryUtility.combineMust(_toQueryBuilders(pMatches)))
                                 .postFilter(QueryUtility.combineMust(_toQueryBuilders(pFilters))));

    SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);

    _LOGGER.info("ElasticSearch responded after " + response.getTook().toString());

    return _toResults(response.getHits());
  }

  /**
   * Converts all our querybuilders to elasticsearch query builders
   *
   * @param pBuilders our builders
   * @return querybuilders
   */
  @NotNull
  private List<QueryBuilder> _toQueryBuilders(@NotNull List<IQueryBuilder> pBuilders)
  {
    return pBuilders.stream()
        .map(IQueryBuilder::toQueryBuilder)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Converts the elasticsearch searchhits to retailplanet objects
   *
   * @param pHits elasticsearch objects
   * @return retailplanet objects
   */
  @NotNull
  private _SearchResult _toResults(@NotNull SearchHits pHits)
  {
    List<JsonObject> elements = _extractElements(pHits);
    long maxSize = pHits.getTotalHits().value;
    return new _SearchResult(elements, maxSize);
  }

  /**
   * Extracts all result elements from elasticsearch result
   *
   * @param pHits elasticsearch object
   * @return retailplanet object
   */
  @NotNull
  private List<JsonObject> _extractElements(@NotNull SearchHits pHits)
  {
    List<JsonObject> results = new ArrayList<>();
    for (SearchHit hit : pHits.getHits())
    {
      JsonObject obj = new JsonObject(hit.getSourceAsString());
      if (obj.getValue("id") == null)
        obj.put("id", hit.getId());

      // Overwrite results with inner hits, if some are found (via nested query)
      Map<String, SearchHits> innerHits = hit.getInnerHits();
      if (innerHits != null)
      {
        innerHits.forEach((pName, pInnerHits) -> {
          JsonArray array = new JsonArray();
          for (SearchHit hitsHit : pInnerHits.getHits())
            array.add(new JsonObject(hitsHit.getSourceAsString()));
          obj.put(pName, array);
        });
      }
      results.add(obj);
    }
    return results;
  }

  /**
   * SearchResult-Impl
   */
  private static final class _SearchResult implements ISearchResult
  {
    private final List<JsonObject> elements;
    private final long maxSize;

    private _SearchResult(List<JsonObject> pElements, long pMaxSize)
    {
      elements = pElements;
      maxSize = pMaxSize;
    }

    @NotNull
    @Override
    public List<JsonObject> getElements()
    {
      return elements;
    }

    @Override
    public long getMaxSize()
    {
      return maxSize;
    }
  }

}
