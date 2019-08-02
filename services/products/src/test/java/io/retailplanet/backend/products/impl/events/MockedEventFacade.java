package io.retailplanet.backend.products.impl.events;

import io.quarkus.test.Mock;
import io.reactivex.Single;
import io.retailplanet.backend.common.events.answer.IEventAnswerFacade;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.market.*;
import io.retailplanet.backend.common.events.search.SearchProductsResultEvent;
import io.retailplanet.backend.common.util.Value;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;

/**
 * EventFacade for UnitTests in Products service
 *
 * @author w.glanzer, 27.07.2019
 */
@Mock
@ApplicationScoped
public class MockedEventFacade extends EventFacade
{

  private final Value<DocumentUpsertEvent> documentUpsertEvent = Value.createMutable();
  private final Value<SearchProductsResultEvent> searchProductsResultEvent = Value.createMutable();

  @NotNull
  @Override
  public Single<SearchMarketsResultEvent> sendSearchMarketsEvent(@NotNull SearchMarketsEvent pEvent)
  {
    return IEventAnswerFacade.readAccess(this).answerEvent(pEvent, SearchMarketsResultEvent::new);
  }

  @Override
  public void sendDocumentUpsertEvent(@NotNull DocumentUpsertEvent pEvent)
  {
    documentUpsertEvent.setValue(pEvent);
  }

  @NotNull
  @Override
  public Single<DocumentSearchResultEvent> sendDocumentSearchEvent(@NotNull DocumentSearchEvent pEvent)
  {
    return IEventAnswerFacade.readAccess(this).answerEvent(pEvent, DocumentSearchResultEvent::new);
  }

  @Override
  public void sendSearchProductsResultEvent(@NotNull SearchProductsResultEvent pEvent)
  {
    searchProductsResultEvent.setValue(pEvent);
  }

  @NotNull
  public Value<DocumentUpsertEvent> getDocumentUpsertEvent()
  {
    return documentUpsertEvent;
  }

  @NotNull
  public Value<SearchProductsResultEvent> getSearchProductsResultEvent()
  {
    return searchProductsResultEvent;
  }

}
