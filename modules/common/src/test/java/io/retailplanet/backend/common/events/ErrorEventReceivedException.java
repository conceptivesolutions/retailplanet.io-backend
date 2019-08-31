package io.retailplanet.backend.common.events;

import org.jetbrains.annotations.NotNull;

/**
 * Exception thrown in UnitTests if an error event was received
 *
 * @author w.glanzer, 28.07.2019
 */
public class ErrorEventReceivedException extends RuntimeException
{

  public ErrorEventReceivedException(@NotNull ErrorEvent pErrorEvent)
  {
    super(pErrorEvent.error());
  }

}
