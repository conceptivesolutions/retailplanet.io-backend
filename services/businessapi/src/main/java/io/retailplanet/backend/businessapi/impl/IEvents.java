package io.retailplanet.backend.businessapi.impl;

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
   * Configuration for Service: businessapi
   */
  @IDynamicConfig.Provider
  class DynamicConfProvider implements IDynamicConfig
  {
    private static final String _GROUP_ID = "businessapi";

    @NotNull
    @Override
    public Builder generate()
    {
      return Builder.create()
          .defaultValue("bootstrap.servers", "${KAFKA_SERVERS}");
    }
  }
}
