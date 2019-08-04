package io.retailplanet.backend.common;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.retailplanet.backend.common.util.i18n.MapUtil;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Map;

/**
 * Resource to start and stop kafka servers in quarkus tests
 *
 * @author w.glanzer, 04.08.2019
 */
public class KafkaTestResource implements QuarkusTestResourceLifecycleManager
{

  @Container
  private KafkaContainer kafkaContainer;

  @Override
  public Map<String, String> start()
  {
    kafkaContainer = new KafkaContainer("5.2.1");
    kafkaContainer.start();

    return MapUtil.of(
        "KAFKA_SERVERS", kafkaContainer.getBootstrapServers(),
        "LOG_LEVEL", "SEVERE"
    );
  }

  @Override
  public void stop()
  {
    if (kafkaContainer != null)
      kafkaContainer.close();
  }

}
