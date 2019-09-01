package io.retailplanet.backend.clients.base.impl;

import com.mashape.unirest.http.Unirest;
import io.quarkus.runtime.StartupEvent;
import io.retailplanet.backend.clients.base.impl.util.SSLUtility;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Startup Class
 *
 * @author w.glanzer, 01.09.2019
 */
@ApplicationScoped
public class Starter
{

  @Inject
  private MainCrawlingThread thread;

  /**
   * Initializes this crawler and starts crawling
   */
  public void init(@SuppressWarnings("unused") @Observes StartupEvent pEvent)
  {
    // Ignore insecure SSL certs
    Unirest.setHttpClient(HttpClients.custom()
                              .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLUtility.createTrustAllContext()))
                              .build());

    if (!thread.isRunning())
      thread.start();
  }

}
