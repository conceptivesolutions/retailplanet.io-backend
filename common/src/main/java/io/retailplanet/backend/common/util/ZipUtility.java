package io.retailplanet.backend.common.util;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Base64;
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
   * @return gzipped base64 string
   */
  @NotNull
  public static String compressedBase64(@NotNull String pCompress)
  {
    try
    {
      ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
      OutputStream stream = new GZIPOutputStream(rstBao);
      stream.write(pCompress.getBytes());
      IOUtils.closeQuietly(stream);
      byte[] bytes = rstBao.toByteArray();
      return Base64.getEncoder().encodeToString(bytes);
    }
    catch (IOException e)
    {
      return pCompress;
    }
  }

  /**
   * Uncompress gzipped base64 string
   *
   * @param pBase64Compressed GZipped Base64 String
   * @return Uncompressed string
   */
  @NotNull
  public static String uncompressBase64(@NotNull String pBase64Compressed)
  {
    try
    {
      byte[] bytes = Base64.getDecoder().decode(pBase64Compressed);
      ByteArrayInputStream rstBai = new ByteArrayInputStream(bytes);
      InputStream stream = new GZIPInputStream(rstBai);
      StringWriter writer = new StringWriter();
      IOUtils.copy(stream, writer);
      return writer.toString();
    }
    catch (IOException e)
    {
      return pBase64Compressed;
    }
  }

}
