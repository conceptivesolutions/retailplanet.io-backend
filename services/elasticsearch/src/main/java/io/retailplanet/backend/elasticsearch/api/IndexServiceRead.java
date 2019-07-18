package io.retailplanet.backend.elasticsearch.api;

import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.elasticsearch.impl.IQueryBuilder;
import io.retailplanet.backend.elasticsearch.impl.events.*;
import io.retailplanet.backend.elasticsearch.impl.facades.IIndexFacade;
import io.retailplanet.backend.elasticsearch.impl.filters.IFilterFactory;
import io.retailplanet.backend.elasticsearch.impl.matches.IMatchFactory;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

/**
 * Service: Read operations on Index
 *
 * @author w.glanzer, 16.07.2019
 */
@ApplicationScoped
public class IndexServiceRead
{

  @Inject
  private IEventFacade eventFacade;

  @Inject
  private IIndexFacade indexFacade;

  @Inject
  private IFilterFactory filterFactory;

  @Inject
  private IMatchFactory matchFactory;

  /**
   * Executes a search on the elasticsearch backend
   *
   * @param pEvent search event
   */
  @Incoming(IEvents.IN_INDEX_DOCUMENT_SEARCH)
  public void documentSearch(@Nullable DocumentSearchEvent pEvent)
  {
    if (pEvent == null)
      return;

    try
    {
      DocumentSearchEvent.Query query = pEvent.query();
      if (query == null)
        throw new IllegalArgumentException("query must not be null");

      List<String> indexTypes = pEvent.indexTypes();
      List<IQueryBuilder> filters = filterFactory.interpretFilters(query.filters());
      List<IQueryBuilder> matches = matchFactory.interpretMatches(query.matches());
      List<JsonObject> result = indexFacade.search(indexTypes, matches, filters, pEvent.offset(), pEvent.length());

      eventFacade.sendDocumentSearchResultEvent(pEvent.createAnswer(DocumentSearchResultEvent.class)
                                                    .hits(Collections.unmodifiableList(result)));
    }
    catch (Exception e)
    {
      eventFacade.notifyError(pEvent, "Failed to execute search", e);
    }
  }

}
