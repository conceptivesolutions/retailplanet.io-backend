package io.retailplanet.backend.elasticsearch.api;

import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.elasticsearch.impl.IEvents;
import io.retailplanet.backend.elasticsearch.impl.facades.IIndexFacade;
import io.vertx.core.json.*;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.elasticsearch.common.xcontent.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service: Index
 *
 * @author w.glanzer, 22.06.2019
 */
@ApplicationScoped
public class IndexService
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(IndexService.class);

  @Inject
  private IIndexFacade indexFacade;

  /**
   * Executes the Index_DOCUMENT_UPSERT event, and inserts / updates documents in index
   *
   * @param pJsonObject Event
   */
  @Incoming(IEvents.IN_INDEX_DOCUMENT_UPSERT)
  public void documentUpsert(@Nullable JsonObject pJsonObject)
  {
    if (pJsonObject == null)
      return;

    String clientid = pJsonObject.getString("clientid");
    String type = pJsonObject.getString("type");
    Object doc = pJsonObject.getValue("doc");
    if (Utility.isNullOrEmptyTrimmedString(clientid) || Utility.isNullOrEmptyTrimmedString(type) || doc == null)
      return;

    try
    {
      indexFacade.upsertDocument(clientid, type, _getDocumentsFromDocField(doc).stream()
          .map(pJsonObj -> {
            String id = pJsonObj.getString("id");
            if (id == null)
              throw new IllegalArgumentException("JSONObject does not have ID value: " + pJsonObj);
            pJsonObj.remove("id"); // remove id, because we already have it in elasticsearch structure afterwards
            return Pair.of(id, _toContentBuilder(pJsonObj));
          }));
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to update index with type " + type + " for client " + clientid, e);
    }
  }

  /**
   * Returns a list of all documents, that should be inserted / udpated / deleted
   *
   * @param pEvent "doc" field of event
   * @return all documents
   */
  @NotNull
  private List<JsonObject> _getDocumentsFromDocField(@NotNull Object pEvent)
  {
    if (pEvent instanceof JsonObject)
      return Collections.singletonList((JsonObject) pEvent);
    else if (pEvent instanceof JsonArray)
      return (List) ((JsonArray) pEvent).stream().collect(Collectors.toList());
    else
      throw new IllegalArgumentException("'doc' does not have correct type: " + pEvent.getClass());
  }

  /**
   * Creates an elasticsearch content for vertx json object
   *
   * @param pJsonObject JSON
   * @return Content for elasticsearch
   */
  @NotNull
  private XContentBuilder _toContentBuilder(@NotNull JsonObject pJsonObject)
  {
    try
    {
      XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
      try (XContentParser parser = XContentFactory.xContent(XContentType.JSON)
          .createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, pJsonObject.encode()))
      {
        builder.copyCurrentStructure(parser);
      }
      return builder;
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed to encode jsonobject to elasticsearch content builder", e);
    }
  }

}
