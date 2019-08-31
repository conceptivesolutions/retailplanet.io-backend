package io.retailplanet.backend.clients.mediamarkt;

import com.google.common.base.Stopwatch;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.retailplanet.backend.clients.base.api.*;
import io.retailplanet.backend.clients.base.impl.util.*;
import io.retailplanet.backend.clients.base.spi.IMarketCrawler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.json.*;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

  @Inject
  public MediaMarktMarketCrawler()
  {
    Unirest.setHttpClient(HttpClients.custom()
                              .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLUtility.createTrustAllContext()))
                              .build());
  }

  @NotNull
  @Override
  public List<CrawledMarket> getMarkets()
  {
    List<CrawledMarket> result = new ArrayList<>();

    try
    {
      JSONObject response = RetryManager.retry(() -> {
        HttpResponse<String> stringResponse = Unirest.get(_BASE_URL + _MEDIAMARKT_AVAILABILITY_URL)
            .queryString("catEntryId", "0")
            .asString();
        return _marketToJSON(stringResponse.getBody());
      }, 10, 1000, null, _LOGGER);

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
          HttpResponse<JsonNode> response = RetryManager.retry(() -> Unirest.get(_BASE_URL + _MEDIAMARKT_AVAILABILITY_URL)
              .queryString("catEntryId", product.getID())
              .asJson(), 10, 1000, null, _LOGGER);

          JSONArray availabilities = response.getBody().getObject()
              .getJSONArray("availabilities");
          for (Object availability : availabilities)
          {
            String outletID = ((JSONObject) availability).getString("id");
            String level = ((JSONObject) availability).getString("level");
            product.availability(outletID, _getType(level), null);
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

  @NotNull
  private static CrawledProduct.Availability _getType(@NotNull String pLevel)
  {
    switch (pLevel)
    {
      case "0":
      case "1": // auf lager
      case "2": // geringer bestand
        return CrawledProduct.Availability.AVAILABLE;
      case "3":
      case "4":
        return CrawledProduct.Availability.ORDERABLE;
      default:
        return CrawledProduct.Availability.NOT_AVAILABLE;
    }
  }

  /**
   * Wandelt den übergebenen Market-String in ein JsonObject um.
   * WTF, das JSON ist manchmal ungültig -> gültig machen
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
