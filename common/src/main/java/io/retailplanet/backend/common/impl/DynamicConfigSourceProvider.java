package io.retailplanet.backend.common.impl;

import de.adito.picoservice.IPicoRegistry;
import io.retailplanet.backend.common.api.IDynamicConfig;
import org.eclipse.microprofile.config.spi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements a new ConfigSourceProvider to build configs with a simpler builder pattern, dynamically in Java
 *
 * @author w.glanzer, 11.06.2019
 */
public class DynamicConfigSourceProvider implements ConfigSourceProvider
{

  @Override
  public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader)
  {
    return IPicoRegistry.INSTANCE.find(IDynamicConfig.class, IDynamicConfig.Provider.class).keySet().stream()
        .map(pProvider -> {
          try
          {
            return (IDynamicConfig) pProvider.getConstructor().newInstance();
          }
          catch (Exception e)
          {
            throw new RuntimeException("Failed to load configprovider " + pProvider, e);
          }
        })
        .map(_DynamicConfigSource::new)
        .collect(Collectors.toList());
  }

  /**
   * Bridge between MicroProfile ConfigSources and our builders
   */
  private static class _DynamicConfigSource implements ConfigSource
  {

    private final IDynamicConfig configProvider;
    private final Map<String, String> content;

    private _DynamicConfigSource(@NotNull IDynamicConfig pConfigProvider)
    {
      configProvider = pConfigProvider;
      content = pConfigProvider.generate()
          .toMap();
    }

    @Override
    public Map<String, String> getProperties()
    {
      return content;
    }

    @Override
    public String getValue(String pName)
    {
      return getProperties().get(pName);
    }

    @Override
    public String getName()
    {
      return configProvider.getClass().getName();
    }
  }

}
