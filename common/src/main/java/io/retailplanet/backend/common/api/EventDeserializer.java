package io.retailplanet.backend.common.api;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Deserializing events in Kafka
 *
 * @author w.glanzer, 20.06.2019
 */
@RegisterForReflection
public class EventDeserializer implements Deserializer<JsonObject>
{

  @Override
  public void configure(Map<String, ?> configs, boolean isKey)
  {
    // nothing
  }

  @Override
  public JsonObject deserialize(String topic, byte[] data)
  {
    if (data == null)
      return null;

    return Buffer.buffer(data).toJsonObject();
  }

  @Override
  public void close()
  {
    // nothing
  }

}
