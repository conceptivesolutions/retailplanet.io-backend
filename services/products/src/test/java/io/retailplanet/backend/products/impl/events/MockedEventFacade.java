package io.retailplanet.backend.products.impl.events;

import io.quarkus.test.Mock;
import io.retailplanet.backend.common.events.index.DocumentUpsertEvent;
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

  @Override
  public void sendDocumentUpsertEvent(@NotNull DocumentUpsertEvent pEvent)
  {
    documentUpsertEvent.setValue(pEvent);
  }

  @NotNull
  public Value<DocumentUpsertEvent> getDocumentUpsertEvent()
  {
    return documentUpsertEvent;
  }


}
