package io.retailplanet.backend.common.api;

import de.adito.picoservice.PicoService;
import org.jetbrains.annotations.*;

import java.lang.annotation.*;
import java.util.*;

/**
 * Dynamic configuration for MicroProfile-Services
 *
 * @author w.glanzer, 11.06.2019
 */
@SuppressWarnings("WeakerAccess")
public interface IDynamicConfig
{

  /**
   * @return Generates the filled config builder
   */
  @NotNull
  Builder generate();

  /**
   * Annotation for all DynamicConfig-Providers
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @PicoService
  @interface Provider
  {
  }

  /**
   * Builder for DynmicConfigs
   */
  class Builder
  {
    private final Map<String, String> defaultValues = new HashMap<>();
    private final List<TopicBuilder> topics = new ArrayList<>();

    @NotNull
    public static Builder create()
    {
      return new Builder()
          .defaultValue("type", "io.smallrye.reactive.messaging.kafka.Kafka");
    }

    private Builder()
    {
    }

    /**
     * Adds a new topic to this builder config
     *
     * @param pTopic Topic
     */
    @NotNull
    public Builder topic(@NotNull TopicBuilder pTopic)
    {
      topics.add(pTopic);
      return this;
    }

    /**
     * Adds a new default value for all topics
     *
     * @param pKey   Key
     * @param pValue Value
     */
    @NotNull
    public Builder defaultValue(@NotNull String pKey, @NotNull String pValue)
    {
      defaultValues.put(pKey, pValue);
      return this;
    }

    @NotNull
    public Map<String, String> toMap()
    {
      Map<String, String> result = new HashMap<>();
      topics.stream()
          .map(pBuilder -> pBuilder.toMap(defaultValues))
          .forEach(result::putAll);
      return result;
    }
  }

  /**
   * Builder for Topics
   */
  class TopicBuilder
  {
    private static final String _IN_SUFFIX = "_IN";
    private static final String _OUT_SUFFIX = "_OUT";
    private final String name;
    private final boolean read;
    private final Map<String, String> values = new HashMap<>();

    /**
     * Creates a new READ topic
     *
     * @param pName              Name
     * @param pGroupId           ID of the group this reader belongs to
     * @param pAutoOffsetReset   earliest, latest, none -> Method to be used, when no offset is set (new client)
     * @param pKeyDeserializer   Deserializer-Class for the key
     * @param pValueDeserializer Deserializer-Class for the value
     * @return the Builder
     */
    @NotNull
    public static TopicBuilder createRead(@NotNull String pName, @NotNull String pGroupId, @NotNull String pAutoOffsetReset,
                                          @NotNull Class<?> pKeyDeserializer, @NotNull Class<?> pValueDeserializer)
    {
      return createRead(pName, pGroupId, pAutoOffsetReset, pKeyDeserializer.getName(), pValueDeserializer.getName());
    }

    /**
     * Creates a new READ topic
     *
     * @param pName              Name
     * @param pGroupId           ID of the group this reader belongs to
     * @param pAutoOffsetReset   earliest, latest, none -> Method to be used, when no offset is set (new client)
     * @param pKeyDeserializer   Deserializer-Classname for the key
     * @param pValueDeserializer Deserializer-Classname for the value
     * @return the Builder
     */
    @NotNull
    public static TopicBuilder createRead(@NotNull String pName, @NotNull String pGroupId, @NotNull String pAutoOffsetReset,
                                          @NotNull String pKeyDeserializer, @NotNull String pValueDeserializer)
    {
      if(!pName.endsWith(_IN_SUFFIX))
        throw new RuntimeException(pName + " is not a valid READ name");

      return new TopicBuilder(pName, pName.substring(0, pName.length() - _IN_SUFFIX.length()), true)
          .autoOffsetReset(pAutoOffsetReset)
          .groupId(pGroupId)
          .keyDeserializer(pKeyDeserializer)
          .valueDeserializer(pValueDeserializer);
    }

    /**
     * Creates a new WRITE topic
     *
     * @param pName            Name
     * @param pKeySerializer   Serializer-Class for the key
     * @param pValueSerializer Serializer-Class for the value
     * @return the Builder
     */
    @NotNull
    public static TopicBuilder createWrite(@NotNull String pName, @NotNull Class<?> pKeySerializer, @NotNull Class<?> pValueSerializer)
    {
      return createWrite(pName, pKeySerializer.getName(), pValueSerializer.getName());
    }

    /**
     * Creates a new WRITE topic
     *
     * @param pName            Name
     * @param pKeySerializer   Serializer-Classname for the key
     * @param pValueSerializer Serializer-Classname for the value
     * @return the Builder
     */
    @NotNull
    public static TopicBuilder createWrite(@NotNull String pName, @NotNull String pKeySerializer, @NotNull String pValueSerializer)
    {
      if(!pName.endsWith(_OUT_SUFFIX))
        throw new RuntimeException(pName + " is not a valid WRITE name");

      return new TopicBuilder(pName, pName.substring(0, pName.length() - _OUT_SUFFIX.length()), false)
          .keySerializer(pKeySerializer)
          .valueSerializer(pValueSerializer)
          .acks(1);
    }

    private TopicBuilder(@NotNull String pName, @NotNull String pTopicName, boolean pIsRead)
    {
      name = pName;
      read = pIsRead;

      // intial topic name
      values.put("topic", pTopicName);
    }

    @NotNull
    public TopicBuilder keySerializer(@NotNull String pKeySerializer)
    {
      values.put("key.serializer", pKeySerializer);
      return this;
    }

    @NotNull
    public TopicBuilder keyDeserializer(@NotNull String pKeyDeserializer)
    {
      values.put("key.deserializer", pKeyDeserializer);
      return this;
    }

    @NotNull
    public TopicBuilder valueSerializer(@NotNull String pValueSerializer)
    {
      values.put("value.serializer", pValueSerializer);
      return this;
    }

    @NotNull
    public TopicBuilder valueDeserializer(@NotNull String pValueDeserializer)
    {
      values.put("value.deserializer", pValueDeserializer);
      return this;
    }

    @NotNull
    public TopicBuilder autoOffsetReset(@Nullable String pType)
    {
      values.put("auto.offset.reset", pType);
      return this;
    }

    @NotNull
    public TopicBuilder acks(int pAcks)
    {
      values.put("acks", String.valueOf(pAcks));
      return this;
    }

    @NotNull
    public TopicBuilder groupId(@NotNull String pGroupId)
    {
      values.put("group.id", pGroupId);
      return this;
    }

    @NotNull
    Map<String, String> toMap(@NotNull Map<String, String> pDefaultValues)
    {
      String keyPrefix = "smallrye.messaging." + (read ? "source." : "sink.") + name.replace('.', '-') + ".";
      Map<String, String> result = new HashMap<>();

      // Add defaults
      pDefaultValues.forEach((pKey, pValue) -> result.put(keyPrefix + pKey, pValue));

      // Add defined
      values.forEach((pKey, pValue) -> result.put(keyPrefix + pKey, pValue));

      return result;
    }

  }
}
