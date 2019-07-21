package io.retailplanet.backend.products.api;

import io.retailplanet.backend.common.events.index.DocumentUpsertEvent;
import io.retailplanet.backend.common.events.product.ProductUpsertEvent;
import io.retailplanet.backend.common.util.*;
import io.retailplanet.backend.products.impl.events.*;
import io.retailplanet.backend.products.impl.struct.Product;
import io.vertx.core.json.*;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collector;

import static io.retailplanet.backend.products.impl.struct.IIndexStructure.INDEX_TYPE;

/**
 * Service: Products
 *
 * @author w.glanzer, 20.06.2019
 */
@ApplicationScoped
public class ProductsService
{

  @Inject
  private IEventFacade eventFacade;

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

    eventFacade.trace(pEvent, () -> {
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
        DocumentUpsertEvent event = pEvent.createAnswer(DocumentUpsertEvent.class)
            .clientID(clientID)
            .type(INDEX_TYPE)
            .doc(Arrays.stream(products)
                     .map(pProduct -> pProduct.toIndexJSON(clientID))
                     .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

        // fire request
        eventFacade.sendDocumentUpsertEvent(event);
      }
      catch (Exception e)
      {
        eventFacade.notifyError(pEvent, "Failed to upsert product", e);
      }

      return;
    });
  }

}
