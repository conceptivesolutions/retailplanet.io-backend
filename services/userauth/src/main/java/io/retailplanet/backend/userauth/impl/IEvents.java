package io.retailplanet.backend.userauth.impl;

import io.retailplanet.backend.common.api.comm.*;
import io.vertx.kafka.client.serialization.*;
import org.apache.kafka.common.serialization.*;

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
  @IncomingEvent(autoOffsetReset = "latest", keyDeserializer = StringDeserializer.class, valueDeserializer = JsonObjectDeserializer.class)
  String IN_BUSINESSTOKEN_CREATE = "BusinessToken_CREATE_IN";

  /**
   * Event: BusinessToken should be created, client was authorized
   */
  @OutgoingEvent(keySerializer = StringSerializer.class, valueSerializer = JsonObjectSerializer.class)
  String OUT_BUSINESSTOKEN_CREATE_AUTH = "BusinessToken_CREATE_AUTH_OUT";

}
