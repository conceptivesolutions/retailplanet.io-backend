package io.retailplanet.backend.clients.saturn;

import com.google.common.collect.*;
import com.mashape.unirest.http.JsonNode;
import io.retailplanet.backend.clients.base.api.CrawledProduct;
import io.retailplanet.backend.clients.base.impl.util.*;
import io.retailplanet.backend.clients.base.spi.IProductCrawler;
import io.retailplanet.backend.common.util.i18n.ListUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.*;
import org.json.JSONObject;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.*;

/**
 * Product-Crawler for Saturn
 *
 * @author w.glanzer, 12.01.2019
 */
@ApplicationScoped
public class SaturnProductCrawler implements IProductCrawler
{
  private static final Logger _LOGGER = LoggerFactory.getLogger(SaturnProductCrawler.class);
  private static final String _BASE_URL = "https://www.saturn.de";
  private static final SSLSocketFactory _SOCKET_FACTORY = SSLUtility.createTrustAllContext().getSocketFactory();

  private final ExecutorService _REQUESTS_EXECUTOR = Executors.newFixedThreadPool(32);
  private final Semaphore _CONCURRENT_REQUESTS = new Semaphore(32);
  private final List<String> failedCategories = new ArrayList<>();

  @Override
  public void read(@NotNull Consumer<CrawledProduct> pProductConsumer)
  {
    try
    {
      List<String> ignoredURLs = _getIgnoredCategoryURLs();

      List<Pair<String, String>> urlPairs = _getCategories()
          .filter(pPair -> !ignoredURLs.contains(pPair.getRight()))
          .collect(Collectors.toList());

      for (Pair<String, String> urlPair : urlPairs)
        _fetchProducts(urlPair, pProductConsumer);
    }
    finally
    {
      failedCategories.clear();
    }
  }

  /**
   * Starts to fetch products from a specific url
   *
   * @param pProductPair     Category / URL to fetch
   * @param pProductConsumer Consumer for the retreived products
   */
  private void _fetchProducts(@NotNull Pair<String, String> pProductPair, @NotNull Consumer<CrawledProduct> pProductConsumer)
  {
    String categoryName = pProductPair.getLeft();
    String url = pProductPair.getRight();
    AtomicInteger size = new AtomicInteger(0);

    try
    {
      List<CompletableFuture<?>> futures = new ArrayList<>();
      int pageCount = _getPageCount(url);
      for (int i = 0; i < pageCount; i++)
      {
        final int fi = i;
        try
        {
          _CONCURRENT_REQUESTS.acquire();
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException("Failed to acquire lock", e);
        }

        futures.add(CompletableFuture.runAsync(() -> {
          try
          {
            _getProductElementsPaged(url, fi, pageCount)
                .map(pProductEle -> _createProduct(categoryName, pProductEle))
                .filter(Objects::nonNull)
                .forEach(pProduct -> {
                  size.incrementAndGet();
                  pProductConsumer.accept(pProduct);
                });
          }
          finally
          {
            _CONCURRENT_REQUESTS.release();
          }
        }, _REQUESTS_EXECUTOR));
      }

      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

      // Log success
      _LOGGER.info("Fetch Products | " + url + " | " + size.get());
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to fetch products", e);
    }
  }

  /**
   * @return Path, URL
   */
  @NotNull
  private Stream<Pair<String, String>> _getCategories()
  {
    try
    {
      Elements categoryList = _fetchDocument(Jsoup.connect(_BASE_URL))
          .getElementsByClass("site-navigation2__panel site-navigation2__panel--level-1").get(0)
          .getElementsByClass("site-navigation2__child-item");

      String mainCategory = "";
      String subCategory = "";
      Set<Pair<String, String>> resultList = new TreeSet<>(Comparator.comparing(Pair::getRight));
      List<CompletableFuture<?>> asyncTasksToWaitOn = new ArrayList<>();

      for (Element category : categoryList)
      {
        String myCategoryName = category.attr("data-tracking-nav");
        Elements childLinks = category.getElementsByClass("site-navigation2__child-link");
        if (childLinks.isEmpty())
          continue;

        String myURL = _getURLFromElement(childLinks.get(0), "href");
        if (!myURL.endsWith(".html"))
          continue;

        switch (category.attr("data-nav-level"))
        {
          case "level 1":
            mainCategory = myCategoryName;
            subCategory = null;
            break;
          case "level 2":
            subCategory = myCategoryName;
            break;
          case "level 3":
            String myCategoryPath = mainCategory + CrawledProduct.CATEGORY_DELIMITER + subCategory + CrawledProduct.CATEGORY_DELIMITER + myCategoryName;
            asyncTasksToWaitOn.add(CompletableFuture.runAsync(() -> {
              try
              {
                Set<Pair<String, String>> subs = _getMoreProducts(myURL);
                if (subs.isEmpty())
                  resultList.add(Pair.of(myCategoryPath, myURL));
                resultList.addAll(subs);
              }
              catch (Exception e)
              {
                resultList.add(Pair.of(myCategoryPath, myURL));
              }
            }, _REQUESTS_EXECUTOR));
            break;
          default:
            _LOGGER.info("Skipping Category Element " + category);
            break;
        }
      }

      // Wait for all tasks
      CompletableFuture.allOf(asyncTasksToWaitOn.toArray(new CompletableFuture[0])).get();

      // Log success
      _LOGGER.info("Found " + resultList.size() + " categories");

      // Return only "real" categories
      return resultList.stream()
          .filter(pPair -> pPair.getRight().contains("/category/"))
          .sorted(Comparator.comparing(Pair::getKey));
    }
    catch (Exception e)
    {
      _LOGGER.error("", e);
      return Stream.empty();
    }
  }

  @NotNull
  private Set<Pair<String, String>> _getMoreProducts(@NotNull String pURLForMore) throws IOException
  {
    Document result = _fetchDocument(Jsoup.connect(pURLForMore));
    Element ancestor = null;
    Element current = null;
    Elements categoriesList = result.getElementsByClass("categories-tree-descendants");
    if (categoriesList.isEmpty())
    {
      categoriesList = result.getElementsByClass("categories-flat-descendants");
      if (!categoriesList.isEmpty())
      {
        ancestor = result.getElementsByClass("categories-flat-ancestors").get(0).getElementsByTag("a").get(0);
        current = result.getElementsByClass("categories-flat-current").get(0);
      }
    }
    else
    {
      ancestor = result.getElementsByClass("categories-tree-ancestors").get(0).getElementsByTag("a").get(0);
      current = result.getElementsByClass("categories-tree-current").get(0);
    }

    if (!categoriesList.isEmpty())
    {
      assert ancestor != null && current != null;
      String ancestorText = ancestor.ownText();
      String currentText = current.ownText();
      return categoriesList.get(0).getElementsByTag("a").stream()
          .map(pEle -> {
            String url = _getURLFromElement(pEle, "href");
            String category = ancestorText + CrawledProduct.CATEGORY_DELIMITER + currentText + CrawledProduct.CATEGORY_DELIMITER + pEle.ownText();
            return Pair.of(category, url);
          })
          .collect(Collectors.toSet());
    }
    return new HashSet<>();
  }

  /**
   * @return Current page count
   */
  private int _getPageCount(String pURL)
  {
    try
    {
      Element paginationEle = _fetchDocument(Jsoup.connect(pURL)).getElementsByClass("pagination-next").get(0).parent();
      String sizeString = paginationEle.getElementsByTag("li").last().previousElementSibling().attr("data-value");
      return Integer.parseInt(sizeString);
    }
    catch (Exception e)
    {
      return 1;
    }
  }

  @NotNull
  private Stream<Element> _getProductElementsPaged(@NotNull String pCategoryURL, int pPage, int pMaxPages)
  {
    Document doc = null;

    try
    {
      if (failedCategories.contains(pCategoryURL))
        return Stream.empty();

      _LOGGER.info("Fetching Products | " + pCategoryURL + " | Page: " + (pPage + 1) + "/" + pMaxPages);

      doc = _fetchDocument(Jsoup.connect(pCategoryURL)
                               .data("page", String.valueOf(pPage + 1))
                               .data("sort", "price"));

      if (!doc.getElementsByClass("products-grid").isEmpty())
        return doc.getElementsByClass("products-grid").get(0).getElementsByTag("script").stream()
            .filter(pEle -> pEle.data().startsWith("var product"));
      else if (!doc.getElementsByClass("products-list").isEmpty())
        return doc.getElementsByClass("products-list").get(0).getElementsByTag("script").stream()
            .filter(pEle -> pEle.data().startsWith("var product"));
    }
    catch (Exception ignored)
    {
    }

    // -> Failure
    if (!failedCategories.contains(pCategoryURL))
    {
      failedCategories.add(pCategoryURL);
      if (doc != null)
        _LOGGER.info("Failed to retrieve elements for category: " + doc.body());
      _LOGGER.error("Failed to retrieve elements for category: " + pCategoryURL + " | Page: " + (pPage + 1) + "/" + pMaxPages);
    }

    return Stream.empty();
  }

  @NotNull
  private Document _fetchDocument(@NotNull Connection pConnection) throws IOException
  {
    return RetryManager.retry(() -> pConnection
        .sslSocketFactory(_SOCKET_FACTORY)
        .get(), 50, 10000, null, _LOGGER);
  }

  @Nullable
  private CrawledProduct _createProduct(@NotNull String pCategoryName, @NotNull Element pElement)
  {
    try
    {
      String scriptData = pElement.data();
      scriptData = scriptData.replaceFirst("var product[0-9]* = ", "").trim();
      scriptData = scriptData.substring(0, scriptData.length() - 1);
      JSONObject script = new JsonNode(scriptData).getObject();

      String name = script.getString("name");
      String id = _getID(pElement.parent());
      String url = _getURLFromElement(pElement.parent().getElementsByAttribute("data-clickable-href").get(0), "data-clickable-href");
      float price = Float.parseFloat(script.getString("price"));
      Map<String, String> additionalInfos = _getAdditionalInfos(pElement.parent());
      Set<String> previews = _getPreviews(pElement.parent());

      return new CrawledProduct()
          .name(name)
          .id(id)
          .category(pCategoryName)
          .url(url)
          .price(price)
          .previews(previews)
          .additionalInfos(additionalInfos);
    }
    catch (Exception e)
    {
      _LOGGER.error("Failed to create product " + pElement.parent(), e);
      return null;
    }
  }

  /**
   * @return Returns the "real" id of a product (catEntryId)
   */
  @NotNull
  private String _getID(@NotNull Element pElement)
  {
    try
    {
      Elements marketssels = pElement.getElementsByClass("marketsel-open");
      String idElement = marketssels.get(0).attr("data-markets-list");
      return idElement.substring(idElement.indexOf("catEntryId=") + "catEntryId=".length());
    }
    catch (Exception e)
    {
      String url = pElement.getElementsByAttribute("data-layer-url").get(0).attr("data-layer-url");
      String mayBeID = url.substring(url.indexOf("catEntryId=") + "catEntryId=".length());
      if (mayBeID.contains("&"))
        mayBeID = mayBeID.substring(0, mayBeID.indexOf("&"));
      return mayBeID;
    }
  }

  /**
   * @return All preview picture urls
   */
  @NotNull
  private Set<String> _getPreviews(@NotNull Element pElement)
  {
    Elements photoElements = pElement.getElementsByClass("product-photo");
    if (photoElements.isEmpty())
      photoElements = pElement.getElementsByClass("photo");
    return photoElements.stream()
        .flatMap(pPhotoEle -> pPhotoEle.getElementsByTag("img").stream())
        .map(pImgEle -> _getURLFromElement(pImgEle, "data-original"))
        .filter(pS -> !pS.trim().isEmpty())
        .map(pURL -> pURL.replace("fee_240_148_png", "fee_786_587_png"))
        .collect(Collectors.toSet());
  }

  /**
   * @return All additional information
   */
  @NotNull
  private Map<String, String> _getAdditionalInfos(@NotNull Element pElement)
  {
    Elements detailsEle = pElement.getElementsByClass("product-details");
    if (detailsEle.isEmpty())
    {
      detailsEle = pElement.getElementsByClass("base-content");
      if (!detailsEle.isEmpty())
        detailsEle = detailsEle.get(0).getElementsByTag("dl");
    }

    return detailsEle.stream()
        .flatMap(pProductDetailsEle -> {
          Multimap<String, String> elements = ArrayListMultimap.create();
          Element lastCategoryEle = null;
          for (Element child : pProductDetailsEle.children())
          {
            switch (child.tagName())
            {
              case "dt":
                lastCategoryEle = child;
                break;

              case "dd":
                if (lastCategoryEle != null)
                {
                  String key = lastCategoryEle.ownText().trim();
                  if (key.endsWith(":"))
                    key = key.substring(0, key.length() - 1);
                  elements.put(key, child.ownText());
                }
                break;
            }
          }
          return elements.asMap().entrySet().stream();
        })
        .map(pEntry -> Maps.immutableEntry(pEntry.getKey(), String.join(",", pEntry.getValue())))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @NotNull
  private String _getURLFromElement(@NotNull Element pElement, @NotNull String pRefContainer)
  {
    String url = pElement.attr(pRefContainer);
    if (url.startsWith("//"))
      url = "https:" + url;
    if (url.startsWith("/"))
      url = _BASE_URL + url;
    if (url.contains("#"))
      url = url.substring(0, url.indexOf('#'));
    return url;
  }

  @NotNull
  private List<String> _getIgnoredCategoryURLs()
  {
    return ListUtil.of();
  }

}
