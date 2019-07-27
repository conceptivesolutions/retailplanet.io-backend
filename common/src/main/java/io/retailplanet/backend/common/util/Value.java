package io.retailplanet.backend.common.util;

import org.jetbrains.annotations.Nullable;

/**
 * Value container with information about, if NULL was set as value
 *
 * @author w.glanzer, 27.07.2019
 */
public class Value<T>
{

  private T value;
  private boolean valueSet;

  public static <T> Value<T> createMutable()
  {
    return new Value<>();
  }

  private Value()
  {
    reset();
  }

  /**
   * @return the current value
   */
  @Nullable
  public synchronized T getValue()
  {
    return value;
  }

  /**
   * @return the current value, and reset to default values
   */
  @Nullable
  public synchronized T getValueAndReset()
  {
    T value = getValue();
    reset();
    return value;
  }

  /**
   * Sets the current value
   *
   * @param pValue value
   */
  public synchronized void setValue(@Nullable T pValue)
  {
    value = pValue;
    valueSet = true;
  }

  /**
   * Determines, if the value was set (maybe to null)
   *
   * @return <tt>true</tt> if it was set
   */
  public synchronized boolean isValueSet()
  {
    return valueSet;
  }

  /**
   * Resets the whole object and marks it as "not modified"
   */
  public synchronized void reset()
  {
    setValue(null);
    valueSet = false;
  }

}
