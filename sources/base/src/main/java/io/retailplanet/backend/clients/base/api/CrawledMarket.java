package io.retailplanet.backend.clients.base.api;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.*;

/**
 * @author w.glanzer, 31.08.2019
 */
public class CrawledMarket
{

  private String name;
  private String id;
  private Float lat, lng;
  private String address;

  public CrawledMarket()
  {
  }

  @Nullable
  public String getName()
  {
    return name;
  }

  @NotNull
  public CrawledMarket name(@Nullable String pName)
  {
    name = pName;
    return this;
  }

  @NotNull
  public String getID()
  {
    return id;
  }

  @NotNull
  public CrawledMarket id(@NotNull String pId)
  {
    id = pId;
    return this;
  }

  @Nullable
  public Pair<Float, Float> getLocation()
  {
    if (lat != null || lng != null)
      return Pair.of(lat, lng);
    return null;
  }

  @NotNull
  public CrawledMarket location(@Nullable Pair<Float, Float> pLocation)
  {
    if (pLocation != null)
    {
      lat = pLocation.getLeft();
      lng = pLocation.getRight();
    }
    else
    {
      lat = null;
      lng = null;
    }
    return this;
  }

  @NotNull
  public CrawledMarket location(@Nullable String pLocation)
  {
    if (pLocation != null && !pLocation.trim().isEmpty())
    {
      String[] geoLoc = pLocation.split(",");
      if (geoLoc.length == 2)
      {
        float lat = Float.parseFloat(geoLoc[0].trim());
        float lng = Float.parseFloat(geoLoc[1].trim());
        return location(Pair.of(lat, lng));
      }
    }

    return location((Pair<Float, Float>) null);
  }

  @Nullable
  public String getAddress()
  {
    return address;
  }

  @NotNull
  public CrawledMarket address(@Nullable String pAddress)
  {
    address = pAddress;
    return this;
  }

  @Override
  public String toString()
  {
    return "CrawledMarket{" +
        "name='" + name + '\'' +
        ", id='" + id + '\'' +
        ", lat=" + lat +
        ", lng=" + lng +
        ", address='" + address + '\'' +
        '}';
  }

}
