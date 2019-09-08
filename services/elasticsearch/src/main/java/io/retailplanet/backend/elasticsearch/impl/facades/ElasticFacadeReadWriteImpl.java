package io.retailplanet.backend.elasticsearch.impl.facades;

import io.retailplanet.backend.elasticsearch.impl.struct.IIndexStructProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.*;

import javax.inject.*;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Contains read / write implementations for elasticsearch
 *
 * @author w.glanzer, 20.06.2019
 */
@Singleton
class ElasticFacadeReadWriteImpl extends ElasticFacadeReadImpl
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(ElasticFacadeReadWriteImpl.class);

  @Inject
  @Metric(name = "documentUpserts")
  private Counter metricDocumentUpserts;

  @Inject
  @Metric(name = "documentUpserts_failed")
  private Counter metricDocumentUpsertsFailed;

  @Inject
  private IIndexStructProvider structProvider;

  @Override
  public void upsertDocument(@NotNull String pClientID, @NotNull String pIndexType, @NotNull Stream<Pair<String, XContentBuilder>> pContentBuilder) throws Exception
  {
    String indexName = structProvider.createIndexName(pClientID, pIndexType);

    // create index if not exist
    createIndex(pClientID, pIndexType, false);

    // create bulked request
    BulkRequest request = new BulkRequest();
    pContentBuilder
        .map(pContentPair -> {
          try
          {
            return new UpdateRequest()
                .index(indexName)
                .id(pContentPair.getKey())
                .doc(pContentPair.getValue())
                .docAsUpsert(true);
          }
          catch (Exception e)
          {
            _LOGGER.error("Failed to insert product", e);
            return null;
          }
        })
        .filter(Objects::nonNull)
        .forEach(request::add);

    // fire async
    restClient.bulkAsync(request, RequestOptions.DEFAULT, new _BulkListener());
  }

  @Override
  public void createIndex(@NotNull String pClientID, @NotNull String pIndexType, boolean pForceRebuild) throws Exception
  {
    String indexName = structProvider.createIndexName(pClientID, pIndexType);
    boolean present = _hasIndex(indexName);
    if (present && !pForceRebuild) // it is already present and should not be rebuilt
      return;
    else if (present) // it is already present, and rebuild is forced
      _dropIndex(indexName);

    // create new index request
    CreateIndexRequest productIndex = new CreateIndexRequest(indexName)
        .settings(Settings.builder()
                      .put("index.number_of_shards", 3)
                      .put("index.number_of_replicas", 2)
                      .putList("index.store.preload", "nvd", "dvd"))
        .mapping(structProvider.createMapping(pIndexType), XContentType.JSON);

    // Execute
    CreateIndexResponse response = restClient.indices()
        .create(productIndex, RequestOptions.DEFAULT);
    if (!response.isAcknowledged())
      throw new Exception("ElasticSearch did not acknowledge the request");
    else
      _LOGGER.info("Successfully (re)built product index " + indexName);
  }

  @Override
  public boolean hasIndex(@NotNull String pClientID, @NotNull String pIndexType) throws Exception
  {
    return _hasIndex(structProvider.createIndexName(pClientID, pIndexType));
  }

  /**
   * Determines, if a index with the given name already exists
   *
   * @param pIndexName Name of the index
   * @return <tt>true</tt> if an index exists
   */
  private boolean _hasIndex(@NotNull String pIndexName) throws Exception
  {
    try
    {
      restClient
          .indices()
          .get(new GetIndexRequest(pIndexName), RequestOptions.DEFAULT);
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  /**
   * Drops a specific index, does nothing it it does not exist
   *
   * @param pName Name of the index to drop
   */
  private void _dropIndex(@NotNull String pName) throws Exception
  {
    if (_hasIndex(pName))
    {
      restClient.indices().delete(new DeleteIndexRequest(pName), RequestOptions.DEFAULT);
      _LOGGER.info("Dropped index " + pName);
    }
  }

  /**
   * BulkProcessorListener-Impl
   */
  private class _BulkListener implements ActionListener<BulkResponse>
  {
    @Override
    public void onResponse(BulkResponse response)
    {
      int successfullcount = response.getItems().length;
      for (BulkItemResponse item : response.getItems())
        if (item.isFailed())
        {
          successfullcount--;
          _LOGGER.warn("Item failed to update " + item.getId(), item.getFailure().getCause());
        }

      // update metrics
      metricDocumentUpserts.inc(successfullcount);
      metricDocumentUpsertsFailed.inc(response.getItems().length - successfullcount);

      _LOGGER.info(successfullcount + " items successfully updated, " + (response.getItems().length - successfullcount) + " failed, took " + response.getTook());
    }

    @Override
    public void onFailure(Exception e)
    {
      _LOGGER.warn("Failed to execute request ", e);
    }
  }

}
