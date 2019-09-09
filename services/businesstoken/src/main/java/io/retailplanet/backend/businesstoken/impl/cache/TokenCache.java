package io.retailplanet.backend.businesstoken.impl.cache;

import com.mongodb.client.MongoClient;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import javax.inject.*;
import java.time.Instant;
import java.util.UUID;

/**
 * @author w.glanzer, 17.06.2019
 */
@Singleton
public class TokenCache
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(TokenCache.class);

  @Inject
  private MongoClient mongoClient;

  /**
   * Puts a token for the specific clientID in the cache
   *
   * @param pClientID ID of the client
   * @param pToken    Token
   */
  public void putToken(@NotNull String pClientID, @NotNull String pToken, @NotNull Instant pValidUntil)
  {
    Token token = new Token();
    token.id = UUID.randomUUID().toString();
    token.clientID = pClientID;
    token.sessionToken = pToken;
    token.validUntil = pValidUntil.toEpochMilli();

    // insert
    Token.insert(mongoClient, token);

    _LOGGER.info("Indexed new token " + token.id + " for client " + pClientID + ", valid until " + pValidUntil);
  }

  /**
   * Checks, if a token is valid. If it is expired, it will be removed
   *
   * @param pSessionToken session token
   * @return <tt>true</tt> if the token is valid
   */
  public STATE validateToken(@NotNull String pSessionToken)
  {
    Token token = Token.findBySessionToken(mongoClient, pSessionToken);
    if (token == null)
      return STATE.INVALID;
    if (token.validUntil < System.currentTimeMillis())
    {
      _invalidate(token);
      return STATE.EXPIRED;
    }
    return STATE.VALID;
  }

  /**
   * Returns the issuer clientid
   *
   * @param pSessionToken Session Token
   * @return the clientid, or <tt>null</tt>
   */
  @Nullable
  public String findIssuer(@NotNull String pSessionToken)
  {
    Token token = Token.findBySessionToken(mongoClient, pSessionToken);
    if (token == null)
      return null;
    return token.clientID;
  }

  /**
   * Invalidates a token and removes it from cache
   *
   * @param pToken session token
   */
  public void invalidateToken(@NotNull String pToken)
  {
    Token token = Token.findBySessionToken(mongoClient, pToken);
    if (token != null)
      _invalidate(token);
  }

  /**
   * Invalidates all expired tokens for all users
   */
  public void invalidateAllExpiredTokens()
  {
    _LOGGER.info("Invalidating all expired tokens ");
    Token.findExpiredTokens(mongoClient).forEach(this::_invalidate);
  }

  /**
   * Invalidates a given token
   *
   * @param pToken Token
   */
  private void _invalidate(@NotNull Token pToken)
  {
    _LOGGER.info("Invalidating token " + pToken.id + " for client " + pToken.clientID);
    Token.delete(mongoClient, pToken);
  }

  /**
   * Represents the current token state
   */
  public enum STATE
  {
    INVALID,
    EXPIRED,
    VALID
  }

}
