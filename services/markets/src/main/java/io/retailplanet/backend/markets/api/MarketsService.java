package io.retailplanet.backend.markets.api;

import io.retailplanet.backend.common.util.*;
import io.retailplanet.backend.markets.impl.IEvents;
import io.retailplanet.backend.markets.impl.index.IIndexFacade;
import io.retailplanet.backend.markets.impl.struct.Market;
import io.vertx.core.json.*;
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
   * @param pJsonObject Markets to upsert
   */
  @Incoming(IEvents.IN_MARKETS_UPSERT)
  public void productUpsert(@Nullable JsonObject pJsonObject)
  {
    if (pJsonObject == null)
      return;

    String clientID = pJsonObject.getString("clientid");
    byte[] binContent = pJsonObject.getBinary("content");
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
