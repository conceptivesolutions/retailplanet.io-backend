package io.retailplanet.backend.userauth.impl.events;

import io.retailplanet.backend.common.api.comm.*;

/**
 * This interface contains all events, incoming and outgoing
 *
 * @author w.glanzer, 11.06.2019
 */
@EventContainer(groupID = "userauth")
public interface IEvents
{

  /**
   * Event: BusinessToken should be created
   */
  @IncomingEvent
  String IN_BUSINESSTOKEN_CREATE = "BusinessToken_CREATE_IN";

  /**
   * Event: BusinessToken should be created, client was authorized
   */
  @OutgoingEvent
  String OUT_BUSINESSTOKEN_CREATE_AUTH = "BusinessToken_CREATE_AUTH_OUT";

}
