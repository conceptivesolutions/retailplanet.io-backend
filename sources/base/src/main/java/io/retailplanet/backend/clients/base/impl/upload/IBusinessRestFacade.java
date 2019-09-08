package io.retailplanet.backend.clients.base.impl.upload;

import io.retailplanet.backend.clients.base.api.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Rest-Client to manage communication with our backend
 *
 * @author w.glanzer, 09.04.2019
 */
public interface IBusinessRestFacade
{

  /**
   * All Facades to manage upload
   */
  interface IUpload
  {

    /**
     * Initiate Product Upload
     *
     * @param pClientID    ClientID
     * @param pClientToken ClientToken
     */
    void init(@NotNull String pTokenEndpoint, @NotNull String pProductEndpoint, @NotNull String pMarketEndpoint,
              @NotNull String pClientID, @NotNull String pClientToken) throws Exception;

    /**
     * Upload Products
     *
     * @param pProducts List of Products
     */
    void products(@NotNull List<CrawledProduct> pProducts) throws Exception;

    /**
     * Upload Markets
     *
     * @param pMarkets List of Products
     */
    void markets(@NotNull List<CrawledMarket> pMarkets) throws Exception;

    /**
     * Finishes the current transaction
     */
    void finish() throws Exception;

  }

}
