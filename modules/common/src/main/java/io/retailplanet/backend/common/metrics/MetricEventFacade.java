package io.retailplanet.backend.common.metrics;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jetbrains.annotations.NotNull;
import org.slf4j.*;

import javax.enterprise.context.*;
import javax.enterprise.event.Observes;

/**
 * @author w.glanzer, 01.09.2019
 */
@ApplicationScoped
public class MetricEventFacade implements IMetricEventFacade
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(MetricEventFacade.class);
  private static IMetricEventFacade INSTANCE;

  @NotNull
  public static IMetricEventFacade getInstance()
  {
    if(INSTANCE == null)
      throw new NullPointerException("INSTANCE must be not null");
    return INSTANCE;
  }

  @Override
  @Counted(name = "answerReceive_failure")
  public void fireAnswerReceiveError(@NotNull Throwable pException)
  {
    // Log
    _LOGGER.error("Failed to receive an event", pException);
  }

  /**
   * Initializer for the current MetricEventFaceImpl, to get CDI into here
   */
  public void init (@Observes @Initialized(ApplicationScoped.class) Object x, IMetricEventFacade bean)
  {
    INSTANCE = bean;
  }

}
