package io.retailplanet.backend.common.events.index;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Event: A document search returned a result
 *
 * @author w.glanzer, 13.07.2019
 */
@RegisterForReflection
public class DocumentSearchResultEvent extends AbstractEvent<DocumentSearchResultEvent>
{

  /**
   * Contains the current count of result elements
   */
  @JsonProperty
  int count;

  /**
   * Contains the result elements
   */
  @JsonProperty
  List<Object> hits;

  /**
   * Sets the "count" field
   *
   * @param pCount Count as integer
   * @return Builder
   */
  @NotNull
  public DocumentSearchResultEvent count(int pCount)
  {
    count = pCount;
    return this;
  }

  /**
   * @return the value of the "count" field
   */
  public int count()
  {
    return count;
  }

  /**
   * Sets the "hits" field
   *
   * @param pHits All Hits
   * @return Builder
   */
  @NotNull
  public DocumentSearchResultEvent hits(List<Object> pHits)
  {
    hits = pHits;
    return this;
  }

  /**
   * @return the value of the "hits" field
   */
  public List<Object> hits()
  {
    return hits;
  }
}
