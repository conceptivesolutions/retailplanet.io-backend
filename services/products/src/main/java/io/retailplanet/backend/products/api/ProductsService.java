package io.retailplanet.backend.products.api;

import io.retailplanet.backend.common.events.product.ProductUpsertEvent;
import io.retailplanet.backend.common.util.*;
import io.retailplanet.backend.products.impl.IEvents;
import io.retailplanet.backend.products.impl.index.IIndexFacade;
import io.retailplanet.backend.products.impl.struct.Product;
import io.vertx.core.json.Json;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Service: Products
 *
 * @author w.glanzer, 20.06.2019
 */
@ApplicationScoped
public class ProductsService
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(ProductsService.class);

  @Inject
  private IIndexFacade indexFacade;

  /**
   * Inserts / Updates a list of products
   *
   * @param pEvent Products to upsert
   */
  @Incoming(IEvents.IN_PRODUCTS_UPSERT)
  public void productUpsert(@Nullable ProductUpsertEvent pEvent)
  {
    if (pEvent == null)
      return;

    String clientID = pEvent.clientID;
    byte[] binContent = pEvent.content;
    if (binContent == null || binContent.length == 0 || Utility.isNullOrEmptyTrimmedString(clientID))
      return;

    try
    {
      // decompress
      String content = ZipUtility.uncompressBase64(binContent);

      // read products
      Product[] products = Json.decodeValue(content, Product[].class);

      // store in index
      indexFacade.upsertProducts(clientID, products);
    }
    catch (Exception e)
    {
      _LOGGER.warn("Failed to upsert product", e);
    }
  }

}
