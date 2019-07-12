package io.retailplanet.backend.common.events.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractAuthorizedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A Market should be inserted / updated
 *
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public class MarketUpsertEvent extends AbstractAuthorizedEvent<MarketUpsertEvent>
{

  /**
   * ID of the client this market belongs to
   */
  @JsonProperty
  public String clientID;

  /**
   * currently used session token
   */
  @JsonProperty
  public String session_token;

  /**
   * Market upsertion content (base64, zipped)
   */
  @JsonProperty
  public byte[] content;

  @NotNull
  public MarketUpsertEvent clientID(String pClientID)
  {
    clientID = pClientID;
    return this;
  }

  @NotNull
  public MarketUpsertEvent session_token(String pSession_token)
  {
    session_token = pSession_token;
    return this;
  }

  @NotNull
  public MarketUpsertEvent content(byte[] pContent)
  {
    content = pContent;
    return this;
  }
}
