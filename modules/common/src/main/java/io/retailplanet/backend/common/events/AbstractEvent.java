package io.retailplanet.backend.common.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.util.Utility;

import java.time.Duration;
import java.util.UUID;

/**
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public abstract class AbstractEvent<S extends AbstractEvent<S>>
{
  /* Specifies how long an event lives in milliseconds */
  public static final long TTL = Utility.isDevMode() ? Duration.ofHours(24).toMillis() : Duration.ofSeconds(3).toMillis();

  @JsonProperty
  public String chainID = UUID.randomUUID().toString();

  public <T extends AbstractEvent> T createAnswer(Class<T> pClazz)
  {
    try
    {
      T event = pClazz.newInstance();
      event.chainID = chainID;
      return event;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
