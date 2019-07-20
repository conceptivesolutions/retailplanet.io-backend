package io.retailplanet.backend.common.util;

import org.jetbrains.annotations.*;

import java.io.*;
import java.util.Properties;

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
    String property = System.getenv("DEV");
    if (property == null)
      return true;
    return !Boolean.FALSE.toString().equalsIgnoreCase(property);
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
   * @return Returns the name / id of the current service
   */
  @NotNull
  public static String getServiceID()
  {
    try (InputStream propStream = ClassLoader.getSystemResourceAsStream("application.properties"))
    {
      Properties props = new Properties();
      assert propStream != null;
      props.load(new InputStreamReader(propStream));
      return props.getProperty("retailplanet.service.group.id");
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

}
