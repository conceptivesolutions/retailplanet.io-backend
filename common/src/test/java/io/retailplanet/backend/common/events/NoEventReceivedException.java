package io.retailplanet.backend.common.events;

/**
 * Exception, if no event was received
 *
 * @author w.glanzer, 27.07.2019
 */
public class NoEventReceivedException extends RuntimeException
{

  public NoEventReceivedException(String message)
  {
    super(message);
  }

}
