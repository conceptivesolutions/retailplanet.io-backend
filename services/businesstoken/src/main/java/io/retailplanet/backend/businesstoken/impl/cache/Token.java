package io.retailplanet.backend.businesstoken.impl.cache;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;

/**
 * Represents a single business token
 *
 * @author w.glanzer, 17.06.2019
 */
@Entity
class Token extends PanacheEntity
{

  /**
   * Represents the clientID of this token
   */
  @Column(name = "CLIENTID")
  String clientID;

  /**
   * Represents the inner token, generated for this client
   */
  @Column(name = "SESSIONTOKEN")
  String sessionToken;

}
