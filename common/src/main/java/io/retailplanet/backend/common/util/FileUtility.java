package io.retailplanet.backend.common.util;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;

/**
 * Utility for files
 *
 * @author w.glanzer, 27.07.2019
 */
public class FileUtility
{

  /**
   * Converts the given File to String, with an uncheckd exception
   *
   * @param pFile file
   * @return string, not <tt>null</tt>
   */
  @NotNull
  public static String toString(@NotNull URL pFile)
  {
    try
    {
      return FileUtils.readFileToString(new File(pFile.toURI()));
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
