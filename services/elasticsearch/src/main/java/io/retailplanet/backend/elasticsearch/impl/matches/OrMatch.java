package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import io.retailplanet.backend.elasticsearch.impl.util.QueryUtility;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Match-Implementation for "or" match
 *
 * @author w.glanzer, 17.07.2019
 */
class OrMatch implements IQueryBuilder
{

  static final String TYPE = "or";

  private final String nestedPath;
  private final String fieldName;
  private final List<String> fieldValues;

  OrMatch(@Nullable String pNestedPath, @NotNull String pFieldName, @NotNull List<String> pFieldValues)
  {
    nestedPath = pNestedPath;
    fieldName = pFieldName;
    fieldValues = pFieldValues;
  }

  @Nullable
  @Override
  public QueryBuilder toQueryBuilder()
  {
    List<QueryBuilder> builders = fieldValues.stream()
        .map(pValue -> QueryBuilders.matchQuery(fieldName, pValue))
        .collect(Collectors.toList());

    if (builders.isEmpty())
      return null;

    QueryBuilder shouldQuery = QueryUtility.combineShould(builders);
    if (nestedPath != null)
      return QueryBuilders.nestedQuery(nestedPath, shouldQuery, ScoreMode.Avg)
          .innerHit(new InnerHitBuilder());

    return shouldQuery;
  }

}
