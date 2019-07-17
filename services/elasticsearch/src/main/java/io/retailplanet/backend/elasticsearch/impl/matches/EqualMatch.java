package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.jetbrains.annotations.*;

/**
 * Match-Implementation for "equal" match
 *
 * @author w.glanzer, 16.07.2019
 */
class EqualMatch implements IQueryBuilder
{

  static final String TYPE = "eq";

  private final String nestedPath;
  private String fieldName;
  private String fieldValue;

  EqualMatch(@Nullable String pNestedPath, @NotNull String pFieldName, @NotNull String pFieldValue)
  {
    nestedPath = pNestedPath;
    fieldName = pFieldName;
    fieldValue = pFieldValue;
  }

  @Nullable
  @Override
  public QueryBuilder toQueryBuilder()
  {
    MatchQueryBuilder builder = QueryBuilders.matchQuery(fieldName, fieldValue);
    if (nestedPath != null)
      return QueryBuilders.nestedQuery(nestedPath, builder, ScoreMode.Avg);
    return builder;
  }

}
