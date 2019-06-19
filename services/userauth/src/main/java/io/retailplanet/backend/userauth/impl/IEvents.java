package io.retailplanet.backend.userauth.impl;

import io.retailplanet.backend.common.api.IDynamicConfig;
import io.vertx.kafka.client.serialization.*;
import org.apache.kafka.common.serialization.*;
import org.jetbrains.annotations.NotNull;

/**
 * This interface contains all events, incoming and outgoing
 *
 * @author w.glanzer, 11.06.2019
 */
public interface IEvents
{

  /**
   * Event: BusinessToken should be created
   */
  String IN_BUSINESSTOKEN_CREATE = "BusinessToken_CREATE_IN";

  /**
   * Event: BusinessToken should be created, client was authorized
   */
  String OUT_BUSINESSTOKEN_CREATE_AUTH = "BusinessToken_CREATE_AUTH_OUT";

  /**
   * Configuration for Service: userauth
   */
  @IDynamicConfig.Provider
  class DynamicConfProvider implements IDynamicConfig
  {
    private static final String _GROUP_ID = "userauth";

    @NotNull
    @Override
    public Builder generate()
    {
      return Builder.create()
          .defaultValue("bootstrap.servers", "${KAFKA_SERVERS}")
          .topic(TopicBuilder.createRead(IN_BUSINESSTOKEN_CREATE, _GROUP_ID, "latest", StringDeserializer.class, JsonObjectDeserializer.class))
          .topic(TopicBuilder.createWrite(OUT_BUSINESSTOKEN_CREATE_AUTH, StringSerializer.class, JsonObjectSerializer.class));
    }
  }
}
