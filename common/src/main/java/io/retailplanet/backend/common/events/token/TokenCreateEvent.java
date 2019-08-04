package io.retailplanet.backend.common.events.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractAuthorizedEvent;
import org.jetbrains.annotations.*;

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
  String clientID;

  /**
   * API token of the client
   */
  @JsonProperty
  String token;

  @NotNull
  public TokenCreateEvent clientID(String pClientID)
  {
    clientID = pClientID;
    return this;
  }

  /**
   * @return value of 'clientID' field
   */
  @Nullable
  public String clientID()
  {
    return clientID;
  }

  @NotNull
  public TokenCreateEvent token(String pToken)
  {
    token = pToken;
    return this;
  }

  /**
   * @return value of 'token' field
   */
  @Nullable
  public String token()
  {
    return token;
  }
}
