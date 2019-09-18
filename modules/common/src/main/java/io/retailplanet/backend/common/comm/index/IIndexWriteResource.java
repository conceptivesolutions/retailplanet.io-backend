package io.retailplanet.backend.common.comm.index;

import javax.ws.rs.*;

/**
 * @author w.glanzer, 05.09.2019
 */
public interface IIndexWriteResource
{

  /**
   * Inserts / Updates documents in index
   *
   * @param pClientID client that issued this write
   * @param pType     indextype to write to
   * @param pDocument document to insert
   */
  @PUT
  void upsertDocument(@QueryParam("clientID") String pClientID, @QueryParam("type") String pType, Object pDocument);

}
