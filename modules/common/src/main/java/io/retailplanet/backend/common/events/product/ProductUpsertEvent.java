package io.retailplanet.backend.common.events.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractAuthorizedEvent;
import org.jetbrains.annotations.*;

/**
 * A Product should be inserted / updated
 *
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public class ProductUpsertEvent extends AbstractAuthorizedEvent<ProductUpsertEvent>
{

  /**
   * ID of the client this product belongs to
   */
  @JsonProperty
  String clientID;

  /**
   * currently used session token
   */
  @JsonProperty
  String session_token;

  /**
   * Product upsertion content (base64, zipped)
   */
  @JsonProperty
  byte[] content;

  @NotNull
  public ProductUpsertEvent clientID(String pClientID)
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
  public ProductUpsertEvent session_token(String pSession_token)
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

  @NotNull
  public ProductUpsertEvent content(byte[] pContent)
  {
    content = pContent;
    return this;
  }

  /**
   * @return value of 'content' field
   */
  @Nullable
  public byte[] content()
  {
    return content;
  }
}
