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
   * @param pIndexName   Name of the index to update
   * @param pClientID    Client from where the products came
   * @param pProductList List of all products to upsert
   */
  void upsertProducts(@NotNull String pIndexName, @NotNull String pClientID, @NotNull Product[] pProductList);

}
