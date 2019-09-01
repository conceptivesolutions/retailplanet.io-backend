package io.retailplanet.backend.common.metrics;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jetbrains.annotations.NotNull;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 01.09.2019
 */
@ApplicationScoped
class MetricEventFacadeImpl implements IMetricEventFacade
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(MetricEventFacadeImpl.class);

  @Override
  @Counted(name = "answerReceive_failure")
  public void fireAnswerReceiveError(@NotNull Throwable pException)
  {
    // Log
    _LOGGER.error("Failed to receive an event", pException);
  }

}
