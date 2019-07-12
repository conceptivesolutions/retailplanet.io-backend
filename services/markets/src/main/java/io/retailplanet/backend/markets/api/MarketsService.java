package io.retailplanet.backend.markets.api;

import io.retailplanet.backend.common.events.market.MarketUpsertEvent;
import io.retailplanet.backend.common.util.*;
import io.retailplanet.backend.markets.impl.IEvents;
import io.retailplanet.backend.markets.impl.index.IIndexFacade;
import io.retailplanet.backend.markets.impl.struct.Market;
import io.vertx.core.json.Json;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Service: Markets
 *
 * @author w.glanzer, 23.06.2019
 */
@ApplicationScoped
public class MarketsService
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(MarketsService.class);

  @Inject
  private IIndexFacade indexFacade;

  /**
   * Inserts / Updates a list of markets
   *
   * @param pEvent Markets to upsert
   */
  @Incoming(IEvents.IN_MARKETS_UPSERT)
  public void marketUpsert(@Nullable MarketUpsertEvent pEvent)
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

      // read markets
      Market[] markets = Json.decodeValue(content, Market[].class);

      // store in index
      indexFacade.upsertMarkets(clientID, markets);
    }
    catch (Exception e)
    {
      _LOGGER.warn("Failed to upsert product", e);
    }
  }
}
