package io.retailplanet.backend.businesstoken.impl;

import io.quarkus.runtime.StartupEvent;
import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;

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
    // Clear invalid tokens
    tokenCache.invalidateAllExpiredTokens();
  }

}
