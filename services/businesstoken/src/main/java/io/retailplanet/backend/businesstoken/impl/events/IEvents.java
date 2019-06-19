package io.retailplanet.backend.businesstoken.impl.events;

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
   * Event: a new BusinessToken should be created
   */
  String IN_BUSINESSTOKEN_CREATE_AUTH = "BusinessToken_CREATE_AUTH_IN";

  /**
   * Event: a new BusinessToken was created
   */
  String IN_BUSINESSTOKEN_CREATED = "BusinessToken_CREATED_IN";

  /**
   * Event: a new BusinessToken was created
   */
  String OUT_BUSINESSTOKEN_CREATED = "BusinessToken_CREATED_OUT";

  /**
   * Configuration for Service: BusinessToken
   */
  @IDynamicConfig.Provider
  class DynamicConfProvider implements IDynamicConfig
  {
    private static final String _GROUP_ID = "businesstoken";

    @NotNull
    @Override
    public Builder generate()
    {
      return Builder.create()
          .defaultValue("bootstrap.servers", "${KAFKA_SERVERS}")
          .topic(TopicBuilder.createRead(IN_BUSINESSTOKEN_CREATE_AUTH, _GROUP_ID, "latest", StringDeserializer.class, JsonObjectDeserializer.class))
          .topic(TopicBuilder.createRead(IN_BUSINESSTOKEN_CREATED, _GROUP_ID, "earliest", StringDeserializer.class, JsonObjectDeserializer.class))
          .topic(TopicBuilder.createWrite(OUT_BUSINESSTOKEN_CREATED, StringSerializer.class, JsonObjectSerializer.class));
    }
  }
}
