package io.retailplanet.backend.common.util.i18n;

/**
 * @author w.glanzer, 11.01.2019
 */
public interface ISupplierEx<T, Ex extends Exception>
{
  T get() throws Ex;
}
