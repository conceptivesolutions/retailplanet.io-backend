package io.retailplanet.backend.elasticsearch.impl.filters;

import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.*;

/**
 * @author w.glanzer, 16.07.2019
 */
@Singleton
class FilterFactoryImpl implements IFilterFactory
{

  @NotNull
  @Override
  public IQueryBuilder interpretFilter(@NotNull String pFilterType, @NotNull String... pFilterDetails) throws Exception
  {
    try
    {
      //noinspection SwitchStatementWithTooFewBranches
      switch (pFilterType)
      {
        case GeoDistanceFilter.TYPE:
          return new GeoDistanceFilter(pFilterDetails[0], Double.valueOf(pFilterDetails[1]), Double.valueOf(pFilterDetails[2]), Integer.valueOf(pFilterDetails[3]));

        default:
          throw new IllegalArgumentException("Filtertype not found " + pFilterType);
      }
    }
    catch (ClassCastException | NoSuchElementException | NumberFormatException cce)
    {
      throw new IllegalArgumentException("Wrong arguments for filter " + pFilterType + ": " + Arrays.toString(pFilterDetails));
    }
  }

}
