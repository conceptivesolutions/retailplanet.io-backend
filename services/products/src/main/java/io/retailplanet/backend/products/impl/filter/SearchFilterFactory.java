package io.retailplanet.backend.products.impl.filter;

import io.retailplanet.backend.products.impl.services.IMarketSearchService;
import io.retailplanet.backend.products.impl.struct.ProductAvailability;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

/**
 * @author w.glanzer, 14.07.2019
 */
@ApplicationScoped
class SearchFilterFactory implements ISearchFilterFactory
{

  @Inject
  @RestClient
  private IMarketSearchService marketSearchService;

  @NotNull
  @Override
  public ISearchFilter create(@NotNull String pType, @NotNull Object pArgumentObject) throws Exception
  {
    try
    {
      //noinspection SwitchStatementWithTooFewBranches
      switch (pType)
      {
        case "geo":
          List<Object> arguments = (List<Object>) pArgumentObject;
          return new GeoSearchFilter(marketSearchService, Collections.singletonList(ProductAvailability.TYPE.AVAILABLE),
                                     Double.parseDouble(String.valueOf(arguments.get(0))), Double.parseDouble(String.valueOf(arguments.get(1))),
                                     Integer.parseInt(String.valueOf(arguments.get(2)))); //todo availability

        default:
          throw new IllegalArgumentException("Type " + pType + " as search filter not found");
      }
    }
    catch (ClassCastException | NoSuchElementException | NumberFormatException e)
    {
      throw new IllegalArgumentException("Filter can not be created with given argument object. (type: " + pType + ", arguments: " + pArgumentObject + ")", e);
    }
    catch (Exception e)
    {
      throw new Exception("Failed to create search filter (type: " + pType + ", arguments: " + pArgumentObject + ")", e);
    }
  }

}
