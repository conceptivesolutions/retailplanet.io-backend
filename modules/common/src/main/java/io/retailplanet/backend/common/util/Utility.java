package io.retailplanet.backend.common.util;

import org.jetbrains.annotations.*;

import java.io.*;
import java.time.Instant;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author w.glanzer, 17.06.2019
 */
public class Utility
{

  /**
   * @return Returns <tt>true</tt> if the current running application runs in DEV mode
   */
  public static boolean isDevMode()
  {
    return getBuildTime() == null;
  }

  /**
   * @return returns the build time
   */
  @Nullable
  public static Instant getBuildTime()
  {
    try (InputStream propStream = ClassLoader.getSystemResourceAsStream("application.properties"))
    {
      Properties props = new Properties();
      assert propStream != null;
      props.load(new InputStreamReader(propStream));
      return Instant.ofEpochMilli(Long.parseLong(props.getProperty("build.timestamp")));
    }
    catch (Exception e)
    {
      return null;
    }
  }

  /**
   * Determines if the given string is null or an emptry trimmed string
   *
   * @param pString String to check
   * @return <tt>true</tt> if it is empty or null
   */
  public static boolean isNullOrEmptyTrimmedString(@Nullable String pString)
  {
    return pString == null || pString.trim().isEmpty();
  }

  /**
   * Transforms the error to a string, with the whole stacktrace
   *
   * @param pError Throwable
   * @return String representation with stacktrace
   */
  @NotNull
  public static String toString(@NotNull Throwable pError)
  {
    StringWriter sw = new StringWriter();
    pError.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  /**
   * Returns either pValue, if not null, or consumes the value of the given supplier
   *
   * @param pValue          Value
   * @param pDefaultValueFn Value-Creator
   * @return Value, not <tt>null</tt>
   */
  @NotNull
  public static <T> T notNull(@Nullable T pValue, @NotNull Supplier<T> pDefaultValueFn)
  {
    if (pValue != null)
      return pValue;
    T value = pDefaultValueFn.get();
    if (value == null)
      throw new NullPointerException("The defaultValueFn must not return null");
    return value;
  }

}
