package io.retailplanet.backend.common.events.index;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Event: Document should be searched in index
 *
 * @author w.glanzer, 12.07.2019
 */
@RegisterForReflection
public class DocumentSearchEvent extends AbstractEvent<DocumentSearchEvent>
{

  @JsonProperty(required = true)
  public Query query;

  @JsonProperty(defaultValue = "0")
  public Integer offset;

  @JsonProperty(defaultValue = "20")
  public Integer length;

  @NotNull
  public DocumentSearchEvent query(Query pQuery)
  {
    query = pQuery;
    return this;
  }

  @NotNull
  public DocumentSearchEvent offset(Integer pOffset)
  {
    offset = pOffset;
    return this;
  }

  @NotNull
  public DocumentSearchEvent length(Integer pLength)
  {
    length = pLength;
    return this;
  }

  @RegisterForReflection
  public static class Query
  {
    @JsonProperty
    public Map<String, String> matches;

    @NotNull
    public Query matches(@NotNull String pKey, @NotNull String pValue)
    {
      if (matches == null)
        matches = new HashMap<>();
      matches.put(pKey, pValue);
      return this;
    }

    @NotNull
    public Query matches(Map<String, String> pMatches)
    {
      matches = pMatches;
      return this;
    }
  }

}
