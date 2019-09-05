package io.retailplanet.backend.common.events.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.retailplanet.backend.common.events.AbstractEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Event which will be sent if the metric-api wants to communicate with the answer service to get response times
 *
 * @author w.glanzer, 28.08.2019
 */
@RegisterForReflection
public class MetricEvent extends AbstractEvent<MetricEvent>
{

  @JsonProperty
  Instant answered;

  @JsonProperty
  Instant started;

  /**
   * @return value of 'answered' field
   */
  public Instant answered()
  {
    return answered;
  }

  @NotNull
  public MetricEvent answered(Instant pAnswered)
  {
    answered = pAnswered;
    return this;
  }

  /**
   * @return value of 'started' field
   */
  public Instant started()
  {
    return started;
  }

  @NotNull
  public MetricEvent started(Instant pStarted)
  {
    started = pStarted;
    return this;
  }

}
