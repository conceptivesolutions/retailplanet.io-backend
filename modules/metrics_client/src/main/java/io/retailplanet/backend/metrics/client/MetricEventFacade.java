package io.retailplanet.backend.metrics.client;

import io.reactivex.*;
import io.retailplanet.backend.common.events.AbstractEventFacade;
import io.retailplanet.backend.common.events.metric.KafkaMetricEvent;
import io.retailplanet.backend.common.util.EventUtility;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author w.glanzer, 28.08.2019
 */
@ApplicationScoped
public class MetricEventFacade extends AbstractEventFacade
{

  @Stream("Metrics_REQUEST_OUT")
  protected Emitter<KafkaMetricEvent> metricRequestEmitter;

  @Stream("Metrics_RESPONSE_IN")
  protected Flowable<KafkaMetricEvent> metricResponseFlowable;

  @NotNull
  public Single<KafkaMetricEvent> sendMetricsEvent(@NotNull KafkaMetricEvent pEvent)
  {
    return send(pEvent, metricRequestEmitter, metricResponseFlowable);
  }

  @Override
  @PostConstruct
  public void init()
  {
    super.init();
    metricResponseFlowable = metricResponseFlowable.as(EventUtility::replayEvents);
  }

}
