package io.retailplanet.backend.businessapi.impl;

import io.retailplanet.backend.common.api.comm.*;

/**
 * This interface contains all events, incoming and outgoing
 *
 * @author w.glanzer, 11.06.2019
 */
@EventContainer(groupID = "businessapi")
public interface IEvents
{

  /**
   * Event: BusinessToken should be created
   */
  @OutgoingEvent
  String OUT_BUSINESSTOKEN_CREATE = "BusinessToken_CREATE_OUT";

  /**
   * Event: BusinessToken should be invalidated
   */
  @OutgoingEvent
  String OUT_BUSINESSTOKEN_INVALIDATED = "BusinessToken_INVALIDATED_OUT";

  /**
   * Event: Product should be updated or inserted
   */
  @OutgoingEvent
  String OUT_PRODUCT_UPSERT_UNAUTH = "Product_UPSERT_UNAUTH_OUT";

  /**
   * Event: Indicates, that a BusinessToken was created
   */
  @IncomingEvent
  String IN_BUSINESSTOKEN_CREATED = "BusinessToken_CREATED_IN";

  /**
   * Event: Indicates, that a BusinessToken could not be created
   */
  @IncomingEvent
  String IN_BUSINESSTOKEN_CREATE_FAILED = "BusinessToken_CREATE_FAILED_IN";

}
