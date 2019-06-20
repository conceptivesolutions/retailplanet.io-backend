package io.retailplanet.backend.businesstoken.impl.cache;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;
import org.jetbrains.annotations.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.stream.Stream;

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

  /**
   * Represents, how long the token will be active. After this timestamp, the token will be inactive
   */
  @Column(name = "VALID")
  Timestamp validUntil;

  /**
   * Returns the Token instance for a session token
   *
   * @param pSessionToken Session Token
   * @return the token, or <tt>null</tt>
   */
  @Nullable
  public static Token findBySessionToken(@NotNull String pSessionToken)
  {
    return find("SESSIONTOKEN", pSessionToken)
        .firstResult();
  }

  /**
   * Returns all token instances for the given client
   *
   * @param pClientID Client ID
   * @return the tokens
   */
  @NotNull
  public static Stream<Token> findByClientID(@NotNull String pClientID)
  {
    return find("CLIENTID", pClientID).stream();
  }

  /**
   * Returns a list of all expired tokens
   *
   * @param pTime Timestamp to determine, if a token is expired
   * @return Token stream
   */
  @NotNull
  public static Stream<Token> findExpiredTokens(@NotNull Instant pTime)
  {
    return find("VALID < :time", Parameters.with("time", pTime.toEpochMilli())).stream();
  }

}
