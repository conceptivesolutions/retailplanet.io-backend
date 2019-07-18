package io.retailplanet.backend.products.impl.events;

import io.retailplanet.backend.common.events.market.*;
import org.jetbrains.annotations.*;

/**
 * Facade to send events
 *
 * @author w.glanzer, 17.07.2019
 */
public interface IEventFacade
{

  /**
   * Search Markets
   *
   * @param pEvent Event to search markets
   * @return result, or <tt>null</tt> if no event was received
   */
  @Nullable
  SearchMarketsResultEvent searchMarkets(@NotNull SearchMarketsEvent pEvent);

}
