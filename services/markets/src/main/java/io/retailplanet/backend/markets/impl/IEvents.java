package io.retailplanet.backend.markets.impl;

import io.retailplanet.backend.common.api.comm.*;

/**
 * This interface contains all events, incoming and outgoing
 *
 * @author w.glanzer, 20.06.2019
 */
@EventContainer(groupID = "markets")
public interface IEvents
{

  /**
   * Event: Markets should be inserted or udpated
   */
  @IncomingEvent
  String IN_MARKETS_UPSERT = "Market_UPSERT_IN";

  /**
   * Event: Markets should be searched
   */
  @IncomingEvent
  String IN_MARKETS_SEARCH = "Search_MARKETS_IN";

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
   * Event: Market search result
   */
  @OutgoingEvent
  String OUT_MARKETS_SEARCHRESULT = "Search_MARKET_RESULT_OUT";

}
