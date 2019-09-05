package io.retailplanet.backend.search.impl.services;

import io.retailplanet.backend.common.events.search.*;
import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.*;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("internal/products/search")
@RegisterRestClient
@URL(targetModule = "products")
public interface IProductSearchService
{

  /**
   * Executes product search
   *
   * @param pEvent Search event
   */
  @POST
  SearchProductsResultEvent searchProducts(@Nullable SearchProductsEvent pEvent);

}
