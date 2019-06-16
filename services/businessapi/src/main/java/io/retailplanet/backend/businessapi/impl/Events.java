package io.retailplanet.backend.businessapi.impl;

import io.retailplanet.backend.common.api.events.EventChain;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * This class contains all event creation stuff
 *
 * @author w.glanzer, 16.06.2019
 */
public final class Events
{

  private Events()
  {
  }

  /**
   * Creates a new BusinessToken_CREATE-Event
   *
   * @param pChainID  ChainID
   * @param pClientID ID of the client which issued this event
   * @return a JsonObject representing this event
   */
  @NotNull
  public static JsonObject createBusinessTokenCreateEvent(@NotNull String pChainID, @NotNull String pClientID)
  {
    return EventChain.createEvent(pChainID)
        .put("clientID", pClientID);
  }

}
