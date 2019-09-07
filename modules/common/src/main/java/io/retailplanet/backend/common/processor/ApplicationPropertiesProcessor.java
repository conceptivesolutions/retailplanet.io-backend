package io.retailplanet.backend.common.processor;

import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.common.util.i18n.MapUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.StandardLocation;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.*;

/**
 * Generates application.properties
 *
 * @author w.glanzer, 19.06.2019
 */
@SupportedAnnotationTypes({"io.retailplanet.backend.common.processor.URL", "org.eclipse.microprofile.rest.client.inject.RegisterRestClient"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ApplicationPropertiesProcessor extends AbstractProcessor
{

  private static final String _BUILD_TIMESTAMP_ENV = "BUILD_TIMESTAMP";
  private static final boolean _BUILD_DEV_MODE = Utility.isNullOrEmptyTrimmedString(_BUILD_TIMESTAMP_ENV);

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
  {
    if (roundEnv.processingOver() || annotations.isEmpty())
      return false;

    Map<String, String> content = new HashMap<>();

    // Defaults
    content.put("io.retailplanet.backend.metrics.client.IMetricServerService/mp-rest/url", _toURL(URL.ETarget.METRICS));
    if (!_BUILD_DEV_MODE)
      content.put("build.timestamp", System.getenv(_BUILD_TIMESTAMP_ENV));

    // URLs
    for (TypeElement annotation : annotations)
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation))
        content.putAll(_getServiceURLMappings(element));

    // Write
    _writeToApplicationProperties(content);

    return false;
  }

  @NotNull
  private Map<String, String> _getServiceURLMappings(@NotNull Element pElement)
  {
    URL urlAnno = pElement.getAnnotation(URL.class);
    if (urlAnno == null)
      throw new IllegalArgumentException("An RestClient has to be annotated with @URL to specify the target in application.properties");
    return MapUtil.of(pElement.asType().toString() + "/mp-rest/url", _toURL(urlAnno.targetModule()));
  }

  @NotNull
  private String _toURL(@NotNull URL.ETarget pTarget)
  {
    return _BUILD_DEV_MODE ? pTarget.getLocalURL() : pTarget.getProductionURL();
  }

  /**
   * Writes all given contents to the application.properties file
   *
   * @param pContent Content
   */
  private void _writeToApplicationProperties(@NotNull Map<String, String> pContent)
  {
    try
    {
      try (Writer writer = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "application.properties").openWriter())
      {
        writer.append(_getApplicationPropertiesContent())
            .append('\n')
            .append("# Autogenerated by ").append(getClass().getName())
            .append('\n');
        for (Map.Entry<String, String> entry : pContent.entrySet())
          writer.append(entry.getKey())
              .append("=")
              .append(entry.getValue())
              .append("\n");
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return The source content of application.properties
   */
  @NotNull
  private String _getApplicationPropertiesContent() throws IOException
  {
    URI targetFolderUri = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "nothing").toUri();
    File moduleFolder = new File(targetFolderUri).getParentFile().getParentFile().getParentFile();
    File propertiesFile = new File(moduleFolder, "src/main/resources/_application.properties");
    if (!propertiesFile.exists())
      return "";
    return String.join("\n", Files.readAllLines(propertiesFile.toPath()));
  }

}
