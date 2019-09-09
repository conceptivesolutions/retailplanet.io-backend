package io.retailplanet.backend.businesstoken.impl.cache;

import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a single business token
 *
 * @author w.glanzer, 17.06.2019
 */
class Token
{

  /**
   * Represents the ID of this token
   */
  String id;

  /**
   * Represents the clientID of this token
   */
  String clientID;

  /**
   * Represents the inner token, generated for this client
   */
  String sessionToken;

  /**
   * Represents, how long the token will be active. After this timestamp, the token will be inactive
   */
  long validUntil;

  /**
   * Inserts a new token in the database
   *
   * @param pMongoClient MongoClient
   * @param pToken       Token to index
   */
  public static void insert(@NotNull MongoClient pMongoClient, @NotNull Token pToken)
  {
    pMongoClient
        .getDatabase("businesstoken")
        .getCollection("tokens")
        .insertOne(new Document()
                       .append("_id", pToken.id)
                       .append("clientID", pToken.clientID)
                       .append("sessionToken", pToken.sessionToken)
                       .append("validUntil", pToken.validUntil));
  }

  /**
   * Returns a token which was retrieved by a sessionToken
   *
   * @param pMongoClient  MongoClient
   * @param pSessionToken Token to search for
   * @return the Token instance, or <tt>null</tt>
   */
  @Nullable
  public static Token findBySessionToken(@NotNull MongoClient pMongoClient, @NotNull String pSessionToken)
  {
    Document doc = pMongoClient
        .getDatabase("businesstoken")
        .getCollection("tokens")
        .find(new Document().append("sessionToken", pSessionToken))
        .first();

    if (doc == null)
      return null;

    Token token = new Token();
    token.id = doc.getString("_id");
    token.clientID = doc.getString("clientID");
    token.sessionToken = doc.getString("sessionToken");
    token.validUntil = doc.getLong("validUntil");
    return token;
  }

  /**
   * Returns a list of all expired tokens
   *
   * @param pMongoClient MongoClient
   * @return All expired tokens
   */
  @NotNull
  public static List<Token> findExpiredTokens(@NotNull MongoClient pMongoClient)
  {
    List<Token> expiredTokens = new ArrayList<>();

    pMongoClient
        .getDatabase("businesstoken")
        .getCollection("tokens")
        .find(new Document().append("validUntil", new Document("$lt", System.currentTimeMillis())))
        .forEach((Consumer<Document>) doc -> {
          Token token = new Token();
          token.id = doc.getString("_id");
          token.clientID = doc.getString("clientID");
          token.sessionToken = doc.getString("sessionToken");
          token.validUntil = doc.getLong("validUntil");
          expiredTokens.add(token);
        });

    return expiredTokens;
  }

  /**
   * Deletes a token from database
   *
   * @param pMongoClient MongoClient
   * @param pToken       Token to delete
   */
  public static void delete(@NotNull MongoClient pMongoClient, @NotNull Token pToken)
  {
    pMongoClient
        .getDatabase("businesstoken")
        .getCollection("tokens")
        .deleteOne(new Document("_id", pToken.id));
  }

}
