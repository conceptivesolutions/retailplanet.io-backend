package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.common.events.index.DocumentSearchEvent;
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
  private final String fieldName;
  private final String fieldValue;
  private final DocumentSearchEvent.Operator operator;

  EqualMatch(@Nullable String pNestedPath, @NotNull String pFieldName, @NotNull String pFieldValue, @NotNull DocumentSearchEvent.Operator pOperator)
  {
    nestedPath = pNestedPath;
    fieldName = pFieldName;
    fieldValue = pFieldValue;
    operator = pOperator;
  }

  @Nullable
  @Override
  public QueryBuilder toQueryBuilder()
  {
    MatchQueryBuilder builder = QueryBuilders.matchQuery(fieldName, fieldValue)
        .operator(operator == DocumentSearchEvent.Operator.AND ? Operator.AND : Operator.OR);
    if (nestedPath != null)
      return QueryBuilders.nestedQuery(nestedPath, builder, ScoreMode.Avg)
          .innerHit(new InnerHitBuilder());
    return builder;
  }

}
