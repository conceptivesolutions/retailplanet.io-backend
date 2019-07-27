package io.retailplanet.backend.common.events;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import io.retailplanet.backend.common.util.Value;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.CoordinatorNotAvailableException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Contains all specific information to build junit tests depending on kafka
 *
 * @author w.glanzer, 27.07.2019
 */
public abstract class AbstractKafkaIntegrationTest
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(AbstractKafkaIntegrationTest.class);
  private static final int _RECEIVE_TIMEOUT_MS = 2500;
  private static final int _RECEIVE_WAIT_MS = 250;
  private static final AtomicBoolean _RUNNING = new AtomicBoolean();
  private static final AtomicReference<KafkaProducer<String, AbstractEvent<?>>> _PRODUCER = new AtomicReference<>();

  @ConfigProperty(name = "retailplanet.service.group.id")
  private String serviceID;

  /**
   * Sends a message to kafka
   *
   * @param pEventName Name of the event
   * @param pEvent     Event
   */
  protected void send(@NotNull String pEventName, @NotNull AbstractEvent<?> pEvent)
  {
    try
    {
      String topic = _getTopicName(pEventName);
      if (topic == null)
        throw new IllegalArgumentException("topic " + pEventName + " is not valid");

      getProducer()
          .send(new ProducerRecord<>(topic, pEvent))
          .get(5, TimeUnit.SECONDS);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to send record", e);
    }
  }

  /**
   * Sends a message to kafka and waits for a "response".
   * This "response" is mainly generated in IEventFacade instances.
   * Fails, if there was no result
   *
   * @param pEventName     Name of the event
   * @param pEvent         Event
   * @param pEventSupplier Supplier, where to get the results from
   * @return the result (can be <tt>null</tt>, if the result was <tt>null</tt>)
   */
  @Nullable
  protected <T extends AbstractEvent<T>> T send(@NotNull String pEventName, @NotNull AbstractEvent<?> pEvent, @NotNull Value<T> pEventSupplier)
  {
    try
    {
      send(pEventName, pEvent);

      for (int i = 0; i < (_RECEIVE_TIMEOUT_MS / _RECEIVE_WAIT_MS); i++)
      {
        T value = pEventSupplier.getValue();
        if (pEventSupplier.isValueSet() && (value == null || Objects.equals(pEvent.chainID, value.chainID))) //validate chainID
          return pEventSupplier.getValueAndReset();

        Thread.sleep(_RECEIVE_WAIT_MS);
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to send record or wait for result", e);
    }

    // Fail
    throw new NoEventReceivedException("Result was not received within timeout");
  }

  /**
   * @return Producer which can send messages to the underlying kafka system
   */
  @NotNull
  protected KafkaProducer<String, AbstractEvent<?>> getProducer()
  {
    awaitServiceRunning();

    synchronized (_PRODUCER)
    {
      KafkaProducer<String, AbstractEvent<?>> prod = _PRODUCER.get();
      if (prod == null)
      {
        prod = getResource().getKafkaTestUtils().getKafkaProducer(StringSerializer.class, EventSerializer.class);
        _PRODUCER.set(prod);
      }

      return prod;
    }
  }

  /**
   * Blocking method to check, if there are all services up and running correctly
   */
  protected void awaitServiceRunning()
  {
    synchronized (_RUNNING)
    {
      // Already running
      if (_RUNNING.get())
        return;

      AdminClient adminClient = getResource().getKafkaTestUtils().getAdminClient();

      try
      {
        long startTime = System.currentTimeMillis();
        int retryCount = 0;

        while (true)
        {
          try
          {
            ConsumerGroupDescription describedGroup = adminClient.describeConsumerGroups(Collections.singletonList(getServiceID()))
                .describedGroups()
                .get(getServiceID())
                .get();

            Collection<MemberDescription> members = describedGroup.members();
            if (!members.isEmpty())
            {
              if (members.stream()
                  .map(MemberDescription::assignment)
                  .noneMatch(pAssignment -> pAssignment.topicPartitions().isEmpty()))
                break;
            }
          }
          catch (ExecutionException e)
          {
            if (retryCount++ > 3 || !(e.getCause() != null && e.getCause() instanceof CoordinatorNotAvailableException)) // ignore CoordinatorExceptions
              throw e;
          }

          // next run
          Thread.sleep(_RECEIVE_WAIT_MS);
        }

        _RUNNING.set(true);

        // Wait for assignment propagation inside quarkus (really necessary? Just to be safe...)
        Thread.sleep(_RECEIVE_WAIT_MS);

        // Log
        _LOGGER.info("Quarkus and Kafka should be up and running now. Took " + (System.currentTimeMillis() - startTime) + "ms");
      }
      catch (Exception e)
      {
        throw new RuntimeException("Failed to await running state", e);
      }
    }
  }

  /**
   * @return the serviceID for the current service containing this IntegrationTest
   */
  @NotNull
  protected String getServiceID()
  {
    return serviceID;
  }

  /**
   * @return The shared KafkaTestResource, to test against kafka backend
   */
  @NotNull
  protected abstract SharedKafkaTestResource getResource();

  /**
   * Converts an event name to a kafka topic name
   *
   * @param pEventName Name of the event
   * @return Topic
   */
  @Nullable
  private String _getTopicName(@Nullable String pEventName)
  {
    if (pEventName == null)
      return null;

    if (pEventName.endsWith("_IN"))
      return pEventName.substring(0, pEventName.length() - 3);
    else if (pEventName.endsWith("_OUT"))
      return pEventName.substring(0, pEventName.length() - 4);
    return pEventName;
  }

}
