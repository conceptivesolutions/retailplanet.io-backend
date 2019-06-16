package io.retailplanet.backend.common.api.events;

import io.reactivex.*;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author w.glanzer, 16.06.2019
 */
public final class EventChain
{

  private static final String CHAINID = "chainID";

  private EventChain()
  {
  }

  /**
   * @return Generates a new random chain id
   */
  @NotNull
  public static String createID()
  {
    return UUID.randomUUID().toString();
  }

  /**
   * Creates a new chained event.
   * A chained event is a event with a "chain ID", which indicates the current call chain.
   *
   * @param pChainID ID
   * @return a json object for the event
   */
  @NotNull
  public static JsonObject createEvent(@NotNull String pChainID)
  {
    return new JsonObject()
        .put(CHAINID, pChainID);
  }

  /**
   * Creates a new flowable which waits for a result with the given chainID
   *
   * @param pChainID ChainID to wait for
   * @return Flowable with filtered chainID and set timeout
   */
  @NotNull
  public static Single<JsonObject> waitForEvent(@NotNull Flowable<JsonObject> pFlowable, @NotNull String pChainID)
  {
    return pFlowable
        .filter(pJson -> EventChain.filter(pJson, pChainID))
        .timeout(30, TimeUnit.SECONDS)
        .firstOrError();
  }

  /**
   * Checks, if the CHAINID of a chained event is equal to given id
   *
   * @param pChainedEvent Event
   * @param pChainID      ID to check for
   * @return <tt>true</tt> if the event has the specific chain id
   */
  public static boolean filter(@NotNull JsonObject pChainedEvent, @NotNull String pChainID)
  {
    String chainID = pChainedEvent.getString(CHAINID);
    if(chainID == null)
      return false;
    return chainID.trim().equals(pChainID);
  }

}
