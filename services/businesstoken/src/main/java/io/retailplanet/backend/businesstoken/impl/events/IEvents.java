package io.retailplanet.backend.businesstoken.impl.events;

import io.retailplanet.backend.common.processor.*;

/**
 * This interface contains all events, incoming and outgoing
 *
 * @author w.glanzer, 11.06.2019
 */
@EventContainer(groupID = "businesstoken")
public interface IEvents
{

  /**
   * Event: a new BusinessToken should be created
   */
  @IncomingEvent
  String IN_BUSINESSTOKEN_CREATE_AUTH = "BusinessToken_CREATE_AUTH_IN";

  /**
   * Event: a new BusinessToken was created
   */
  @IncomingEvent(autoOffsetReset = "earliest")
  String IN_BUSINESSTOKEN_CREATED = "BusinessToken_CREATED_IN";

  /**
   * Event: a BusinessToken was invalidated
   */
  @IncomingEvent
  String IN_BUSINESSTOKEN_INVALIDATED = "BusinessToken_INVALIDATED_IN";

  /**
   * Event: Product should be updated or inserted
   */
  @IncomingEvent
  String IN_PRODUCT_UPSERT_UNAUTH = "Product_UPSERT_UNAUTH_IN";

  /**
   * Event: Market should be updated or inserted
   */
  @IncomingEvent
  String IN_MARKET_UPSERT_UNAUTH = "Market_UPSERT_UNAUTH_IN";

  /**
   * Event: a new BusinessToken was created
   */
  @OutgoingEvent
  String OUT_BUSINESSTOKEN_CREATED = "BusinessToken_CREATED_OUT";

  /**
   * Event: a BusinessToken was invalidated
   */
  @OutgoingEvent
  String OUT_BUSINESSTOKEN_INVALIDATED = "BusinessToken_INVALIDATED_OUT";

  /**
   * Event: Product should be updated or inserted
   */
  @OutgoingEvent
  String OUT_PRODUCT_UPSERT = "Product_UPSERT_OUT";

  /**
   * Event: Market should be updated or inserted
   */
  @OutgoingEvent
  String OUT_MARKET_UPSERT = "Market_UPSERT_OUT";

}
