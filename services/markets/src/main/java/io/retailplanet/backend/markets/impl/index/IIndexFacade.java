package io.retailplanet.backend.markets.impl.index;

import io.retailplanet.backend.markets.impl.struct.Market;
import org.jetbrains.annotations.NotNull;

/**
 * Facade to proxy all index requests
 *
 * @author w.glanzer, 23.06.2019
 */
public interface IIndexFacade
{

  /**
   * Inserts / Updates markets in elasticsearch
   *
   * @param pClientID   Client from where the markets came
   * @param pMarketList List of all markets to upsert
   */
  void upsertMarkets(@NotNull String pClientID, @NotNull Market[] pMarketList);

}
