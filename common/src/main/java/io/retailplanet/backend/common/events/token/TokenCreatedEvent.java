package io.retailplanet.backend.common.events.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Event: Token was created
 *
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public class TokenCreatedEvent extends AbstractEvent<TokenCreatedEvent>
{

  /**
   * ID of the client, this token belongs to
   */
  @JsonProperty
  public String clientID;

  /**
   * Timestamp this token is valid until
   */
  @JsonProperty
  public Instant valid_until;

  /**
   * Created session token
   */
  @JsonProperty
  public String session_token;

  @NotNull
  public TokenCreatedEvent clientID(String pClientID)
  {
    clientID = pClientID;
    return this;
  }

  @NotNull
  public TokenCreatedEvent valid_until(Instant pValid_until)
  {
    valid_until = pValid_until;
    return this;
  }

  @NotNull
  public TokenCreatedEvent session_token(String pSession_token)
  {
    session_token = pSession_token;
    return this;
  }
}
