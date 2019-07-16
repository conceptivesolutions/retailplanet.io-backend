package io.retailplanet.backend.markets.api;

import io.retailplanet.backend.common.api.AbstractService;
import io.retailplanet.backend.common.events.index.DocumentUpsertEvent;
import io.retailplanet.backend.common.events.market.MarketUpsertEvent;
import io.retailplanet.backend.common.util.*;
import io.retailplanet.backend.markets.impl.IEvents;
import io.retailplanet.backend.markets.impl.struct.*;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.*;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.stream.Collector;

/**
 * Service: Markets
 *
 * @author w.glanzer, 23.06.2019
 */
@ApplicationScoped
public class MarketsService extends AbstractService
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(MarketsService.class);

  @Stream(IEvents.OUT_INDEX_DOCUMENT_UPSERT)
  Emitter<DocumentUpsertEvent> upsertMarketsInIndex;

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
      DocumentUpsertEvent request = new DocumentUpsertEvent()
          .clientID(clientID)
          .type(IIndexStructure.INDEX_TYPE)
          .doc(Arrays.stream(markets)
                   .map(pMarket -> pMarket.toIndexJSON(clientID))
                   .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

      // fire request
      upsertMarketsInIndex.send(request);
    }
    catch (Exception e)
    {
      _LOGGER.warn("Failed to upsert product", e);
    }
  }
}
