package io.retailplanet.backend.products.impl.struct;

/**
 * Contains all necessary information for the index backend
 *
 * @author w.glanzer, 21.06.2019
 */
public interface IIndexStructure
{

  /**
   * All fields for a product in index
   */
  interface IProduct
  {
    /* Name of the product */
    String NAME = "name";
    /* Unique ID of the product (index-unique, not global) */
    String ID = "id";
    /* URL for this product */
    String URL = "url";
    /* Current price */
    String PRICE = "price";
    /* Category of the product */
    String CATEGORY = "category";
    /* List of all previews */
    String PREVIEWS = "previews";
    /* additional information */
    String ADDITIONAL_INFO = "additionalInfo";
    /* All information about the availability */
    String AVAILABILITY = "availability";
    /* ID of the client, where the product comes from */
    String CLIENT_ID = "client_id";
    /* Time this product was inserted / updated */
    String UPDATED = "updated";
  }

  /**
   * All necessary fields to determine the availability of a product in a specific market
   */
  interface IAvailability
  {
    /* TYPE, if AVAILABLE, ORDERABLE, ... */
    String TYPE = "type";
    /* Current quantity (optional) */
    String QUANTITY = "quantity";
  }

}
