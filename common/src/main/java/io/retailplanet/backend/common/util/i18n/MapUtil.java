package io.retailplanet.backend.common.util.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author w.glanzer, 04.08.2019
 */
@SuppressWarnings("unused")
public class MapUtil
{

  @NotNull
  public static <K, V> Map<K, V> of()
  {
    return Collections.emptyMap();
  }

  @NotNull
  public static <K, V> Map<K, V> of(@NotNull K pKey1, @NotNull V pValue1)
  {
    Map<K, V> map = new HashMap<>();
    map.put(Objects.requireNonNull(pKey1), Objects.requireNonNull(pValue1));
    return map;
  }

  @NotNull
  public static <K, V> Map<K, V> of(@NotNull K pKey1, @NotNull V pValue1, @NotNull K pKey2, @NotNull V pValue2)
  {
    Map<K, V> map = new HashMap<>();
    map.put(Objects.requireNonNull(pKey1), Objects.requireNonNull(pValue1));
    map.put(Objects.requireNonNull(pKey2), Objects.requireNonNull(pValue2));
    return map;
  }

  @NotNull
  public static <K, V> Map<K, V> of(@NotNull K pKey1, @NotNull V pValue1, @NotNull K pKey2, @NotNull V pValue2, @NotNull K pKey3, @NotNull V pValue3)
  {
    Map<K, V> map = new HashMap<>();
    map.put(Objects.requireNonNull(pKey1), Objects.requireNonNull(pValue1));
    map.put(Objects.requireNonNull(pKey2), Objects.requireNonNull(pValue2));
    map.put(Objects.requireNonNull(pKey3), Objects.requireNonNull(pValue3));
    return map;
  }

  @NotNull
  public static <K, V> Map<K, V> of(@NotNull K pKey1, @NotNull V pValue1, @NotNull K pKey2, @NotNull V pValue2, @NotNull K pKey3, @NotNull V pValue3, @NotNull K pKey4, @NotNull V pValue4)
  {
    Map<K, V> map = new HashMap<>();
    map.put(Objects.requireNonNull(pKey1), Objects.requireNonNull(pValue1));
    map.put(Objects.requireNonNull(pKey2), Objects.requireNonNull(pValue2));
    map.put(Objects.requireNonNull(pKey3), Objects.requireNonNull(pValue3));
    map.put(Objects.requireNonNull(pKey4), Objects.requireNonNull(pValue4));
    return map;
  }

  @NotNull
  public static <K, V> Map<K, V> of(@NotNull K pKey1, @NotNull V pValue1, @NotNull K pKey2, @NotNull V pValue2, @NotNull K pKey3, @NotNull V pValue3, @NotNull K pKey4, @NotNull V pValue4,
                                    @NotNull K pKey5, @NotNull V pValue5)
  {
    Map<K, V> map = new HashMap<>();
    map.put(Objects.requireNonNull(pKey1), Objects.requireNonNull(pValue1));
    map.put(Objects.requireNonNull(pKey2), Objects.requireNonNull(pValue2));
    map.put(Objects.requireNonNull(pKey3), Objects.requireNonNull(pValue3));
    map.put(Objects.requireNonNull(pKey4), Objects.requireNonNull(pValue4));
    map.put(Objects.requireNonNull(pKey5), Objects.requireNonNull(pValue5));
    return map;
  }

  @NotNull
  public static <K, V> Map<K, V> of(@NotNull K pKey1, @NotNull V pValue1, @NotNull K pKey2, @NotNull V pValue2, @NotNull K pKey3, @NotNull V pValue3, @NotNull K pKey4, @NotNull V pValue4,
                                    @NotNull K pKey5, @NotNull V pValue5, @NotNull K pKey6, @NotNull V pValue6)
  {
    Map<K, V> map = new HashMap<>();
    map.put(Objects.requireNonNull(pKey1), Objects.requireNonNull(pValue1));
    map.put(Objects.requireNonNull(pKey2), Objects.requireNonNull(pValue2));
    map.put(Objects.requireNonNull(pKey3), Objects.requireNonNull(pValue3));
    map.put(Objects.requireNonNull(pKey4), Objects.requireNonNull(pValue4));
    map.put(Objects.requireNonNull(pKey5), Objects.requireNonNull(pValue5));
    map.put(Objects.requireNonNull(pKey6), Objects.requireNonNull(pValue6));
    return map;
  }

  @NotNull
  public static <K, V> Map<K, V> of(@NotNull K pKey1, @NotNull V pValue1, @NotNull K pKey2, @NotNull V pValue2, @NotNull K pKey3, @NotNull V pValue3, @NotNull K pKey4, @NotNull V pValue4,
                                    @NotNull K pKey5, @NotNull V pValue5, @NotNull K pKey6, @NotNull V pValue6, @NotNull K pKey7, @NotNull V pValue7)
  {
    Map<K, V> map = new HashMap<>();
    map.put(Objects.requireNonNull(pKey1), Objects.requireNonNull(pValue1));
    map.put(Objects.requireNonNull(pKey2), Objects.requireNonNull(pValue2));
    map.put(Objects.requireNonNull(pKey3), Objects.requireNonNull(pValue3));
    map.put(Objects.requireNonNull(pKey4), Objects.requireNonNull(pValue4));
    map.put(Objects.requireNonNull(pKey5), Objects.requireNonNull(pValue5));
    map.put(Objects.requireNonNull(pKey6), Objects.requireNonNull(pValue6));
    map.put(Objects.requireNonNull(pKey7), Objects.requireNonNull(pValue7));
    return map;
  }

}
