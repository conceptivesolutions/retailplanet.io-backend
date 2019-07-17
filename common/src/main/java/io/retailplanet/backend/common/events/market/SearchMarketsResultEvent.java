package io.retailplanet.backend.common.events.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Event fired, when a market search event was completed sucessfully
 *
 * @author w.glanzer, 14.07.2019
 */
@RegisterForReflection
public class SearchMarketsResultEvent extends AbstractEvent<SearchMarketsResultEvent>
{

  @JsonProperty
  List<String> marketIDs;

  @NotNull
  public SearchMarketsResultEvent marketIDs(List<String> pMarketList)
  {
    marketIDs = pMarketList;
    return this;
  }

  /**
   * @return value of 'marketIDs' field
   */
  @NotNull
  public List<String> marketIDs()
  {
    return marketIDs == null ? Collections.emptyList() : marketIDs;
  }

}
