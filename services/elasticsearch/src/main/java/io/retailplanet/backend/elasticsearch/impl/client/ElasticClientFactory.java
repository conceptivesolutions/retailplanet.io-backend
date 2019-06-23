package io.retailplanet.backend.elasticsearch.impl.client;

import io.retailplanet.backend.common.util.Utility;
import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * @author w.glanzer, 20.06.2019
 */
@SuppressWarnings("unused")
    // CDI
class ElasticClientFactory
{

  private static final String ENV_ELASTICSEARCH_SERVERS = "ELASTICSEARCH_SERVERS";

  /**
   * @return Creates a new instance of elasticsearch client
   */
  @Singleton
  @Produces
  public RestHighLevelClient createHighLevelClient()
  {
    RestClientBuilder clientBuilder = RestClient.builder(_readHostsFromEnv())
        .setHttpClientConfigCallback(pHttpAsyncClientBuilder -> pHttpAsyncClientBuilder
            .setKeepAliveStrategy((pHttpResponse, pHttpContext) -> Duration.of(5, ChronoUnit.MINUTES).toMillis()));
    return new RestHighLevelClient(clientBuilder);
  }

  /**
   * @return All Hosts defined in ELASTICSEARCH_SERVERS variable, separated by comma.
   */
  @NotNull
  private HttpHost[] _readHostsFromEnv()
  {
    // Read environment variable
    String elasticServersString = System.getenv(ENV_ELASTICSEARCH_SERVERS);
    if (Utility.isNullOrEmptyTrimmedString(elasticServersString))
      throw new IllegalArgumentException(ENV_ELASTICSEARCH_SERVERS + " is not defined");

    // Parse variable to HttpHosts
    return Stream.of(elasticServersString.split(","))
        .map(HttpHost::create)
        .toArray(HttpHost[]::new);
  }

}
