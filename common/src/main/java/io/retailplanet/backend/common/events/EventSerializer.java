package io.retailplanet.backend.common.events;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.*;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.*;

import java.util.Map;

/**
 * Serializing events in Kafka
 *
 * @author w.glanzer, 20.06.2019
 */
@RegisterForReflection
public class EventSerializer implements Serializer<AbstractEvent<?>>
{

  static final String _EVENT_TYPE_KEY = "_eventtype";
  private static final Logger _LOGGER = LoggerFactory.getLogger(EventSerializer.class);

  @Override
  public void configure(Map<String, ?> configs, boolean isKey)
  {
    // nothing
  }

  @Override
  public byte[] serialize(String topic, AbstractEvent data)
  {
    // Logging
    _LOGGER.info("Sending message " + (data == null ? "<null>" : "'" + data.chainID + "'") + " to kafka topic " + topic);

    if (data == null)
      return null;

    JsonObject json = Json.encodeToBuffer(data).toJsonObject();

    // enrich with class name
    json.put(_EVENT_TYPE_KEY, data.getClass().getName());

    return json.encode().getBytes();
  }

  @Override
  public void close()
  {
    // nothing
  }

}
