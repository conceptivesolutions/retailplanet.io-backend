package io.retailplanet.backend.products.api;

import io.retailplanet.backend.common.util.ZipUtility;
import io.retailplanet.backend.products.impl.IEvents;
import io.retailplanet.backend.products.impl.elastic.ElasticStructureFacade;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.NotNull;

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

  @Inject
  private ElasticStructureFacade elasticStructureFacade;

  @Incoming(IEvents.IN_PRODUCTS_UPSERT)
  public void productUpsert(@NotNull JsonObject pJsonObject)
  {
    byte[] binContent = pJsonObject.getBinary("content");
    if (binContent == null || binContent.length == 0)
      return;

    String content = ZipUtility.uncompressBase64(binContent);
    // todo insert in elasticsearch
  }


}
