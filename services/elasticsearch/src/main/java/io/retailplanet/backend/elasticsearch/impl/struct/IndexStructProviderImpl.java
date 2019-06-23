package io.retailplanet.backend.elasticsearch.impl.struct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author w.glanzer, 23.06.2019
 */
@Singleton
class IndexStructProviderImpl implements IIndexStructProvider
{

  private static final Map<String, String> _MAPPING_CACHE = new HashMap<>();

  @Override
  @NotNull
  public String createIndexName(@NotNull String pClientID, @NotNull String pIndexType)
  {
    // generate an ascii conform identifier
    String asciiClientID = StringUtils.isAsciiPrintable(pClientID) ? pClientID.toLowerCase() :
        Base64.getEncoder().encodeToString(pClientID.getBytes(StandardCharsets.UTF_8)).toLowerCase();

    switch (pIndexType)
    {
      case "product":
        return "products_" + asciiClientID;

      case "market":
        return "markets";

      default:
        throw new IllegalArgumentException("Indextype " + pIndexType + " not found");
    }
  }

  @Override
  @NotNull
  public String createMapping(@NotNull String pIndexType) throws IllegalArgumentException
  {
    switch (pIndexType)
    {
      case "products":
        return _MAPPING_CACHE.computeIfAbsent(pIndexType, pProductsType -> _readFileUnchecked(getClass().getResource("struct_products.json")));

      case "markets":
        return _MAPPING_CACHE.computeIfAbsent(pIndexType, pProductsType -> _readFileUnchecked(getClass().getResource("struct_markets.json")));

      default:
        throw new IllegalArgumentException("Indextype " + pIndexType + " not found");
    }
  }

  /**
   * Reads a file content, but with unchecked exception
   */
  @NotNull
  private String _readFileUnchecked(@NotNull URL pURL)
  {
    try
    {
      return IOUtils.toString(pURL, StandardCharsets.UTF_8);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

}
