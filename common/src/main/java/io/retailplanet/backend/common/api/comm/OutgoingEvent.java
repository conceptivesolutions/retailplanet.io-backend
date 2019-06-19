package io.retailplanet.backend.common.api.comm;

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
  Class<?> keySerializer();

  /**
   * @return Class to deserialize the key
   */
  Class<?> valueSerializer();

}
