package io.retailplanet.backend.products.impl.index;

import io.retailplanet.backend.common.events.index.DocumentUpsertEvent;
import io.retailplanet.backend.products.impl.IEvents;
import io.retailplanet.backend.products.impl.struct.Product;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonArray;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.stream.Collector;

/**
 * StreamedIndexFacade
 *
 * @author w.glanzer, 22.06.2019
 */
@Singleton
class StreamedIndexFacade implements IIndexFacade
{

  private static final String _INDEX_TYPE = "product";

  @Stream(IEvents.OUT_INDEX_DOCUMENT_UPSERT)
  Emitter<DocumentUpsertEvent> upsertProductsInIndex;

  @Override
  public void upsertProducts(@NotNull String pClientID, @NotNull Product[] pProductList)
  {
    // Build request to index facade
    DocumentUpsertEvent event = new DocumentUpsertEvent()
        .clientID(pClientID)
        .type(_INDEX_TYPE)
        .doc(Arrays.stream(pProductList)
            .map(pProduct -> pProduct.toIndexJSON(pClientID))
            .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

    // fire request
    upsertProductsInIndex.send(event);
  }


}
