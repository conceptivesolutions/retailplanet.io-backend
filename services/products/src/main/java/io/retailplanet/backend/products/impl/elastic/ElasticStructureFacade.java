package io.retailplanet.backend.products.impl.elastic;

import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.jetbrains.annotations.NotNull;

import javax.inject.*;

/**
 * @author w.glanzer, 20.06.2019
 */
@Singleton
public class ElasticStructureFacade
{

  private final RestHighLevelClient restClient;

  @Inject
  public ElasticStructureFacade(@NotNull ElasticClientFactory pClientFactory)
  {
    restClient = pClientFactory.createHighLevelClient();
  }

  public boolean hasIndex(@NotNull String pIndexName)
  {
    try
    {
      restClient
          .indices()
          .get(new GetIndexRequest(pIndexName), RequestOptions.DEFAULT);
      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
  }

}
