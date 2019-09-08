package io.retailplanet.backend.common.objects.index;

import org.jetbrains.annotations.*;

import java.util.List;

/**
 * A document search returned a result
 *
 * @author w.glanzer, 13.07.2019
 */
public class SearchResult
{

  /**
   * Contains the current count of result elements
   */
  public long count;

  /**
   * Contains the result elements
   */
  public List<Object> hits;

  /**
   * Sets the "count" field
   *
   * @param pCount Count as integer
   * @return Builder
   */
  @NotNull
  public SearchResult count(long pCount)
  {
    count = pCount;
    return this;
  }

  /**
   * @return the value of the "count" field
   */
  public long count()
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
  public SearchResult hits(List<Object> pHits)
  {
    hits = pHits;
    return this;
  }

  /**
   * @return the value of the "hits" field
   */
  @Nullable
  public List<Object> hits()
  {
    return hits;
  }
}
