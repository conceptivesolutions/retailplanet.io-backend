package io.retailplanet.backend.products.impl.index;

import io.retailplanet.backend.products.impl.struct.Product;
import org.jetbrains.annotations.NotNull;

/**
 * Facade to proxy all index requests
 *
 * @author w.glanzer, 22.06.2019
 */
public interface IIndexFacade
{

  /**
   * Inserts / Updates products in elasticsearch
   *
   * @param pClientID    Client from where the products came
   * @param pProductList List of all products to upsert
   */
  void upsertProducts(@NotNull String pClientID, @NotNull Product[] pProductList);

}
