package io.retailplanet.backend.elasticsearch.api;

import io.retailplanet.backend.common.api.AbstractService;
import io.retailplanet.backend.common.events.ErrorEvent;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.elasticsearch.impl.*;
import io.retailplanet.backend.elasticsearch.impl.facades.IIndexFacade;
import io.retailplanet.backend.elasticsearch.impl.filters.IFilterFactory;
import io.retailplanet.backend.elasticsearch.impl.matches.IMatchFactory;
import io.smallrye.reactive.messaging.annotations.*;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

/**
 * Service: Read operations on Index
 *
 * @author w.glanzer, 16.07.2019
 */
@ApplicationScoped
public class IndexServiceRead extends AbstractService
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(IndexServiceRead.class);

  @Stream(IEvents.OUT_INDEX_DOCUMENT_SEARCHRESULT)
  Emitter<DocumentSearchResultEvent> searchResultEmitter;

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

      //List<String> indices = pEvent.indexTypes(); // todo use indextypes
      List<IQueryBuilder> filters = filterFactory.interpretFilters(query.filters());
      List<IQueryBuilder> matches = matchFactory.interpretMatches(query.matches());
      List<JsonObject> result = indexFacade.search(matches, filters, pEvent.offset(), pEvent.length());

      searchResultEmitter.send(pEvent.createAnswer(DocumentSearchResultEvent.class)
                                   .hits(Collections.unmodifiableList(result)));
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to execute search", e);
      errorsEmitter.send(new ErrorEvent().error(e));
    }
  }

}
