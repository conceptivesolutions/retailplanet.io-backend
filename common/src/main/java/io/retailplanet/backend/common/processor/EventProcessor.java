package io.retailplanet.backend.common.processor;

import io.retailplanet.backend.common.api.comm.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * Generates application.properties
 *
 * @author w.glanzer, 19.06.2019
 */
@SupportedAnnotationTypes({"io.retailplanet.backend.common.api.comm.IncomingEvent",
                           "io.retailplanet.backend.common.api.comm.OutgoingEvent",
                           "io.retailplanet.backend.common.api.comm.EventContainer"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class EventProcessor extends AbstractProcessor
{

  public EventProcessor()
  {
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
  {
    if (roundEnv.processingOver() || annotations.isEmpty())
      return false;

    _EventContainer container = null;
    List<_IncomingEvent> incomingEvents = new ArrayList<>();
    List<_OutgoingEvent> outgoingEvents = new ArrayList<>();
    for (TypeElement annotation : annotations)
    {
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation))
      {
        switch (annotation.getQualifiedName().toString())
        {
          case "io.retailplanet.backend.common.api.comm.IncomingEvent":
            incomingEvents.add(new _IncomingEvent(element));
            break;

          case "io.retailplanet.backend.common.api.comm.OutgoingEvent":
            outgoingEvents.add(new _OutgoingEvent(element));
            break;

          case "io.retailplanet.backend.common.api.comm.EventContainer":
            container = new _EventContainer(element);
            break;
        }
      }
    }

    if (container == null)
      throw new RuntimeException("Missing EventContainer");

    // Translate to application.properties content
    Map<String, String> content = _toFileContent(container, incomingEvents, outgoingEvents);

    // Write
    _writeToApplicationProperties(content);

    return true;
  }

  private void _writeToApplicationProperties(@NotNull Map<String, String> pContent)
  {
    try
    {
      CharSequence prevContent;
      try
      {
        prevContent = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "application.properties").getCharContent(false);
      }
      catch (Exception e)
      {
        prevContent = "";
      }

      try (Writer writer = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "application.properties").openWriter())
      {
        writer.append(prevContent);
        writer.append("\n");
        pContent.forEach((pKey, pValue) -> {
          try
          {
            writer.append(pKey).append("=").append(pValue).append("\n");
          }
          catch (IOException pE)
          {
            pE.printStackTrace();
          }
        });
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  private Map<String, String> _toFileContent(@NotNull _EventContainer pContainer, @NotNull List<_IncomingEvent> pIncomingEvents,
                                             @NotNull List<_OutgoingEvent> pOutgoingEvents)
  {
    Map<String, String> defaultValues = new HashMap<>(pContainer.defaultValues);
    defaultValues.put("type", "io.smallrye.reactive.messaging.kafka.Kafka");
    defaultValues.put("bootstrap.servers", "${KAFKA_SERVERS}");

    Map<String, String> map = new LinkedHashMap<>();
    pIncomingEvents.forEach(pEv -> map.putAll(pEv.toMap(pContainer.groupID, defaultValues)));
    pOutgoingEvents.forEach(pEv -> map.putAll(pEv.toMap(defaultValues)));
    return map;
  }

  private static class _EventContainer
  {
    private final String groupID;
    private final Map<String, String> defaultValues = new HashMap<>();

    private _EventContainer(Element pElement)
    {
      EventContainer anno = pElement.getAnnotation(EventContainer.class);
      groupID = anno.groupID();
      String[] defaults = anno.defaults();
      for (int i = 0; i < defaults.length; i = i + 2)
        defaultValues.put(defaults[i], defaults[i + 1]);
    }
  }

  private static class _IncomingEvent
  {
    private static final String _IN_SUFFIX = "_IN";
    private final String name;
    private final String topic;
    private final String autoOffsetReset;
    private final String keyDeserializer;
    private final String valueDeserializer;

    private _IncomingEvent(Element pElement)
    {
      name = ElementFilter.fieldsIn(Collections.singletonList(pElement)).get(0).getConstantValue().toString();
      if (!name.endsWith(_IN_SUFFIX))
        throw new RuntimeException(name + " is not a valid INCOMINGEVENT name");

      topic = name.substring(0, name.length() - _IN_SUFFIX.length());

      IncomingEvent event = pElement.getAnnotation(IncomingEvent.class);
      autoOffsetReset = event.autoOffsetReset();
      keyDeserializer = _getClassName(event::keyDeserializer);
      valueDeserializer = _getClassName(event::valueDeserializer);
    }

    @NotNull
    private Map<String, String> toMap(@NotNull String pGroupID, @NotNull Map<String, String> pDefaultValues)
    {
      String keyPrefix = "smallrye.messaging.source." + name.replace('.', '-') + ".";
      Map<String, String> result = new HashMap<>();

      // Add defaults
      pDefaultValues.forEach((pKey, pValue) -> result.put(keyPrefix + pKey, pValue));

      // Add all our values
      result.put(keyPrefix + "topic", topic);
      result.put(keyPrefix + "group.id", pGroupID);
      result.put(keyPrefix + "auto.offset.reset", autoOffsetReset);
      result.put(keyPrefix + "key.deserializer", keyDeserializer);
      result.put(keyPrefix + "value.deserializer", valueDeserializer);

      return result;
    }
  }

  private static class _OutgoingEvent
  {
    private static final String _OUT_SUFFIX = "_OUT";
    private final String name;
    private final String topic;
    private final String keySerializer;
    private final String valueSerializer;

    private _OutgoingEvent(Element pElement)
    {
      name = ElementFilter.fieldsIn(Collections.singletonList(pElement)).get(0).getConstantValue().toString();
      if (!name.endsWith(_OUT_SUFFIX))
        throw new RuntimeException(name + " is not a valid OUTGOINGEVENT name");

      topic = name.substring(0, name.length() - _OUT_SUFFIX.length());
      OutgoingEvent ev = pElement.getAnnotation(OutgoingEvent.class);
      keySerializer = _getClassName(ev::keySerializer);
      valueSerializer = _getClassName(ev::valueSerializer);
    }

    @NotNull
    private Map<String, String> toMap(@NotNull Map<String, String> pDefaultValues)
    {
      String keyPrefix = "smallrye.messaging.sink." + name.replace('.', '-') + ".";
      Map<String, String> result = new HashMap<>();

      // Add defaults
      pDefaultValues.forEach((pKey, pValue) -> result.put(keyPrefix + pKey, pValue));

      // Add all our values
      result.put(keyPrefix + "topic", topic);
      result.put(keyPrefix + "key.serializer", keySerializer);
      result.put(keyPrefix + "value.serializer", valueSerializer);
      result.put(keyPrefix + "acks", "1");

      return result;
    }
  }

  private static String _getClassName(Supplier<Class<?>> pSupplier)
  {
    try
    {
      return pSupplier.get().getName();
    }
    catch (MirroredTypeException ex)
    {
      return ex.getTypeMirror().toString();
    }
  }

}
