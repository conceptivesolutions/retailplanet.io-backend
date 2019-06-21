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
  @IncomingEvent(autoOffsetReset = "earliest")
  String IN_PRODUCTS_UPSERT = "Product_UPSERT_IN";


}
