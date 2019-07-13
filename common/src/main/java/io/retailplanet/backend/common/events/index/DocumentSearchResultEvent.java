package io.retailplanet.backend.common.events.index;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;

/**
 * Event: A document search returned a result
 *
 * @author w.glanzer, 13.07.2019
 */
@RegisterForReflection
public class DocumentSearchResultEvent extends AbstractEvent<DocumentSearchResultEvent>
{

  @JsonProperty
  public String result; //todo

}
