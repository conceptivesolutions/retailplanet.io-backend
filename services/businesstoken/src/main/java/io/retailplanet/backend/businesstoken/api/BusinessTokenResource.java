package io.retailplanet.backend.businesstoken.api;

import com.google.common.collect.ImmutableMap;
import io.retailplanet.backend.businesstoken.impl.cache.TokenCache;
import io.retailplanet.backend.businesstoken.impl.services.IUserAuthService;
import io.retailplanet.backend.common.util.Utility;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.*;
import java.util.*;

/**
 * Service to generate / list all currently available businesstokens
 *
 * @author w.glanzer, 10.06.2019
 */
@Path("/business/token")
public class BusinessTokenResource
{

  /* Represents how long a token will be active by default */
  private static final Duration _TOKEN_LIFESPAN = Duration.ofHours(48);

  @Inject
  private TokenCache tokenCache;

  @Inject
  @RestClient
  private IUserAuthService userAuthService;

  /**
   * Generates a new session token for a specific client.
   * If a previous session token was issued, it will be invalidated.
   *
   * @param pClientID    ID of the requesting client
   * @param pClientToken Token to authorize the client
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/generate")
  public Map<String, Object> generateToken(@QueryParam("clientid") String pClientID, @QueryParam("token") String pClientToken)
  {
    // validate request
    if (Utility.isNullOrEmptyTrimmedString(pClientID) || Utility.isNullOrEmptyTrimmedString(pClientToken))
      throw new BadRequestException();

    // check if user is allowed to create tokens
    userAuthService.validate(pClientID, "BUSINESSTOKEN_CREATE");

    // generate token and validity
    String session_token = UUID.randomUUID().toString(); // todo
    Instant validUntil = Instant.now().plus(_TOKEN_LIFESPAN);

    // Only add valid tokens, because invalid are useless
    if (validUntil.isAfter(Instant.now()))
      tokenCache.putToken(pClientID, session_token, validUntil);

    return ImmutableMap.<String, Object>builder()
        .put("session_token", session_token)
        .put("valid_until", validUntil)
        .build();
  }

  /**
   * Invalidates the given token
   *
   * @param pSessionToken Token to be removed
   */
  @DELETE
  @Path("{session_token}")
  public void invalidateToken(@PathParam("session_token") String pSessionToken)
  {
    // validate request
    if (Utility.isNullOrEmptyTrimmedString(pSessionToken))
      throw new BadRequestException();

    // Invalidate given session_token
    tokenCache.invalidateToken(pSessionToken);
  }

}
