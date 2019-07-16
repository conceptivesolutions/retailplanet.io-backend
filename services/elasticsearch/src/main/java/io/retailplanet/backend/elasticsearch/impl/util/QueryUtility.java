package io.retailplanet.backend.elasticsearch.impl.util;

import org.elasticsearch.index.query.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Utility for all QueryBuilders
 *
 * @author w.glanzer, 16.07.2019
 */
public class QueryUtility
{

  /**
   * Combines a list of querybuilders to a single querybuilder with MUST condition
   *
   * @param pBuilder List of builders
   * @return single builder
   */
  @Nullable
  public static QueryBuilder combineMust(@Nullable List<QueryBuilder> pBuilder)
  {
    if (pBuilder == null || pBuilder.isEmpty())
      return null;

    if (pBuilder.size() == 1)
      return pBuilder.get(0);

    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    pBuilder.stream()
        .filter(Objects::nonNull)
        .forEach(boolQuery::must);
    return boolQuery;
  }

  /**
   * Combines a list of querybuilders to a single querybuilder with SHOULD condition
   *
   * @param pBuilder List of builders
   * @return single builder
   */
  @Nullable
  public static QueryBuilder combineShould(@Nullable List<QueryBuilder> pBuilder)
  {
    if (pBuilder == null || pBuilder.isEmpty())
      return null;

    if (pBuilder.size() == 1)
      return pBuilder.get(0);

    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    pBuilder.stream()
        .filter(Objects::nonNull)
        .forEach(boolQuery::should);
    return boolQuery;
  }

}
