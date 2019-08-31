package io.retailplanet.backend.clients.base.impl.upload;

import com.google.common.collect.Maps;
import com.mashape.unirest.http.*;
import io.retailplanet.backend.clients.base.api.*;
import org.jetbrains.annotations.NotNull;
import org.json.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executes REST Calls to Backend
 *
 * @author w.glanzer, 09.04.2019
 */
@ApplicationScoped
public class BusinessRestFacadeImpl implements IBusinessRestFacade.IUpload
{

  private static final String _BUSINESS = "/business";
  private String host;
  private String sessionToken;

  @Override
  public void productsInit(@NotNull String pHost, @NotNull String pClientID, @NotNull String pClientToken) throws Exception
  {
    _setHost(pHost);

    // Init
    sessionToken = Unirest.get(host + _BUSINESS + "/token/generate")
        .queryString("clientid", pClientID)
        .queryString("token", pClientToken)
        .asJson()
        .getBody()
        .getObject()
        .getString("session_token");
  }

  @Override
  public void products(@NotNull List<CrawledProduct> pProducts) throws Exception
  {
    // revalidate host and sessiontoken
    _setHost(host);

    // to jsonNode
    JSONArray root = new JSONArray();
    pProducts.stream()
        .map(this::_toNode)
        .forEach(root::put);

    // Execute
    HttpResponse<String> response = Unirest.put(host + _BUSINESS + "/product")
        .header("Content-Encoding", "gzip")
        .header("Content-Type", "application/json")
        .header("session_token", sessionToken)
        .body(root)
        .asString();
    if (response.getStatus() != 200)
      throw new Exception(response.getStatusText());
  }

  @Override
  public void markets(@NotNull List<CrawledMarket> pMarkets) throws Exception
  {
    // revalidate host and sessiontoken
    _setHost(host);

    // to jsonNode
    JSONArray root = new JSONArray();
    pMarkets.stream()
        .map(this::_toNode)
        .forEach(root::put);

    // Execute
    HttpResponse<String> response = Unirest.put(host + _BUSINESS + "/market")
        .header("Content-Encoding", "gzip")
        .header("Content-Type", "application/json")
        .header("session_token", sessionToken)
        .body(root)
        .asString();
    if (response.getStatus() != 200)
      throw new Exception(response.getStatusText());
  }

  @Override
  public void productsFlush() throws Exception
  {
    try
    {
      // Flush
      HttpResponse<String> response = Unirest.delete(host + _BUSINESS + "/token/" + sessionToken)
          .asString();
      if (response.getStatus() != 200)
        throw new Exception(response.getStatusText());
    }
    finally
    {
      // invalidate session token
      sessionToken = null;
    }
  }

  @NotNull
  private JSONObject _toNode(@NotNull CrawledProduct pProduct)
  {
    return new JSONObject()
        .put("name", pProduct.getName())
        .put("id", pProduct.getID())
        .put("category", pProduct.getCategory())
        .put("url", pProduct.getURL())
        .put("price", pProduct.getPrice())
        .put("previews", pProduct.getPreviews())
        .put("additionalInfos", pProduct.getAdditionalInfos())
        .put("availability", pProduct.getAvailability().entrySet().stream()
            .map(pEntry -> Maps.immutableEntry(pEntry.getKey(), new JSONObject()
                .put("type", pEntry.getValue().getKey())
                .put("quantity", pEntry.getValue().getValue())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  @NotNull
  private JSONObject _toNode(@NotNull CrawledMarket pMarket)
  {
    return new JSONObject()
        .put("name", pMarket.getName())
        .put("id", pMarket.getID())
        .put("lat", pMarket.getLocation() != null ? pMarket.getLocation().getKey() : null)
        .put("lng", pMarket.getLocation() != null ? pMarket.getLocation().getValue() : null)
        .put("address", pMarket.getAddress());
  }

  /**
   * Sets a new hostname
   *
   * @param pHost Host
   */
  private void _setHost(@NotNull String pHost)
  {
    if (sessionToken != null && !Objects.equals(host, pHost))
      throw new RuntimeException("Another request is already running. The RestFacade could only be used to execute one call at the same time!");
    if (pHost.trim().isEmpty())
      throw new IllegalArgumentException("Hostname must not be empty");
    host = pHost;
  }

}
