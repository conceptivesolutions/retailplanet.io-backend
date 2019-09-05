package io.retailplanet.backend.products.impl.services;

import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/elasticsearch")
@RegisterRestClient
@URL(targetModule = "elasticsearch")
public interface IIndexWriteService
{

  /**
   * Inserts / Updates documents in index
   *
   * @param pClientID client that issued this write
   * @param pType     indextype to write to
   * @param pDocument document to insert
   */
  @PUT
  Response upsertDocument(@PathParam("clientID") String pClientID, @PathParam("type") String pType, Object pDocument);

}
