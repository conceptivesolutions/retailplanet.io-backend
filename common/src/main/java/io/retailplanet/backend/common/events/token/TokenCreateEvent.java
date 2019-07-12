package io.retailplanet.backend.common.events.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractAuthorizedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event: Token should be created
 *
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public class TokenCreateEvent extends AbstractAuthorizedEvent<TokenCreateEvent>
{

  /**
   * ID of the client who issued this token
   */
  @JsonProperty
  public String clientID;

  /**
   * API token of the client
   */
  @JsonProperty
  public String token;

  @NotNull
  public TokenCreateEvent clientID(String pClientID)
  {
    clientID = pClientID;
    return this;
  }

  @NotNull
  public TokenCreateEvent token(String pToken)
  {
    token = pToken;
    return this;
  }

}
