package io.retailplanet.backend.common;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Resource to set specfic retailplanet service information to junit tests
 *
 * @author w.glanzer, 27.07.2019
 */
public class ServiceKafkaTestResource extends SharedKafkaTestResource
{

  @Override
  public void beforeAll(ExtensionContext context) throws Exception
  {
    // Shut up kafka / zookeeper!
    System.setProperty("quarkus.log.category.\"kafka\".level", "SEVERE");
    System.setProperty("quarkus.log.category.\"org.apache.zookeeper.server\".level", "SEVERE");

    super.beforeAll(context);

    _setEnvVariable("KAFKA_SERVERS", getKafkaConnectString());
    _setEnvVariable("LOG_LEVEL", "SEVERE");
  }

  /**
   * Sets a value for the current environment
   *
   * @param pVariable Variable Name
   * @param pValue    Variable Value
   */
  private void _setEnvVariable(@NotNull String pVariable, @NotNull String pValue) throws Exception
  {
    Map<String, String> env = System.getenv();
    Field field = env.getClass().getDeclaredField("m");
    field.setAccessible(true);
    ((Map<String, String>) field.get(env)).put(pVariable, pValue);
  }

}
