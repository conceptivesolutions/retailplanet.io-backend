package io.retailplanet.backend.products.impl.elastic;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;

import javax.inject.Singleton;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author w.glanzer, 20.06.2019
 */
@Singleton
public class ElasticClientFactory
{

  public RestHighLevelClient createHighLevelClient()
  {
    return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")) //todo
                                       .setHttpClientConfigCallback(pHttpAsyncClientBuilder -> pHttpAsyncClientBuilder
                                           .setKeepAliveStrategy((pHttpResponse, pHttpContext) -> Duration.of(5, ChronoUnit.MINUTES).toMillis())));
  }

}
