package io.retailplanet.backend.clients.mediamarkt;

import com.google.common.base.Stopwatch;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.retailplanet.backend.clients.base.api.*;
import io.retailplanet.backend.clients.base.impl.util.RetryManager;
import io.retailplanet.backend.clients.base.spi.IMarketCrawler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.json.*;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author w.glanzer, 09.01.2019
 */
@ApplicationScoped
public class MediaMarktMarketCrawler implements IMarketCrawler
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(MediaMarktMarketCrawler.class);
  private static final String _BASE_URL = "https://www.mediamarkt.de";
  private static final String _MEDIAMARKT_AVAILABILITY_URL = "/de/market-selector-list-availability.json";

  @NotNull
  @Override
  public List<CrawledMarket> getMarkets()
  {
    List<CrawledMarket> result = new ArrayList<>();

    try
    {
      JSONObject response = RetryManager.retry(() -> _getAvailabilities("0"), 10, 1000, null, _LOGGER);
      JSONArray markets = response.getJSONArray("markets");
      for (Object market : markets)
      {
        String id = ((JSONObject) market).getString("id");
        String name = ((JSONObject) market).getString("name");
        Float lat = Float.valueOf(((JSONObject) market).getString("lat"));
        Float lng = Float.valueOf(((JSONObject) market).getString("lng"));
        String address = ((JSONObject) market).getString("description");
        address = address.substring("<address>".length(), address.indexOf("</address>")).replaceAll("<br/>", "\n");
        address = StringEscapeUtils.unescapeHtml4(address);
        result.add(new CrawledMarket()
                       .name(name)
                       .id(id)
                       .location(Pair.of(lat, lng))
                       .address(address));
      }
    }
    catch (UnirestException e)
    {
      _LOGGER.warn("Failed to retrieve market list", e);
    }

    return result;
  }

  @Override
  public void includeAvailability(@NotNull List<CrawledProduct> pProducts)
  {
    // Log
    _LOGGER.info("Including availability for " + pProducts.size() + " products...");

    ExecutorService executor = Executors.newWorkStealingPool(8);
    Stopwatch watch = Stopwatch.createStarted();

    // Submit all products to executor
    for (CrawledProduct product : pProducts)
    {
      executor.execute(() -> {
        try
        {
          JSONArray availabilities = RetryManager.retry(() -> _getAvailabilities(product.getID()), 10, 1000, null, _LOGGER)
              .getJSONArray("availabilities");
          for (Object availability : availabilities)
          {
            String outletID = ((JSONObject) availability).getString("id");
            String level = ((JSONObject) availability).getString("level");
            product.availability(outletID, _getType(level), null); //todo quantity
          }
        }
        catch (UnirestException e)
        {
          _LOGGER.warn("Failed to retrieve availability for product " + product.getID());
        }
      });
    }

    try
    {
      // await termination
      executor.shutdown();
      executor.awaitTermination(30, TimeUnit.MINUTES);

      // Log
      _LOGGER.info("Finished including availability for " + pProducts.size() + " products in " + watch.stop().toString());
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException("Failed to retrieve availability: execution took too long", e);
    }
  }

  /**
   * Returns all availabilities to a specific product id
   *
   * @param pCatEntryId product ID (defined as catEntryId)
   * @return JsonObject
   */
  @NotNull
  private JSONObject _getAvailabilities(@NotNull String pCatEntryId) throws UnirestException
  {
    HttpResponse<String> stringResponse = Unirest.get(_BASE_URL + _MEDIAMARKT_AVAILABILITY_URL)
        .queryString("catEntryId", pCatEntryId)
        .asString();
    return _marketToJSON(stringResponse.getBody());
  }

  /**
   * Returns the availability type for a specific level
   *
   * @param pLevel Level returned by page
   * @return availability
   */
  @NotNull
  private static CrawledProduct.Availability _getType(@NotNull String pLevel)
  {
    switch (pLevel)
    {
      case "0":
      case "1":
      case "2":
        return CrawledProduct.Availability.AVAILABLE;
      case "3":
      case "4":
        return CrawledProduct.Availability.ORDERABLE;
      default:
        return CrawledProduct.Availability.NOT_AVAILABLE;
    }
  }

  /**
   * Transforms the current market string to a JsonObject.
   * WTF, the JSON is sometimes invalid -> make it valid
   *
   * @param pString JSON
   * @return JSONObject
   */
  @NotNull
  private static JSONObject _marketToJSON(@NotNull String pString)
  {
    try
    {
      return new JSONObject(pString);
    }
    catch (JSONException ex)
    {
      StringBuilder builder = new StringBuilder(pString);
      builder.insert(builder.length() - 2, "\"\"");
      return new JSONObject(builder.toString());
    }
  }

}
