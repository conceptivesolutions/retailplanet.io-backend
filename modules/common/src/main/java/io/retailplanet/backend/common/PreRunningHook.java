package io.retailplanet.backend.common;

import io.quarkus.runtime.StartupEvent;
import io.retailplanet.backend.common.util.Utility;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 * Hook to define some default initialisation tasks on quarkus startup / shutdown
 *
 * @author w.glanzer, 20.07.2019
 */
@ApplicationScoped
public class PreRunningHook
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(PreRunningHook.class);

  @SuppressWarnings("unused")
  void onStart(@Observes StartupEvent ev)
  {
    boolean devMode = Utility.isDevMode();

    if (devMode)
      // log current state
      _LOGGER.info("Starting sevice in development mode");
    else
    {
      _LOGGER.info("Starting service in production mode");
    }
  }

}
