package io.retailplanet.backend.elasticsearch.impl.facades;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import io.retailplanet.backend.elasticsearch.impl.util.QueryUtility;
import io.vertx.core.json.JsonObject;
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
  public List<JsonObject> search(@NotNull List<IQueryBuilder> pMatches, @NotNull List<IQueryBuilder> pFilters, @Nullable Integer pOffset, @Nullable Integer pLength) throws Exception
  {
    SearchRequest request = new SearchRequest()
        .source(new SearchSourceBuilder()
                    .query(QueryUtility.combineShould(_toQueryBuilders(pMatches)))
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
  private List<JsonObject> _toResults(@NotNull SearchHits pHits)
  {
    List<JsonObject> results = new ArrayList<>();
    for (SearchHit hit : pHits.getHits())
    {
      JsonObject obj = new JsonObject(hit.getSourceAsString());
      if (obj.getValue("id") == null)
        obj.put("id", hit.getId());
      results.add(obj);
    }
    return results;
  }

}
