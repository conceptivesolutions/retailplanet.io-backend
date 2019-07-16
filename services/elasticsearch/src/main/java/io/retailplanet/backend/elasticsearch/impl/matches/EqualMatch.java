package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.elasticsearch.index.query.*;
import org.jetbrains.annotations.NotNull;

/**
 * Match-Implementation for "equal" match
 *
 * @author w.glanzer, 16.07.2019
 */
class EqualMatch implements IQueryBuilder
{

  static final String TYPE = "eq";

  private String fieldName;
  private String fieldValue;

  public EqualMatch(@NotNull String pFieldName, @NotNull String pFieldValue)
  {
    fieldName = pFieldName;
    fieldValue = pFieldValue;
  }

  @NotNull
  @Override
  public QueryBuilder toQueryBuilder()
  {
    return QueryBuilders.matchQuery(fieldName, fieldValue);
  }

}
