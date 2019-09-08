package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.common.objects.index.Match;
import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.jetbrains.annotations.*;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author w.glanzer, 16.07.2019
 */
public interface IMatchFactory
{

  /**
   * Interprets a list of matches
   *
   * @param pMatches Matches retrieved
   * @return list of "real" matches objects
   */
  @NotNull
  default List<IQueryBuilder> interpretMatches(@Nullable List<Match> pMatches) throws Exception
  {
    if (pMatches == null)
      return Collections.emptyList();
    List<IQueryBuilder> result = new ArrayList<>();
    for (Match filter : pMatches)
    {
      List<Match> innerMatches = filter.innerMatches();
      List<IQueryBuilder> inners = null;
      if (innerMatches != null)
        inners = interpretMatches(innerMatches);
      String matchType = filter.name();
      String[] content = filter.content();
      if (matchType == null || content == null)
        LoggerFactory.getLogger(IMatchFactory.class).info("Skipping match, because content or matchType were NULL" + filter);
      else
        result.add(interpretMatch(matchType, filter.nestedPath(), inners, content));
    }
    return result;
  }

  /**
   * Interprets a single match
   *
   * @param pMatchType    Type of the match
   * @param pNestedPath   Path of nested field, if nested
   * @param pInnerMatches Inner matches
   * @param pMatchDetails Details for the given match
   * @return Match, not <tt>null</tt>
   */
  @NotNull
  IQueryBuilder interpretMatch(@NotNull String pMatchType, @Nullable String pNestedPath, @Nullable List<IQueryBuilder> pInnerMatches,
                               @NotNull String... pMatchDetails) throws Exception;

}
