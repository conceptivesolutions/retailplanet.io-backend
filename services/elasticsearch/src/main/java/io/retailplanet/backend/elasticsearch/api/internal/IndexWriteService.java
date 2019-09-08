package io.retailplanet.backend.elasticsearch.api.internal;

import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.elasticsearch.impl.facades.IIndexFacade;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.common.xcontent.*;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

/**
 * Service: Write operations on Index
 *
 * @author w.glanzer, 22.06.2019
 */
@Path("/internal/elasticsearch")
public class IndexWriteService
{

  @Inject
  private IIndexFacade indexFacade;

  /**
   * Inserts / Updates documents in index
   *
   * @param pClientID client that issued this write
   * @param pType     indextype to write to
   * @param pDocument document to insert
   */
  @PUT
  public Response upsertDocument(@QueryParam("clientID") String pClientID, @QueryParam("type") String pType, Object pDocument) //todo specify Object
  {
    if (Utility.isNullOrEmptyTrimmedString(pClientID) || Utility.isNullOrEmptyTrimmedString(pType) || pDocument == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    try
    {
      indexFacade.upsertDocument(pClientID, pType, _getDocumentsFromDocField(pDocument).stream()
          .map(pJsonObj -> {
            String id = Utility.getString(pJsonObj, "id");
            if (id == null)
              throw new IllegalArgumentException("JSONObject does not have ID value: " + pJsonObj);
            pJsonObj.remove("id"); // remove id, because we already have it in elasticsearch structure afterwards
            return Pair.of(id, _toContentBuilder(pJsonObj));
          }));
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to update index with type " + pType + " for client " + pClientID, e);
    }

    return Response.ok().build();
  }

  /**
   * Returns a list of all documents, that should be inserted / udpated / deleted
   *
   * @param pEvent "doc" field of event
   * @return all documents
   */
  @NotNull
  private List<Map<String, Object>> _getDocumentsFromDocField(@NotNull Object pEvent) //todo
  {
    if (pEvent instanceof List)
      return (List<Map<String, Object>>) pEvent;
    else
      throw new IllegalArgumentException("'doc' does not have correct type: " + pEvent.getClass());
  }

  /**
   * Creates an elasticsearch content for vertx json object
   *
   * @param pContent JSON
   * @return Content for elasticsearch
   */
  @NotNull
  private XContentBuilder _toContentBuilder(@NotNull Map<String, Object> pContent)
  {
    try
    {
      XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
      try (XContentParser parser = XContentFactory.xContent(XContentType.JSON)
          .createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, JsonbBuilder.create().toJson(pContent)))
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
