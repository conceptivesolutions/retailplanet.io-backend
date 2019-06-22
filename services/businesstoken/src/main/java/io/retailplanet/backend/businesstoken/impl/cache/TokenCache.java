package io.retailplanet.backend.businesstoken.impl.cache;

import org.jetbrains.annotations.*;
import org.slf4j.*;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author w.glanzer, 17.06.2019
 */
@Singleton
public class TokenCache
{

  private static final Logger _LOGGER = LoggerFactory.getLogger(TokenCache.class);

  /**
   * Puts a token for the specific clientID in the cache
   *
   * @param pClientID ID of the client
   * @param pToken    Token
   */
  @Transactional
  public void putToken(@NotNull String pClientID, @NotNull String pToken, @NotNull Instant pValidUntil)
  {
    Token token = new Token();
    token.clientID = pClientID;
    token.sessionToken = pToken;
    token.validUntil = new Timestamp(pValidUntil.toEpochMilli());
    token.persist();

    _LOGGER.info("Indexed new token " + token.id + " for client " + pClientID + ", valid until " + pValidUntil);
  }

  /**
   * Checks, if a token is valid. If it is expired, it will be removed
   *
   * @param pSessionToken session token
   * @return <tt>true</tt> if the token is valid
   */
  @Transactional
  public STATE validateToken(@NotNull String pSessionToken)
  {
    Token token = Token.findBySessionToken(pSessionToken);
    if (token == null)
      return STATE.INVALID;
    if (token.validUntil != null && token.validUntil.toInstant().isBefore(Instant.now()))
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
  @Transactional
  public String findIssuer(@NotNull String pSessionToken)
  {
    Token token = Token.findBySessionToken(pSessionToken);
    if (token == null)
      return null;
    return token.clientID;
  }

  /**
   * Invalidates a token and removes it from cache
   *
   * @param pToken session token
   */
  @Transactional
  public void invalidateToken(@NotNull String pToken)
  {
    Token token = Token.findBySessionToken(pToken);
    if(token != null)
      _invalidate(token);
  }

  /**
   * Invalidates all tokens for a specific user
   *
   * @param pClientID ID of the client to invalidate all tokens
   */
  @Transactional
  public void invalidateAllTokens(@NotNull String pClientID)
  {
    Token.findByClientID(pClientID).forEach(this::_invalidate);
  }

  /**
   * Invalidates all expired tokens for all users
   */
  @Transactional
  public void invalidateAllExpiredTokens()
  {
    Token.findExpiredTokens(Instant.now()).forEach(this::_invalidate);
  }

  /**
   * Invalidates a given token
   *
   * @param pToken Token
   */
  @Transactional
  private void _invalidate(@NotNull Token pToken)
  {
    _LOGGER.info("Invalidating token " + pToken.id + " for client " + pToken.clientID);
    pToken.delete();
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
