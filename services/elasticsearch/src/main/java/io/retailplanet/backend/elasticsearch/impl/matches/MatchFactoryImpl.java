package io.retailplanet.backend.elasticsearch.impl.matches;

import io.retailplanet.backend.common.events.index.DocumentSearchEvent;
import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.jetbrains.annotations.*;

import javax.inject.Singleton;
import java.util.*;

/**
 * @author w.glanzer, 16.07.2019
 */
@Singleton
class MatchFactoryImpl implements IMatchFactory
{

  @NotNull
  @Override
  public IQueryBuilder interpretMatch(@NotNull String pMatchType, @Nullable String pNestedPath,
                                      @Nullable List<IQueryBuilder> pInnerMatches, @NotNull String... pMatchDetails) throws Exception
  {
    try
    {
      switch (pMatchType)
      {
        case EqualMatch.TYPE:
          return new EqualMatch(pNestedPath, pMatchDetails[0], pMatchDetails[1], DocumentSearchEvent.Operator.valueOf(pMatchDetails[2]));

        case OrMatch.TYPE:
          return new OrMatch(pNestedPath, pMatchDetails[0], Arrays.asList(pMatchDetails).subList(1, pMatchDetails.length));

        case CombinedMatch.TYPE:
          return new CombinedMatch(pNestedPath, DocumentSearchEvent.Operator.valueOf(pMatchDetails[0]), Objects.requireNonNull(pInnerMatches));

        default:
          throw new IllegalArgumentException("Matchtype not found " + pMatchType);
      }
    }
    catch (ClassCastException | NoSuchElementException | NumberFormatException | NullPointerException e)
    {
      throw new Exception("Wrong arguments for match " + pMatchType + ": " + Arrays.toString(pMatchDetails));
    }
  }

}
