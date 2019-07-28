package io.retailplanet.backend.search.impl.events;

import io.retailplanet.backend.common.processor.*;

/**
 * This interface contains all events, incoming and outgoing
 *
 * @author w.glanzer, 11.06.2019
 */
@EventContainer(groupID = "search")
public interface IEvents
{

  /**
   * Event: Product search started
   */
  @OutgoingEvent
  String OUT_SEARCH_PRODUCTS = "Search_PRODUCTS_OUT";

  /**
   * Event: Product search returned results
   */
  @IncomingEvent
  String IN_SEARCH_PRODUCTS_RESULT = "Search_PRODUCTS_RESULT_IN";

}
