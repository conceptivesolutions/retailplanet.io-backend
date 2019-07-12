package io.retailplanet.backend.markets.impl.index;

import io.retailplanet.backend.common.events.index.DocumentUpsertEvent;
import io.retailplanet.backend.markets.impl.IEvents;
import io.retailplanet.backend.markets.impl.struct.Market;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonArray;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.stream.Collector;

/**
 * StreamedIndexFacade
 *
 * @author w.glanzer, 23.06.2019
 */
@Singleton
class StreamedIndexFacade implements IIndexFacade
{

  private static final String _INDEX_TYPE = "market";

  @Stream(IEvents.OUT_INDEX_DOCUMENT_UPSERT)
  Emitter<DocumentUpsertEvent> upsertMarketsInIndex;

  @Override
  public void upsertMarkets(@NotNull String pClientID, @NotNull Market[] pMarketList)
  {
    // Build request to index facade
    DocumentUpsertEvent request = new DocumentUpsertEvent()
        .clientID(pClientID)
        .type(_INDEX_TYPE)
        .doc(Arrays.stream(pMarketList)
                 .map(pMarket -> pMarket.toIndexJSON(pClientID))
                 .collect(Collector.of(JsonArray::new, JsonArray::add, JsonArray::addAll)));

    // fire request
    upsertMarketsInIndex.send(request);
  }


}
