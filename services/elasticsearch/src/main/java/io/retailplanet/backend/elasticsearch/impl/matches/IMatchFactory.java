package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.common.events.index.DocumentSearchEvent;
import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * @author w.glanzer, 16.07.2019
 */
public interface IMatchFactory
{

  /**
   * Interprets a list of matches
   *
   * @param pMatches Matches retrieved by event
   * @return list of "real" matches objects
   */
  @NotNull
  default List<IQueryBuilder> interpretMatches(@Nullable List<Map.Entry<String, String[]>> pMatches) throws Exception
  {
    if (pMatches == null)
      return Collections.emptyList();
    List<IQueryBuilder> result = new ArrayList<>();
    for (Map.Entry<String, String[]> filter : pMatches)
    {
      String id = filter.getKey();
      String nestedPath = null;
      if (id.contains(DocumentSearchEvent.Query.NESTED_DELIMITER))
      {
        String[] split = id.split(DocumentSearchEvent.Query.NESTED_DELIMITER);
        nestedPath = split[0];
        id = split[1];
      }

      result.add(interpretMatch(id, nestedPath, filter.getValue()));
    }
    return result;
  }

  /**
   * Interprets a single match
   *
   * @param pMatchType    Type of the match
   * @param pNestedPath   Path of nested field, if nested
   * @param pMatchDetails Details for the given match
   * @return Match, not <tt>null</tt>
   */
  @NotNull
  IQueryBuilder interpretMatch(@NotNull String pMatchType, @Nullable String pNestedPath, @NotNull String... pMatchDetails) throws Exception;

}
