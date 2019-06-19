package io.retailplanet.backend.businessapi.impl;

import io.retailplanet.backend.common.api.comm.*;
import io.vertx.kafka.client.serialization.*;
import org.apache.kafka.common.serialization.*;

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
  @OutgoingEvent(keySerializer = StringSerializer.class, valueSerializer = JsonObjectSerializer.class)
  String OUT_BUSINESSTOKEN_CREATE = "BusinessToken_CREATE_OUT";

  /**
   * Event: Indicates, that a BusinessToken was created
   */
  @IncomingEvent(autoOffsetReset = "latest", keyDeserializer = StringDeserializer.class, valueDeserializer = JsonObjectDeserializer.class)
  String IN_BUSINESSTOKEN_CREATED = "BusinessToken_CREATED_IN";

  /**
   * Event: Indicates, that a BusinessToken could not be created
   */
  @IncomingEvent(autoOffsetReset = "latest", keyDeserializer = StringDeserializer.class, valueDeserializer = JsonObjectDeserializer.class)
  String IN_BUSINESSTOKEN_CREATE_FAILED = "BusinessToken_CREATE_FAILED_IN";

}
