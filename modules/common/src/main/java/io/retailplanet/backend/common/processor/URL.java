package io.retailplanet.backend.common.processor;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation that defines the URL for a retailplanet service
 *
 * @author w.glanzer, 05.09.2019
 */
@Target({ElementType.TYPE})
public @interface URL
{

  /**
   * @return Name of the target module, where the rest service is located
   */
  ETarget targetModule() default ETarget.__DUMMY__;

  /**
   * Defines all targetable modules
   */
  enum ETarget
  {
    __DUMMY__("http://dummy.localhost.not.resolvable", "http://localhost"),
    BUSINESSTOKEN("http://businesstoken.retailplanet-services.svc.cluster.local:8080", "http://localhost:8088"),
    ELASTICSEARCH("http://elasticsearch.retailplanet-services.svc.cluster.local:8080", "http://localhost:8083"),
    MARKETS("http://markets.retailplanet-services.svc.cluster.local:8080", "http://localhost:8084"),
    METRICS("http://metrics.retailplanet-services.svc.cluster.local:8080", "http://localhost:8085"),
    PRODUCTS("http://products.retailplanet-services.svc.cluster.local:8080", "http://localhost:8086"),
    USERAUTH("http://userauth.retailplanet-services.svc.cluster.local:8080", "http://localhost:8082");

    private String productionURL;
    private String localURL;

    ETarget(String pProductionURL, String pLocalURL)
    {
      productionURL = pProductionURL;
      localURL = pLocalURL;
    }

    @NotNull
    public String getProductionURL()
    {
      return productionURL;
    }

    @NotNull
    public String getLocalURL() //todo remove, if quarkus is finally able to resolve environemnt variables correctly in RESTClient...
    {
      return localURL;
    }
  }

}
