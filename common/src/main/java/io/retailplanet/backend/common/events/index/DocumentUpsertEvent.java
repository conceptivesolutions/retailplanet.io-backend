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
  public String clientID;

  /**
   * type of the document
   */
  @JsonProperty
  public String type;

  /**
   * document itself
   */
  @JsonProperty
  public Object doc;

  @NotNull
  public DocumentUpsertEvent clientID(String pClientID)
  {
    clientID = pClientID;
    return this;
  }

  @NotNull
  public DocumentUpsertEvent type(String pType)
  {
    type = pType;
    return this;
  }

  @NotNull
  public DocumentUpsertEvent doc(Object pDoc)
  {
    doc = pDoc;
    return this;
  }
}
