package io.retailplanet.backend.common.api.comm;

import io.retailplanet.backend.common.api.EventDeserializer;
import org.apache.kafka.common.serialization.*;

import java.lang.annotation.*;

/**
 * Event: Incoming
 *
 * @author w.glanzer, 19.06.2019
 */
@Target({ElementType.FIELD})
public @interface IncomingEvent
{

  /**
   * Determines what should happen, if Kafka has no offset for this group
   *
   * @return "latest", "earliest"
   */
  String autoOffsetReset() default "latest";

  /**
   * @return Class to deserialize the key
   */
  Class<? extends Deserializer> keyDeserializer() default StringDeserializer.class;

  /**
   * @return Class to deserialize the value
   */
  Class<? extends Deserializer> valueDeserializer() default EventDeserializer.class;

}
