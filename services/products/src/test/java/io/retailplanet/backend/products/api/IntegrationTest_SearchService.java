package io.retailplanet.backend.products.api;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.retailplanet.backend.common.*;
import io.retailplanet.backend.common.events.*;
import io.retailplanet.backend.common.events.answer.IEventAnswerFacade;
import io.retailplanet.backend.common.events.index.*;
import io.retailplanet.backend.common.events.market.*;
import io.retailplanet.backend.common.events.search.*;
import io.retailplanet.backend.common.util.i18n.ListUtil;
import io.retailplanet.backend.products.impl.events.*;
import io.retailplanet.backend.products.impl.struct.*;
import io.vertx.core.json.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * IntegrationTest for SearchService
 *
 * @author w.glanzer, 28.07.2019
 * @see io.retailplanet.backend.products.api.SearchService
 */
@QuarkusTest
@Testcontainers
@QuarkusTestResource(KafkaTestResource.class)
class IntegrationTest_SearchService extends AbstractKafkaIntegrationTest
{

  @Inject
  private MockedEventFacade eventFacade;

  @Test
  void testSearchProducts()
  {
    Product product1 = _createProduct("product1", null, null, null, -1, null, null, null);
    Product product2 = _createProduct("product2", UUID.randomUUID().toString(), "category", "https://test.example/product", 19, Collections.singletonList("myPreview"), null, null);
    Product product3 = _createProduct("product3", UUID.randomUUID().toString(), "category/one", "https://test.example/product", -1, null, null, null);
    Product product4 = _createProduct("product4", UUID.randomUUID().toString(), null, "https://test.example/product", 42, Collections.singletonList("myPreview"), null, null);

    // Rebuild AnswerService
    IEventAnswerFacade.writeAccess(eventFacade)
        .reset()
        .answerWith(SearchMarketsEvent.class, _createSearchMarketResultEventForSearchProductsEvent())
        .answerWith(DocumentSearchEvent.class, _createDocumentSearchResultEventForSearchProductsEvent(product1, product2, product3, product4));

    // Send SearchProductsEvent ...
    SearchProductsResultEvent searchProductsResultEvent = send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent(), eventFacade.getSearchProductsResultEvent());

    // ... validate SearchProductsResultEvent
    Assertions.assertNotNull(searchProductsResultEvent);
    Assertions.assertNotNull(searchProductsResultEvent.elements);
    Assertions.assertTrue(searchProductsResultEvent.elements.stream().anyMatch(pObj -> _checkProductEquality(pObj, product1)), "product1 not found");
    Assertions.assertTrue(searchProductsResultEvent.elements.stream().anyMatch(pObj -> _checkProductEquality(pObj, product2)), "product2 not found");
    Assertions.assertTrue(searchProductsResultEvent.elements.stream().anyMatch(pObj -> _checkProductEquality(pObj, product3)), "product3 not found");
    Assertions.assertTrue(searchProductsResultEvent.elements.stream().anyMatch(pObj -> _checkProductEquality(pObj, product4)), "product4 not found");
    Assertions.assertEquals(4, searchProductsResultEvent.maxSize);
  }

  @Test
  void testSearchProducts_null()
  {
    Assertions.assertThrows(NoEventReceivedException.class, () -> send(IEvents.IN_SEARCH_PRODUCTS, null, eventFacade.getDocumentUpsertEvent()));
  }

  @Test
  void testSearchProducts_invalidDocumentSearchResultEvent_NullOrEmptyHits()
  {
    IEventAnswerFacade.writeAccess(eventFacade)
        .reset()
        .answerWith(SearchMarketsEvent.class, _createSearchMarketResultEventForSearchProductsEvent())
        .answerWith(DocumentSearchEvent.class, new DocumentSearchResultEvent()
            .hits(null)
            .count(15));

    // NULL hits should result in a "valid" ProductSearchResult, because we simply found nothing
    SearchProductsResultEvent result = send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent(), eventFacade.getSearchProductsResultEvent());
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.elements);
    Assertions.assertTrue(result.elements.isEmpty());
    Assertions.assertEquals(0, result.maxSize);

    IEventAnswerFacade.writeAccess(eventFacade)
        .reset()
        .answerWith(SearchMarketsEvent.class, _createSearchMarketResultEventForSearchProductsEvent())
        .answerWith(DocumentSearchEvent.class, new DocumentSearchResultEvent()
            .hits(ListUtil.of())
            .count(-1));

    // EMPTY hits should result in a "valid" ProductSearchResult, because we simply found nothing
    result = send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent(), eventFacade.getSearchProductsResultEvent());
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.elements);
    Assertions.assertTrue(result.elements.isEmpty());
    Assertions.assertEquals(0, result.maxSize);
  }

  @Test
  void testSearchProducts_invalidOffset()
  {
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent().offset(null), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent().offset(Integer.MIN_VALUE), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent().offset(Integer.MAX_VALUE), eventFacade.getDocumentUpsertEvent()));
  }

  @Test
  void testSearchProducts_invalidLength()
  {
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent().length(null), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent().length(0), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent().length(Integer.MIN_VALUE), eventFacade.getDocumentUpsertEvent()));
    Assertions.assertThrows(ErrorEventReceivedException.class, () -> send(IEvents.IN_SEARCH_PRODUCTS, _createSearchProductsEvent().length(Integer.MAX_VALUE), eventFacade.getDocumentUpsertEvent()));
  }

  @NotNull
  private DocumentSearchResultEvent _createDocumentSearchResultEventForSearchProductsEvent(@NotNull Product... pHits)
  {
    return new DocumentSearchResultEvent()
        .hits(Arrays.stream(pHits)
                  .map(pProduct -> pProduct.toIndexJSON("clientid").getMap())
                  .collect(Collectors.toList()))
        .count(pHits.length);
  }

  @NotNull
  private SearchMarketsResultEvent _createSearchMarketResultEventForSearchProductsEvent()
  {
    return new SearchMarketsResultEvent()
        .marketIDs(Collections.singletonList("myMarket"));
  }

  @NotNull
  private SearchProductsEvent _createSearchProductsEvent()
  {
    return new SearchProductsEvent()
        .offset(0)
        .length(20)
        .filter(new JsonObject()
                    .put("geo", new JsonArray()
                        .add(25)
                        .add(25.59)
                        .add(30)))
        .query("myQuery");
  }

  private static boolean _checkProductEquality(@NotNull Object pObject, @NotNull Product pOther)
  {
    if (!(pObject instanceof Product))
      return false;
    Product product = (Product) pObject;
    return Objects.equals(product.name, pOther.name) &&
        Objects.equals(product.id, pOther.id) &&
        Objects.equals(product.category, pOther.category) &&
        Objects.equals(product.url, pOther.url) &&
        Objects.equals(product.price, pOther.price) &&
        Objects.equals(product.previews == null ? Collections.emptyList() : product.previews, pOther.previews == null ? Collections.emptyList() : pOther.previews) &&
        Objects.equals(product.additionalInfos == null ? Collections.emptyMap() : product.additionalInfos, pOther.additionalInfos == null ? Collections.emptyMap() : pOther.additionalInfos) &&
        Objects.equals(product.availability == null ? Collections.emptyMap() : product.availability, pOther.availability == null ? Collections.emptyMap() : pOther.availability);
  }

  @NotNull
  private static Product _createProduct(String pName, String pId, String pCategory, String pUrl, float pPrice,
                                        List<String> pPreviews, Map<String, String> pAdditionalInfos, Map<String, ProductAvailability> pAvailability)
  {
    Product product = new Product();
    product.name = pName;
    product.id = pId;
    product.category = pCategory;
    product.url = pUrl;
    product.price = pPrice;
    product.previews = pPreviews;
    product.additionalInfos = pAdditionalInfos;
    product.availability = pAvailability;
    return product;
  }

}
