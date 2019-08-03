package io.retailplanet.backend.common;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.*;

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

    HashMap<String, String> map = new HashMap<>();
    map.put("KAFKA_SERVERS", kafkaContainer.getBootstrapServers());
    map.put("LOG_LEVEL", "SEVERE");
    return map;
  }

  @Override
  public void stop()
  {
    if (kafkaContainer != null)
      kafkaContainer.close();
  }

}
