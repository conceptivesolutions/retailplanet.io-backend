package io.retailplanet.backend.clients.base.impl.upload;

import com.google.common.collect.Maps;
import com.mashape.unirest.http.*;
import io.retailplanet.backend.clients.base.api.*;
import io.retailplanet.backend.common.util.Utility;
import org.jetbrains.annotations.NotNull;
import org.json.*;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
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
  private Instant sessionTokenValidity;

  @Override
  public void init(@NotNull String pHost, @NotNull String pClientID, @NotNull String pClientToken) throws Exception
  {
    host = pHost;

    // Validate host
    if (Utility.isNullOrEmptyTrimmedString(host))
      throw new IllegalArgumentException("Hostname must not be null");

    // Init a new session token
    JSONObject tokenObj = Unirest.get(host + _BUSINESS + "/token/generate")
        .queryString("clientid", pClientID)
        .queryString("token", pClientToken)
        .asJson()
        .getBody()
        .getObject();

    sessionToken = tokenObj.getString("session_token");
    sessionTokenValidity = Instant.parse(tokenObj.getString("valid_until"));

    // Validate session token
    if (Utility.isNullOrEmptyTrimmedString(sessionToken))
      throw new IllegalArgumentException("Failed to retrieve sessionToken. The backend returned null or an empty string");

    // Ensure validity
    _ensureSessionTokenValidity();
  }

  @Override
  public void products(@NotNull List<CrawledProduct> pProducts) throws Exception
  {
    // Ensure sesstion token validity
    _ensureSessionTokenValidity();

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
    // Ensure sesstion token validity
    _ensureSessionTokenValidity();

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
  public void finish() throws Exception
  {
    try
    {
      // Finish
      HttpResponse<String> response = Unirest.delete(host + _BUSINESS + "/token/" + sessionToken)
          .asString();
      if (response.getStatus() != 200)
        throw new Exception(response.getStatusText());
    }
    finally
    {
      // invalidate session token
      sessionToken = null;
      sessionTokenValidity = null;
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
   * Checks the current sessiontoken validity time and throws an exception, if it is invalid
   */
  private void _ensureSessionTokenValidity()
  {
    if (sessionTokenValidity == null)
      throw new NullPointerException("sessionTokenValidity is null");
    if (Instant.now().isAfter(sessionTokenValidity))
      throw new IllegalArgumentException("Session token expired. " + sessionTokenValidity);
  }

}
