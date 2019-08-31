package io.retailplanet.backend.clients.base.spi;

import io.retailplanet.backend.clients.base.api.CrawledProduct;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Reader, which extracts Products from a source
 *
 * @author w.glanzer, 31.08.2019
 */
public interface IProductCrawler
{

  /**
   * Reads all Products from an existing source
   *
   * @param pProductConsumer Object, which consumes products (async)
   */
  void read(@NotNull Consumer<CrawledProduct> pProductConsumer);

}
