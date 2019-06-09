package io.retailplanet.backend.businesstoken.impl;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.*;
import org.jetbrains.annotations.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single busines token, specified for a single client
 *
 * @author w.glanzer, 09.06.2019
 */
@Entity
public class BusinessToken extends PanacheEntityBase
{

  @Id
  @GeneratedValue
  @Column(name = "SESSION_TOKEN", columnDefinition = "UUID")
  public UUID session_token;

  @Column(name = "CLIENT_ID")
  public String client_id;

  @Column(name = "VALID_UNTIL")
  public Instant valid_until;

  @Column(name = "DATE_NEW")
  public Instant date_new;

  /**
   * Returns the latest token issued by a specific client
   *
   * @param pClientID ID of the client
   * @return the latest token
   */
  @Nullable
  public static BusinessToken findByClientID(@NotNull String pClientID)
  {
    return BusinessToken
        .<BusinessToken>stream("client_id = :client_id",
                Sort.descending("date_new"),
                Parameters.with("client_id", pClientID))
        .findFirst()
        .orElse(null);
  }

}
