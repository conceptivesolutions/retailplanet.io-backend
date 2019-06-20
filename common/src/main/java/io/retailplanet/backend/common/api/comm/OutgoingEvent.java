package io.retailplanet.backend.common.api.comm;

import io.retailplanet.backend.common.api.EventSerializer;
import org.apache.kafka.common.serialization.*;

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
   * @return Class to serialize the key
   */
  Class<? extends Serializer> keySerializer() default StringSerializer.class;

  /**
   * @return Class to deserialize the key
   */
  Class<? extends Serializer> valueSerializer() default EventSerializer.class;

}
