package io.retailplanet.backend.products.impl;

import io.retailplanet.backend.common.api.comm.*;

/**
 * This interface contains all events, incoming and outgoing
 *
 * @author w.glanzer, 20.06.2019
 */
@EventContainer(groupID = "products")
public interface IEvents
{

  /**
   * Event: Product should be inserted or udpated
   */
  @IncomingEvent
  String IN_PRODUCTS_UPSERT = "Product_UPSERT_IN";

  /**
   * Event: Products should be searched
   */
  @IncomingEvent
  String IN_SEARCH_PRODUCTS = "Search_PRODUCTS_IN";

  /**
   * Event: A search returned a result
   */
  @IncomingEvent
  String IN_INDEX_DOCUMENT_SEARCHRESULT = "Index_DOCUMENT_SEARCHRESULT_IN";

  /**
   * Event: Index document should be inserted / updated
   */
  @OutgoingEvent
  String OUT_INDEX_DOCUMENT_UPSERT = "Index_DOCUMENT_UPSERT_OUT";

  /**
   * Event: Index document should be searched
   */
  @OutgoingEvent
  String OUT_INDEX_DOCUMENT_SEARCH = "Index_DOCUMENT_SEARCH_OUT";

  /**
   * Event: Transfer searched results
   */
  @OutgoingEvent
  String OUT_SEARCH_PRODUCTS_RESULT = "Search_PRODUCTS_RESULT_OUT";

}
