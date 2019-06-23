package io.retailplanet.backend.products.impl.index;

import io.retailplanet.backend.products.impl.IEvents;
import io.retailplanet.backend.products.impl.struct.Product;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.*;
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
  Emitter<JsonObject> upsertProductsInIndex;

  @Override
  public void upsertProducts(@NotNull String pIndexName, @NotNull String pClientID, @NotNull Product[] pProductList)
  {
    // Build request to index facade
    JsonObject request = new JsonObject()
        .put("index", pIndexName)
        .put("type", _INDEX_TYPE)
        .put("doc", Arrays.stream(pProductList)
            .map(pProduct -> pProduct.toJSON(pClientID))
            .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

    // fire request
    upsertProductsInIndex.send(request);
  }


}
