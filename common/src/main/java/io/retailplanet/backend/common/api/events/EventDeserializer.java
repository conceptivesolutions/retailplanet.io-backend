package io.retailplanet.backend.common.api.events;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.*;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.*;

import java.util.*;

/**
 * Deserializing events in Kafka
 *
 * @author w.glanzer, 20.06.2019
 */
@RegisterForReflection
public class EventDeserializer implements Deserializer<AbstractEvent>
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(EventDeserializer.class);
  private static final Map<Object, Class<AbstractEvent>> _CACHE = new HashMap<>();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey)
  {
    // nothing
  }

  @Override
  public AbstractEvent deserialize(String topic, byte[] data)
  {
    if (data == null)
      return null;

    JsonObject json = Buffer.buffer(data).toJsonObject();

    Object className = json.remove(EventSerializer._EVENT_TYPE_KEY);
    if (className == null)
      return null;

    try
    {
      return Json.decodeValue(json.toBuffer(), _CACHE.computeIfAbsent(className, pName -> {
        try
        {
          return (Class<AbstractEvent>) Class.forName(pName.toString());
        }
        catch (Exception e)
        {
          _LOGGER.error("Failed to create event class " + pName, e);
          throw new RuntimeException(e);
        }
      }));
    }
    catch (Throwable e)
    {
      _LOGGER.error("Failed to decode value", e);
      return null;
    }
  }

  @Override
  public void close()
  {
    // nothing
  }

}
