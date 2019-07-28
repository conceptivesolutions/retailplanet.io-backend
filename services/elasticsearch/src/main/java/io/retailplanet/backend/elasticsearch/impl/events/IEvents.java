package io.retailplanet.backend.elasticsearch.impl.events;

import io.retailplanet.backend.common.processor.*;

/**
 * This interface contains all events, incoming and outgoing
 *
 * @author w.glanzer, 22.06.2019
 */
@EventContainer(groupID = "elasticsearch")
public interface IEvents
{

  /**
   * Event: Index document should be inserted / updated
   */
  @IncomingEvent
  String IN_INDEX_DOCUMENT_UPSERT = "Index_DOCUMENT_UPSERT_IN";

  /**
   * Event: Index document should be searched
   */
  @IncomingEvent
  String IN_INDEX_DOCUMENT_SEARCH = "Index_DOCUMENT_SEARCH_IN";

  /**
   * Event: A search returned a result
   */
  @OutgoingEvent
  String OUT_INDEX_DOCUMENT_SEARCHRESULT = "Index_DOCUMENT_SEARCHRESULT_OUT";

}
