package io.retailplanet.backend.common.events.exceptions;

/**
 * Exception which will be logged (or thrown), if an ErrorEvent was raised
 *
 * @author w.glanzer, 28.07.2019
 */
public class ErrorEventException extends Exception
{
  public ErrorEventException(String message)
  {
    super(message);
  }

  public ErrorEventException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ErrorEventException(Throwable cause)
  {
    super(cause);
  }
}
