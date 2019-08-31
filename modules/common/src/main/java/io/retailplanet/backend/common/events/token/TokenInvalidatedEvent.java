package io.retailplanet.backend.common.events.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.*;

/**
 * Event: Token should be invalidated
 *
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public class TokenInvalidatedEvent extends AbstractEvent<TokenInvalidatedEvent>
{

  /**
   * If this clientID is set, all tokens will be invalidated that belong to the given client
   */
  @JsonProperty
  String clientID;

  /**
   * Specific token, that should be invalidated
   */
  @JsonProperty
  String session_token;

  @NotNull
  public TokenInvalidatedEvent clientID(String pClientID)
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
  public TokenInvalidatedEvent session_token(String pSession_token)
  {
    session_token = pSession_token;
    return this;
  }

  /**
   * @return value of 'session_token' field
   */
  @Nullable
  public String session_token()
  {
    return session_token;
  }
}
