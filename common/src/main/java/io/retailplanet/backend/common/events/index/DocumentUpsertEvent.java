package io.retailplanet.backend.common.events.index;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event: Indexed document should be inserted / updated
 *
 * @author w.glanzer, 11.07.2019
 */
@RegisterForReflection
public class DocumentUpsertEvent extends AbstractEvent<DocumentUpsertEvent>
{

  /**
   * ID of the client, where this event comes from
   */
  @JsonProperty
  String clientID;

  /**
   * type of the document
   */
  @JsonProperty
  String type;

  /**
   * document itself
   */
  @JsonProperty
  Object doc;

  @NotNull
  public DocumentUpsertEvent clientID(String pClientID)
  {
    clientID = pClientID;
    return this;
  }

  /**
   * @return value of 'clientID' field
   */
  public String clientID()
  {
    return clientID;
  }

  @NotNull
  public DocumentUpsertEvent type(String pType)
  {
    type = pType;
    return this;
  }

  /**
   * @return value of 'type' field
   */
  public String type()
  {
    return type;
  }

  @NotNull
  public DocumentUpsertEvent doc(Object pDoc)
  {
    doc = pDoc;
    return this;
  }

  /**
   * @return value of 'doc' field
   */
  public Object doc()
  {
    return doc;
  }

}
