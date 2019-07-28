package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.common.events.index.DocumentSearchEvent;
import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import io.retailplanet.backend.elasticsearch.impl.util.QueryUtility;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Match for combined matches
 *
 * @author w.glanzer, 17.07.2019
 */
class CombinedMatch implements IQueryBuilder
{

  static final String TYPE = "combined";

  private final String nestedPath;
  private final DocumentSearchEvent.Operator operator;
  private final List<IQueryBuilder> innerBuilders;

  CombinedMatch(@Nullable String pNestedPath, @NotNull DocumentSearchEvent.Operator pOperator, @NotNull List<IQueryBuilder> pInnerBuilders)
  {
    nestedPath = pNestedPath;
    operator = pOperator;
    innerBuilders = pInnerBuilders;
  }

  @Nullable
  @Override
  public QueryBuilder toQueryBuilder()
  {
    if (innerBuilders.isEmpty())
      return null;

    List<QueryBuilder> builders = innerBuilders.stream()
        .map(IQueryBuilder::toQueryBuilder)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    QueryBuilder builder;
    if (operator == DocumentSearchEvent.Operator.OR)
      builder = QueryUtility.combineShould(builders);
    else
      builder = QueryUtility.combineMust(builders);

    if (nestedPath != null)
      builder = QueryBuilders.nestedQuery(nestedPath, builder, ScoreMode.Avg)
          .innerHit(new InnerHitBuilder());
    return builder;
  }

}
