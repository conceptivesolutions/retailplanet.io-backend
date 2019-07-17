package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import io.retailplanet.backend.elasticsearch.impl.util.QueryUtility;
import org.elasticsearch.index.query.QueryBuilder;
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

  private final boolean useShouldClause;
  private final List<IQueryBuilder> innerBuilders;

  CombinedMatch(@NotNull String pType, @NotNull List<IQueryBuilder> pInnerBuilders)
  {
    useShouldClause = pType.equalsIgnoreCase("or");
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

    if (useShouldClause)
      return QueryUtility.combineShould(builders);
    return QueryUtility.combineMust(builders);
  }

}
