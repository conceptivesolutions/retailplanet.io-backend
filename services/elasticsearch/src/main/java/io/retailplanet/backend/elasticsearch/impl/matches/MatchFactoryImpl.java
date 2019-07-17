package io.retailplanet.backend.elasticsearch.impl.matches;

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
  public IQueryBuilder interpretMatch(@NotNull String pMatchType, @Nullable String pNestedPath, @NotNull String... pMatchDetails) throws Exception
  {
    try
    {
      //noinspection SwitchStatementWithTooFewBranches
      switch (pMatchType)
      {
        case EqualMatch.TYPE:
          return new EqualMatch(pNestedPath, pMatchDetails[0], pMatchDetails[1]);

        default:
          throw new IllegalArgumentException("Matchtype not found " + pMatchType);
      }
    }
    catch (ClassCastException | NoSuchElementException | NumberFormatException cce)
    {
      throw new Exception("Wrong arguments for match " + pMatchType + ": " + Arrays.toString(pMatchDetails));
    }
  }

}
