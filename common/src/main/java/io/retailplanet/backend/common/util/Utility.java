package io.retailplanet.backend.common.util;

import org.jetbrains.annotations.Nullable;

/**
 * @author w.glanzer, 17.06.2019
 */
public class Utility
{

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

}
