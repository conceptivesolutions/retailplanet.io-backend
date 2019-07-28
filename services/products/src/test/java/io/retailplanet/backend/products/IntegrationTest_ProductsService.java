package io.retailplanet.backend.products;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.retailplanet.backend.common.events.*;
import io.retailplanet.backend.common.events.index.DocumentUpsertEvent;
import io.retailplanet.backend.common.events.product.ProductUpsertEvent;
import io.retailplanet.backend.common.util.*;
import io.retailplanet.backend.products.impl.events.*;
import io.retailplanet.backend.products.impl.struct.IIndexStructure;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.util.UUID;

/**
 * IntegrationTest for ProductsService
 *
 * @author w.glanzer, 27.07.2019
 * @see io.retailplanet.backend.products.api.ProductsService
 */
@QuarkusTest
public class IntegrationTest_ProductsService extends AbstractKafkaIntegrationTest
{
  @RegisterExtension
  @SuppressWarnings("WeakerAccess")
  public static final SharedKafkaTestResource sharedKafkaTestResource = new ServiceKafkaTestResource();
  private static final String _UPSERT_CONTENT = FileUtility.toString(IntegrationTest_ProductsService.class.getResource("upsertProducts_product.json"));

  @Inject
  private MockedEventFacade eventFacade;

  @Test
  void testProductUpsertEvent()
  {
    ProductUpsertEvent event = _createUpsertEvent();
    DocumentUpsertEvent result = send(IEvents.IN_PRODUCTS_UPSERT, event, eventFacade.getDocumentUpsertEvent());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(result.clientID(), event.clientID);
    Assertions.assertEquals(result.type(), IIndexStructure.INDEX_TYPE);
    Assertions.assertNotNull(result.doc());
  }

  @Test
  void testProductUpsertEvent_null()
  {
    Assertions.assertThrows(NoEventReceivedException.class, () -> send(IEvents.IN_PRODUCTS_UPSERT, null, eventFacade.getDocumentUpsertEvent()));
  }

  @Test
  void testProductUpsertEvent_invalidContent()
  {
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_PRODUCTS_UPSERT, _createUpsertEvent().content(null), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_PRODUCTS_UPSERT, _createUpsertEvent().content(new byte[0]), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_PRODUCTS_UPSERT, _createUpsertEvent().content("iAMinvalidBASE64".getBytes()), eventFacade.getDocumentUpsertEvent()));
  }

  @Test
  void testProductUpsertEvent_invalidSessionToken()
  {
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_PRODUCTS_UPSERT, _createUpsertEvent().session_token(null), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_PRODUCTS_UPSERT, _createUpsertEvent().session_token(" "), eventFacade.getDocumentUpsertEvent()));
  }

  @Test
  void testProductUpsertEvent_invalidClientID()
  {
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_PRODUCTS_UPSERT, _createUpsertEvent().clientID(null), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_PRODUCTS_UPSERT, _createUpsertEvent().clientID(" "), eventFacade.getDocumentUpsertEvent()));
  }

  @NotNull
  @Override
  protected SharedKafkaTestResource getResource()
  {
    return sharedKafkaTestResource;
  }

  /**
   * @return a correct and valid product upsert event
   */
  @NotNull
  private ProductUpsertEvent _createUpsertEvent()
  {
    return new ProductUpsertEvent()
        .clientID("myclientID")
        .content(ZipUtility.compressedBase64(_UPSERT_CONTENT))
        .session_token(UUID.randomUUID().toString());
  }

}
