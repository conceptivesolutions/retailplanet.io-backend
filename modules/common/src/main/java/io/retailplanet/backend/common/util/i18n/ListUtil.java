package io.retailplanet.backend.common.util.i18n;

import java.util.*;

/**
 * @author w.glanzer, 04.08.2019
 */
@SuppressWarnings("unused")
public class ListUtil
{

  @SafeVarargs
  public static <E> List<E> of(E... elements)
  {
    if (elements == null)
      throw new NullPointerException();

    if (elements.length == 0)
      return Collections.emptyList();
    else if (elements.length == 1)
      return Collections.singletonList(elements[0]);
    return Arrays.asList(elements);
  }

}
