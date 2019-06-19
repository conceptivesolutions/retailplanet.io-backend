package io.retailplanet.backend.businesstoken.impl.cache;

import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

/**
 * @author w.glanzer, 17.06.2019
 */
@ApplicationScoped
public class TokenCache
{

  /**
   * Puts a token for the specific clientID in the cache
   *
   * @param pClientID ID of the client
   * @param pToken    Token
   */
  @Transactional
  public void putToken(@NotNull String pClientID, @NotNull String pToken)
  {
    Token token = new Token();
    token.clientID = pClientID;
    token.sessionToken = pToken;
    token.persist();
  }

}
