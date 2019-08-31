package io.retailplanet.backend.clients.base.spi;

import io.retailplanet.backend.clients.base.api.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Reader, that retrieves all markets from a source
 *
 * @author w.glanzer, 31.08.2019
 */
public interface IMarketCrawler
{

  /**
   * @return All available Markets
   */
  @NotNull
  List<CrawledMarket> getMarkets();

  /**
   * Adds the availability to the given products
   *
   * @param pProducts Products, which requested the availability informations
   */
  void includeAvailability(@NotNull List<CrawledProduct> pProducts);

}
