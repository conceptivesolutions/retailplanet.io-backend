package io.retailplanet.backend.common.processor;

import io.retailplanet.backend.common.events.EventSerializer;
import org.apache.kafka.common.serialization.Serializer;

import java.lang.annotation.*;

/**
 * Event: Outgoing
 *
 * @author w.glanzer, 19.06.2019
 */
@Target({ElementType.FIELD})
public @interface OutgoingEvent
{

  /**
   * @return Class to deserialize the key
   */
  Class<? extends Serializer> valueSerializer() default EventSerializer.class;

}
