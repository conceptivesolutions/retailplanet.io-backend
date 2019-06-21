package io.retailplanet.backend.common.util;

import io.vertx.core.json.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

/**
 * @author w.glanzer, 21.06.2019
 */
class Test_ZipUtility
{

  @Test
  void testCompressAndUncompressSimple()
  {
    final String inputString = "I_AM_A_SIMPLE_STRING_WITH_äöü_ß_<a>_SIGNS";

    byte[] compressed = ZipUtility.compressedBase64(inputString);
    Assertions.assertNotEquals(inputString.getBytes(StandardCharsets.UTF_8), compressed, "String was not compressed");

    String decompressed = ZipUtility.uncompressBase64(compressed);
    Assertions.assertEquals(inputString, decompressed);
  }

  @Test
  void testCompressWithJsonObject()
  {
    final String inputString = new JsonObject()
        .put("key1", "val1")
        .put("key2", new JsonObject()
            .put("innerKey1", 123)
            .put("innerKey2", "chuck")
            .put("innerKey3", "norris"))
        .put("key3", new JsonArray()
            .add("arr1")
            .add("arr2")).toString();

    byte[] compressed = ZipUtility.compressedBase64(inputString);
    Assertions.assertNotEquals(inputString.getBytes(StandardCharsets.UTF_8), compressed, "String was not compressed");

    String decompressed = ZipUtility.uncompressBase64(compressed);
    Assertions.assertEquals(inputString, decompressed);
  }

  @Test
  void testCompressLarge()
  {
    JsonObject obj = new JsonObject();
    for (int i = 0; i < 100000; i++)
      obj.put("key" + i, RandomStringUtils.randomAlphabetic(50));
    final String inputString = obj.toString();

    byte[] compressed = ZipUtility.compressedBase64(inputString);
    Assertions.assertNotEquals(inputString.getBytes(StandardCharsets.UTF_8), compressed, "String was not compressed");

    String decompressed = ZipUtility.uncompressBase64(compressed);
    Assertions.assertEquals(inputString, decompressed);
  }

}
