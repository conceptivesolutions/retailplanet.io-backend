package io.retailplanet.backend.markets.api;

import io.retailplanet.backend.common.events.index.DocumentUpsertEvent;
import io.retailplanet.backend.common.events.market.MarketUpsertEvent;
import io.retailplanet.backend.common.util.*;
import io.retailplanet.backend.markets.impl.events.*;
import io.retailplanet.backend.markets.impl.struct.*;
import io.vertx.core.json.*;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collector;

/**
 * Service: Markets
 *
 * @author w.glanzer, 23.06.2019
 */
@ApplicationScoped
public class MarketsService
{

  @Inject
  private IEventFacade eventFacade;

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
      eventFacade.sendDocumentUpsertEvent(request);
    }
    catch (Exception e)
    {
      eventFacade.notifyError(pEvent, "Failed to upsert market", e);
    }
  }
}
