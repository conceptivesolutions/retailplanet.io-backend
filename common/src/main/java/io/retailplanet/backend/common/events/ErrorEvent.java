package io.retailplanet.backend.common.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.util.Utility;
import org.jetbrains.annotations.NotNull;

/**
 * Event containing an error message
 *
 * @author w.glanzer, 12.07.2019
 */
@RegisterForReflection
public class ErrorEvent extends AbstractEvent<ErrorEvent>
{

  @JsonProperty
  public String error;

  @NotNull
  public ErrorEvent error(Throwable pError)
  {
    return error(Utility.toString(pError));
  }

  @NotNull
  public ErrorEvent error(String pError)
  {
    error = pError;
    return this;
  }

}