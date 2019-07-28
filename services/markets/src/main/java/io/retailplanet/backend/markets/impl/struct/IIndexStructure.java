package io.retailplanet.backend.markets.impl.struct;

/**
 * Contains all necessary information for the index backend
 *
 * @author w.glanzer, 23.06.2019
 */
public interface IIndexStructure
{

  String INDEX_TYPE = "market";

  /**
   * All fields for a market in index
   */
  interface IMarket
  {
    /* Name of the market */
    String NAME = "name";
    /* Unique ID of the market (globally unique) */
    String ID = "id";
    /* ID of the original client */
    String CLIENTID = "clientid";
    /* The location in lat,lon */
    String LOCATION = "location";
    /* Displayable address */
    String ADDRESS = "address";
  }

}
