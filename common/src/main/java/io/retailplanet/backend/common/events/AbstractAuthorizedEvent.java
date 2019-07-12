package io.retailplanet.backend.common.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Event with "authorized" property
 *
 * @author w.glanzer, 11.07.2019
 */
public abstract class AbstractAuthorizedEvent<S extends AbstractAuthorizedEvent<S>> extends AbstractEvent<S>
{

  @JsonProperty(defaultValue = "false")
  public boolean authorized;

  @NotNull
  public S authorized(boolean pAuthorized)
  {
    authorized = pAuthorized;
    return (S) this;
  }

}
