package io.retailplanet.backend.elasticsearch.impl.facades;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import io.retailplanet.backend.elasticsearch.impl.util.QueryUtility;
import org.eclipse.microprofile.metrics.annotation.Counted;
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
  @Counted(name = "search")
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

    _LOGGER.info("ElasticSearch current query: " + request.toString().replace("\n", " "));

    SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);

    _LOGGER.info("ElasticSearch responded after " + response.getTook().toString() + " with " + response.getHits().getTotalHits() + " hits");

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
    List<Object> elements = _extractElements(pHits);
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
  private List<Object> _extractElements(@NotNull SearchHits pHits)
  {
    List<Object> results = new ArrayList<>();
    for (SearchHit hit : pHits.getHits())
    {
      Map<String, Object> obj = hit.getSourceAsMap();
      if (obj.get("id") == null)
        obj.put("id", hit.getId());

      // Overwrite results with inner hits, if some are found (via nested query)
      Map<String, SearchHits> innerHits = hit.getInnerHits();
      if (innerHits != null)
      {
        innerHits.forEach((pName, pInnerHits) -> {
          List<Map<String, Object>> array = new ArrayList<>();
          for (SearchHit hitsHit : pInnerHits.getHits())
            array.add(hitsHit.getSourceAsMap());
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
    private final List<Object> elements;
    private final long maxSize;

    private _SearchResult(List<Object> pElements, long pMaxSize)
    {
      elements = pElements;
      maxSize = pMaxSize;
    }

    @NotNull
    @Override
    public List<Object> getElements()
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
