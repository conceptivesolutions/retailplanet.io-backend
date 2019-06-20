package io.retailplanet.backend.businesstoken.impl;

import io.quarkus.runtime.*;
import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;
import io.retailplanet.backend.common.util.H2Starter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Hook for Quarkus lifecycles
 *
 * @author w.glanzer, 17.06.2019
 */
@ApplicationScoped
class LifecycleHook
{

  @Inject
  TokenCache tokenCache;

  @SuppressWarnings("unused")
  void onStart(@Observes StartupEvent pEvent)
  {
    // Start the H2 instance
    H2Starter.getInstance().start();

    // Clear invalid tokens
    tokenCache.invalidateAllExpiredTokens();
  }

  @SuppressWarnings("unused")
  void onEnd(@Observes ShutdownEvent pEvent)
  {
    // Stop the H2 instance
    H2Starter.getInstance().stop();
  }

}
