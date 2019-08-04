package io.retailplanet.backend.common.events;

import com.fasterxml.jackson.annotation.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.util.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Event containing an error message
 *
 * @author w.glanzer, 12.07.2019
 */
@RegisterForReflection
public class ErrorEvent extends AbstractEvent<ErrorEvent>
{

  @JsonProperty(required = true)
  SerializedExceptionContent content;

  @NotNull
  public ErrorEvent error(@NotNull Throwable pError)
  {
    content = pError instanceof SerializedException ? ((SerializedException) pError).content : new SerializedExceptionContent(pError);
    return this;
  }

  /**
   * @return value of 'error' field
   */
  @NotNull
  public SerializedException error()
  {
    return new SerializedException(content);
  }

  @RegisterForReflection
  public static class SerializedExceptionContent
  {
    @JsonProperty
    public String serviceID;

    @JsonProperty
    public String error;

    @JsonProperty
    public SerializedExceptionContent serializedCause;

    @JsonProperty
    public List<SerializedStackTraceElement> serializedStackTrace;

    @JsonCreator
    public SerializedExceptionContent()
    {
    }

    private SerializedExceptionContent(@NotNull Throwable pException)
    {
      serviceID = Utility.getServiceID();
      error = pException.getMessage();
      if (pException.getCause() != null)
        serializedCause = pException.getCause() instanceof SerializedException ? ((SerializedException) pException.getCause()).content : new SerializedExceptionContent(pException.getCause());
      serializedStackTrace = Arrays.stream(pException.getStackTrace())
          .map(SerializedStackTraceElement::new)
          .collect(Collectors.toList());
    }

  }

  @RegisterForReflection
  public static class SerializedStackTraceElement
  {
    @JsonProperty
    public String declaringClass;

    @JsonProperty
    public String methodName;

    @JsonProperty
    public String fileName;

    @JsonProperty
    public int lineNumber;

    @JsonCreator
    public SerializedStackTraceElement()
    {
    }

    private SerializedStackTraceElement(@NotNull StackTraceElement pElement)
    {
      declaringClass = pElement.getClassName();
      methodName = pElement.getMethodName();
      fileName = pElement.getFileName();
      lineNumber = pElement.getLineNumber();
    }

    @NotNull
    private StackTraceElement toElement()
    {
      return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }
  }

  public static class SerializedException extends RuntimeException
  {
    private SerializedExceptionContent content;

    public SerializedException(@NotNull SerializedExceptionContent pContent)
    {
      super(pContent.error, pContent.serializedCause != null ? new SerializedException(pContent.serializedCause) : null);
      content = pContent;
      if (content.serializedStackTrace != null)
      {
        StackTraceElement[] trace = content.serializedStackTrace.stream()
            .map(SerializedStackTraceElement::toElement)
            .toArray(StackTraceElement[]::new);
        setStackTrace(trace);
      }
    }

    @Override
    public String toString()
    {
      String s = getClass().getName();
      String message = getLocalizedMessage();
      message = (message != null) ? (s + ": " + message) : s;
      return "[" + content.serviceID + "] -> " + message;
    }
  }

}
