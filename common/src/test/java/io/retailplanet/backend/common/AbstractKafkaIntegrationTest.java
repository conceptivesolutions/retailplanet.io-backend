package io.retailplanet.backend.common;

import io.retailplanet.backend.common.events.*;
import io.retailplanet.backend.common.util.Value;
import io.retailplanet.backend.common.util.i18n.MapUtil;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.CoordinatorNotAvailableException;
import org.apache.kafka.common.serialization.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.time.Duration;
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
  private static final int _RECEIVE_RETRY_COUNT = 10;
  private static final int _RECEIVE_WAIT_MS = 250;
  private static final AtomicBoolean _RUNNING = new AtomicBoolean();
  private static final AtomicReference<KafkaProducer<String, AbstractEvent>> _PRODUCER_REF = new AtomicReference<>();
  private static final AtomicReference<AdminClient> _ADMINCLIENT_REF = new AtomicReference<>();
  private static final Map<String, ErrorEvent> _ERRORS = new HashMap<>();

  @ConfigProperty(name = "retailplanet.service.group.id")
  private String serviceID;

  @BeforeEach
  void setUp()
  {
    String kafka_servers = System.getProperty("KAFKA_SERVERS");

    // Build Admin Client
    if (_ADMINCLIENT_REF.get() == null)
      _ADMINCLIENT_REF.set(AdminClient.create(MapUtil.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_servers)));

    // Build Producer
    if (_PRODUCER_REF.get() == null)
      _PRODUCER_REF.set(new KafkaProducer<>(MapUtil.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_servers), new StringSerializer(), new EventSerializer()));

    // Build error consumer
    KafkaConsumer<String, AbstractEvent> consumer = new KafkaConsumer<>(MapUtil.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_servers, ConsumerConfig.GROUP_ID_CONFIG, "error-consumer"),
                                                                        new StringDeserializer(), new EventDeserializer());
    consumer.subscribe(Collections.singletonList("ERRORS"));

    // Read error strean
    CompletableFuture.runAsync(() -> {
      while (!Thread.currentThread().isInterrupted())
        consumer.poll(Duration.ofMillis(250))
            .records("ERRORS")
            .forEach(pRecord -> {
              AbstractEvent event = pRecord.value();
              assert event instanceof ErrorEvent;
              _ERRORS.put(event.chainID, (ErrorEvent) event);
            });
    }, Executors.newSingleThreadExecutor())
        .whenComplete((pVoid, pEx) -> {
          if (pEx != null)
            Assertions.fail(pEx);
        });
  }

  /**
   * Sends a message to kafka
   *
   * @param pEventName Name of the event
   * @param pEvent     Event
   */
  protected void send(@NotNull String pEventName, @Nullable AbstractEvent<?> pEvent)
  {
    try
    {
      String topic = _getTopicName(pEventName);
      if (topic == null)
        throw new IllegalArgumentException("topic " + pEventName + " is not valid");

      // only if running
      _awaitServiceRunning();

      // send
      _PRODUCER_REF.get()
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
  protected <T extends AbstractEvent<T>> T send(@NotNull String pEventName, @Nullable AbstractEvent<?> pEvent, @NotNull Value<T> pEventSupplier)
  {
    try
    {
      send(pEventName, pEvent);

      // Poll events
      for (int i = 0; i < _RECEIVE_RETRY_COUNT; i++)
      {
        T value = pEventSupplier.getValue();

        // Valid response?
        if (pEventSupplier.isValueSet())
        {
          Boolean validChainID = pEvent != null && value != null ? Objects.equals(pEvent.chainID, value.chainID) : null;
          if (validChainID == null || validChainID)
            // chainID is valid
            return pEventSupplier.getValueAndReset();
          else
            // chainID invalid
            _LOGGER.warn("Received event with unexpected chainID (should: " + pEvent.chainID + " but was " + value.chainID + ")");
        }

        // Error event?
        if (pEvent != null)
        {
          ErrorEvent errorEvent = _ERRORS.get(pEvent.chainID);
          if (errorEvent != null)
            throw new ErrorEventReceivedException(errorEvent);
        }

        Thread.sleep(_RECEIVE_WAIT_MS);
      }
    }
    catch (ErrorEventReceivedException e)
    {
      // Do not catch, just rethrow
      throw e;
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to send record or wait for result", e);
    }

    // Fail
    throw new NoEventReceivedException("Result was not received within timeout");
  }

  /**
   * Blocking method to check, if there are all services up and running correctly
   */
  private void _awaitServiceRunning()
  {
    synchronized (_RUNNING)
    {
      // Already running
      if (_RUNNING.get())
        return;

      AdminClient adminClient = _ADMINCLIENT_REF.get();

      try
      {
        long startTime = System.currentTimeMillis();
        int retryCount = 0;

        while (true)
        {
          try
          {
            ConsumerGroupDescription describedGroup = adminClient.describeConsumerGroups(Collections.singletonList(_getServiceID()))
                .describedGroups()
                .get(_getServiceID())
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
  private String _getServiceID()
  {
    return serviceID;
  }

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
