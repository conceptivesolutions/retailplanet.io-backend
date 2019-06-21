package io.retailplanet.backend.products.api;

import io.quarkus.runtime.StartupEvent;
import io.retailplanet.backend.products.impl.elastic.ElasticStructureFacade;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Service: Products
 *
 * @author w.glanzer, 20.06.2019
 */
@ApplicationScoped
class ProductsService
{

  @Inject
  private ElasticStructureFacade elasticStructureFacade;

  void onStart(@Observes StartupEvent pEvent)
  {
    System.out.println(elasticStructureFacade.hasIndex("nix"));
  }

}
