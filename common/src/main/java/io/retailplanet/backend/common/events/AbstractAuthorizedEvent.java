package io.retailplanet.backend.common.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jetbrains.annotations.NotNull;

/**
 * Event with "authorized" property
 *
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public abstract class AbstractAuthorizedEvent<S extends AbstractAuthorizedEvent<S>> extends AbstractEvent<S>
{

  @JsonProperty(defaultValue = "false")
  boolean authorized;

  @NotNull
  public S authorized(boolean pAuthorized)
  {
    authorized = pAuthorized;
    return (S) this;
  }

  /**
   * @return value of 'authorized' field
   */
  public boolean authorized()
  {
    return authorized;
  }

}
