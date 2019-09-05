package io.retailplanet.backend.common.processor;

import io.retailplanet.backend.common.util.i18n.ListUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.tools.StandardLocation;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Supplier;

/**
 * Generates application.properties
 *
 * @author w.glanzer, 19.06.2019
 */
@SupportedAnnotationTypes({})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class EventProcessor extends AbstractProcessor //todo
{

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
  {
    return false;
  }

  /**
   * Transforms the annotationprocessing elements to a readable map
   *
   * @param pContainer      Eventcontainer
   * @param pIncomingEvents all incoming events
   * @param pOutgoingEvents all outgoing events
   * @return a map for application.properties
   */
  /**@NotNull
  private Map<String, String> _toFileContent(@NotNull _EventContainer pContainer, @NotNull List<_IncomingEvent> pIncomingEvents,
                                             @NotNull List<_OutgoingEvent> pOutgoingEvents)
  {
    Map<String, String> defaultValues = new HashMap<>(pContainer.defaultValues);

    Map<String, String> map = new LinkedHashMap<>();
    map.put("retailplanet.service.group.id", pContainer.groupID);

    // Jaeger defaults
    map.put("quarkus.jaeger.service-name", pContainer.groupID);
    map.put("quarkus.jaeger.sampler-type", "const");
    map.put("quarkus.jaeger.sampler-param", "1");
    map.put("mp.opentracing.server.operation-name-provider", "http-path");

    pIncomingEvents.forEach(pEv -> map.putAll(pEv.toMap(pContainer.groupID, defaultValues)));
    pOutgoingEvents.forEach(pEv -> map.putAll(pEv.toMap(defaultValues)));
    return map;
  }*/

}
