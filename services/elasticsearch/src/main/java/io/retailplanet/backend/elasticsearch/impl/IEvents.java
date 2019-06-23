package io.retailplanet.backend.elasticsearch.impl;

import io.retailplanet.backend.common.api.comm.*;

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
  @IncomingEvent(autoOffsetReset = "earliest")
  String IN_INDEX_DOCUMENT_UPSERT = "Index_DOCUMENT_UPSERT_IN";

}
