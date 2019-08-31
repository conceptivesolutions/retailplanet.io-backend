package io.retailplanet.backend.clients.base.impl;

import com.google.common.base.Stopwatch;
import io.quarkus.runtime.StartupEvent;
import io.retailplanet.backend.clients.base.api.*;
import io.retailplanet.backend.clients.base.impl.upload.IBusinessRestFacade;
import io.retailplanet.backend.clients.base.spi.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Startup Class
 *
 * @author w.glanzer, 01.09.2019
 */
@ApplicationScoped
public class Starter
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(Starter.class);
  private static final int pageSize = 50;

  @ConfigProperty(name = "RP_HOST")
  protected String host;

  @ConfigProperty(name = "RP_CLIENTID")
  protected String clientID;

  @ConfigProperty(name = "RP_TOKEN")
  protected String clientToken;

  @Inject
  protected IMarketCrawler marketCrawler;

  @Inject
  protected IProductCrawler productCrawler;

  @Inject
  private IBusinessRestFacade.IUpload uploadFacade;

  private final ExecutorService uploadExecutor = Executors.newFixedThreadPool(8);
  private final Semaphore blockingSemaphore = new Semaphore(8);
  private final List<CompletableFuture<?>> uploadTasks = Collections.synchronizedList(new ArrayList<>());

  /**
   * Initializes this crawler and starts crawling
   */
  public void init(@SuppressWarnings("unused") @Observes StartupEvent pEvent)
  {
    _LOGGER.info("Starting to crawl with " + productCrawler.getClass().getSimpleName() + " and " + marketCrawler.getClass().getSimpleName());

    // timetracking
    Stopwatch watch = Stopwatch.createStarted();

    try
    {
      // Init upload
      uploadFacade.productsInit(host, clientID, clientToken);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to initiate upload", e);
    }

    // Upload all products
    _uploadProducts();

    // Upload all markets
    _uploadMarkets();

    try
    {
      // Wait for tasks to finish
      CompletableFuture.allOf(uploadTasks.toArray(new CompletableFuture[0])).get(10800, TimeUnit.SECONDS);

      // Execute flush on Backend
      uploadFacade.productsFlush();
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to upload tasks to backend", e);
    }
    finally
    {
      _LOGGER.info("Updating products took " + watch.stop().toString());
    }

    // Finish
    System.exit(0);
  }

  /**
   * Uploads all available products
   */
  private void _uploadProducts()
  {
    final AtomicReference<List<CrawledProduct>> currentProducts = new AtomicReference<>(Collections.synchronizedList(new ArrayList<>()));

    // Read all products from stream
    productCrawler.read(pProduct -> {
      synchronized (currentProducts)
      {
        List<CrawledProduct> currentProductList = currentProducts.get();
        currentProductList.add(pProduct);
        if (currentProductList.size() >= pageSize)
        {
          _asyncExtendAndUploadProducts(currentProductList);
          currentProducts.set(Collections.synchronizedList(new ArrayList<>()));
        }
      }
    });

    // Upload last page
    List<CrawledProduct> lastProductPage = currentProducts.get();
    if (!lastProductPage.isEmpty())
      _asyncExtendAndUploadProducts(lastProductPage);
  }

  /**
   * Uploads all available markets
   */
  private void _uploadMarkets()
  {
    _asyncUploadMarkets(marketCrawler.getMarkets());
  }

  /**
   * Executes the upload tasks for a list of products
   *
   * @param pProducts Products to upload
   */
  private void _asyncExtendAndUploadProducts(@NotNull List<CrawledProduct> pProducts)
  {
    try
    {
      blockingSemaphore.acquire();
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException("Failed to acquire lock");
    }

    uploadTasks.add(CompletableFuture.runAsync(() -> {
      try
      {
        // Extend with availability
        marketCrawler.includeAvailability(pProducts);

        // Upload
        uploadFacade.products(pProducts);
      }
      catch (Exception e)
      {
        _LOGGER.error("Failed to upload product page", e);

        // Fail really fast, we do not want to go through the whole queue..
        System.exit(300);
      }
      finally
      {
        blockingSemaphore.release();
      }
    }, uploadExecutor));
  }

  /**
   * Executes the upload tasks for a list of markets
   *
   * @param pMarkets Markets to upload
   */
  private void _asyncUploadMarkets(@NotNull List<CrawledMarket> pMarkets)
  {
    try
    {
      blockingSemaphore.acquire();
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException("Failed to acquire lock");
    }

    uploadTasks.add(CompletableFuture.runAsync(() -> {
      try
      {
        // Upload
        uploadFacade.markets(pMarkets);
      }
      catch (Exception e)
      {
        _LOGGER.error("Failed to upload market page", e);

        // Fail really fast, we do not want to go through the whole queue..
        System.exit(300);
      }
      finally
      {
        blockingSemaphore.release();
      }
    }, uploadExecutor));
  }

}
