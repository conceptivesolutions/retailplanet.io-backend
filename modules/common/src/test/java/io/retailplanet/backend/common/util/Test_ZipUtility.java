package io.retailplanet.backend.common.util;

import com.google.common.collect.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import javax.json.bind.JsonbBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
    Map<Object, Object> obj = ImmutableMap.builder()
        .put("key1", "val1")
        .put("key2", ImmutableMap.builder()
            .put("innerKey1", 123)
            .put("innerKey2", "chuck")
            .put("innerKey3", "norris")
            .build())
        .put("key3", ImmutableList.builder()
            .add("arr1")
            .add("arr2")
            .build())
        .build();
    final String inputString = JsonbBuilder.create().toJson(obj);

    byte[] compressed = ZipUtility.compressedBase64(inputString);
    Assertions.assertNotEquals(inputString.getBytes(StandardCharsets.UTF_8), compressed, "String was not compressed");

    String decompressed = ZipUtility.uncompressBase64(compressed);
    Assertions.assertEquals(inputString, decompressed);
  }

  @Test
  void testCompressLarge()
  {
    ImmutableMap.Builder<Object, Object> obj = ImmutableMap.builder();
    for (int i = 0; i < 100000; i++)
      obj.put("key" + i, RandomStringUtils.randomAlphabetic(50));
    final String inputString = obj.toString();

    byte[] compressed = ZipUtility.compressedBase64(inputString);
    Assertions.assertNotEquals(inputString.getBytes(StandardCharsets.UTF_8), compressed, "String was not compressed");

    String decompressed = ZipUtility.uncompressBase64(compressed);
    Assertions.assertEquals(inputString, decompressed);
  }

}
