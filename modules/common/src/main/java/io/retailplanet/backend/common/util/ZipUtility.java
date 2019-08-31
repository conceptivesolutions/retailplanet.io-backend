package io.retailplanet.backend.common.util;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

/**
 * @author w.glanzer, 21.06.2019
 */
public class ZipUtility
{

  /**
   * Compress a string to gzipped base64 string
   *
   * @param pCompress String that should be compressed
   * @return gzipped bytes
   */
  @NotNull
  public static byte[] compressedBase64(@NotNull String pCompress)
  {
    try
    {
      ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
      OutputStream stream = new GZIPOutputStream(rstBao);
      stream.write(pCompress.getBytes(StandardCharsets.UTF_8));
      IOUtils.closeQuietly(stream);
      return rstBao.toByteArray();
    }
    catch (IOException e)
    {
      return pCompress.getBytes(StandardCharsets.UTF_8);
    }
  }

  /**
   * Uncompress gzipped base64 string
   *
   * @param pCompressed GZipped bytes
   * @return Uncompressed string
   */
  @NotNull
  public static String uncompressBase64(@NotNull byte[] pCompressed)
  {
    try
    {
      InputStream stream = new GZIPInputStream(new ByteArrayInputStream(pCompressed));
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      IOUtils.copyLarge(stream, bos);
      IOUtils.closeQuietly(stream);
      return bos.toString(StandardCharsets.UTF_8.name());
    }
    catch (IOException e)
    {
      return new String(pCompressed, StandardCharsets.UTF_8);
    }
  }

}
