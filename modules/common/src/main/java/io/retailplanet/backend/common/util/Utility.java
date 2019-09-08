package io.retailplanet.backend.common.util;

import org.jetbrains.annotations.*;

import java.io.*;
import java.time.Instant;
import java.util.*;
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

  /**
   * Returns the value of pKey in pContainer as a string
   *
   * @param pContainer Container
   * @param pKey       Key
   * @return result as a string
   */
  @Nullable
  public static String getString(@NotNull Map<String, Object> pContainer, @NotNull String pKey)
  {
    Object value = pContainer.get(pKey);
    if (value != null)
      return String.valueOf(value);
    return null;
  }

  /**
   * Returns the value of pKey in pContainer as a string
   *
   * @param pContainer Container
   * @param pKey       Key
   * @param pDefault   Default value if no value is associated with pKey
   * @return result as a string
   */
  @NotNull
  public static String getString(@NotNull Map<String, Object> pContainer, @NotNull String pKey, @NotNull String pDefault)
  {
    String result = getString(pContainer, pKey);
    return result == null ? pDefault : result;
  }

  /**
   * Returns the value of pKey in pContainer as an integer
   *
   * @param pContainer Container
   * @param pKey       Key
   * @return result as  an integer
   */
  @Nullable
  public static Integer getInteger(@NotNull Map<String, Object> pContainer, @NotNull String pKey)
  {
    String value = getString(pContainer, pKey);
    if (value != null)
      return Integer.valueOf(value);
    return null;
  }

  /**
   * Returns the value of pKey in pContainer as an integer
   *
   * @param pContainer Container
   * @param pKey       Key
   * @param pDefault   Default value if no value is associated with pKey
   * @return result as an integer
   */
  @NotNull
  public static Integer getInteger(@NotNull Map<String, Object> pContainer, @NotNull String pKey, @NotNull Integer pDefault)
  {
    Integer result = getInteger(pContainer, pKey);
    return result == null ? pDefault : result;
  }

  /**
   * Returns the value of pKey in pContainer as a long
   *
   * @param pContainer Container
   * @param pKey       Key
   * @return result as a long
   */
  @Nullable
  public static Long getLong(@NotNull Map<String, Object> pContainer, @NotNull String pKey)
  {
    String value = getString(pContainer, pKey);
    if (value != null)
      return Long.valueOf(value);
    return null;
  }

  /**
   * Returns the value of pKey in pContainer as a long
   *
   * @param pContainer Container
   * @param pKey       Key
   * @param pDefault   Default value if no value is associated with pKey
   * @return result as a long
   */
  @NotNull
  public static Long getLong(@NotNull Map<String, Object> pContainer, @NotNull String pKey, @NotNull Long pDefault)
  {
    Long result = getLong(pContainer, pKey);
    return result == null ? pDefault : result;
  }

  /**
   * Returns the value of pKey in pContainer as a float
   *
   * @param pContainer Container
   * @param pKey       Key
   * @return result as a float
   */
  @Nullable
  public static Float getFloat(@NotNull Map<String, Object> pContainer, @NotNull String pKey)
  {
    String value = getString(pContainer, pKey);
    if (value != null)
      return Float.valueOf(value);
    return null;
  }

  /**
   * Returns the value of pKey in pContainer as a float
   *
   * @param pContainer Container
   * @param pKey       Key
   * @param pDefault   Default value if no value is associated with pKey
   * @return result as a float
   */
  @NotNull
  public static Float getFloat(@NotNull Map<String, Object> pContainer, @NotNull String pKey, @NotNull Float pDefault)
  {
    Float result = getFloat(pContainer, pKey);
    return result == null ? pDefault : result;
  }

  /**
   * Returns the value of pKey in pContainer as a list
   *
   * @param pContainer Container
   * @param pKey       Key
   * @return result as a list
   */
  @Nullable
  public static <T> List<T> getList(@NotNull Map<String, Object> pContainer, @NotNull String pKey)
  {
    return (List<T>) pContainer.get(pKey);
  }

  /**
   * Returns the value of pKey in pContainer as a list
   *
   * @param pContainer Container
   * @param pKey       Key
   * @param pDefault   Default value if no value is associated with pKey
   * @return result as a list
   */
  @NotNull
  public static <T> List<T> getList(@NotNull Map<String, Object> pContainer, @NotNull String pKey, @NotNull List<T> pDefault)
  {
    try
    {
      List<T> result = getList(pContainer, pKey);
      return result == null ? pDefault : result;
    }
    catch (ClassCastException cce)
    {
      return pDefault;
    }
  }

}
