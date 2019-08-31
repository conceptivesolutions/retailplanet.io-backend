package io.retailplanet.backend.common.events.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.*;

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
  String clientID;

  /**
   * Timestamp this token is valid until
   */
  @JsonProperty
  Instant valid_until;

  /**
   * Created session token
   */
  @JsonProperty
  String session_token;

  @NotNull
  public TokenCreatedEvent clientID(String pClientID)
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
  public TokenCreatedEvent valid_until(Instant pValid_until)
  {
    valid_until = pValid_until;
    return this;
  }

  /**
   * @return value of 'valid_until' field
   */
  @Nullable
  public Instant valid_until()
  {
    return valid_until;
  }

  @NotNull
  public TokenCreatedEvent session_token(String pSession_token)
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
