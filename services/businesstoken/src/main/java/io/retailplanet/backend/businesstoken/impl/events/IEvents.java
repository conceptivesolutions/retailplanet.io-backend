package io.retailplanet.backend.businesstoken.impl.events;

import io.retailplanet.backend.common.api.comm.*;

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
   * Event: a new BusinessToken was created
   */
  @OutgoingEvent
  String OUT_BUSINESSTOKEN_CREATED = "BusinessToken_CREATED_OUT";

}
