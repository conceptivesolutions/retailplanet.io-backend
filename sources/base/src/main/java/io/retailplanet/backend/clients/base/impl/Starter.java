package io.retailplanet.backend.clients.base.impl;

import io.quarkus.runtime.StartupEvent;

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
    if (!thread.isRunning())
      thread.start();
  }

}
