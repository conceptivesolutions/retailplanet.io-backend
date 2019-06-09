package io.retailplanet.backend.businesstoken.api;

import io.retailplanet.backend.businesstoken.impl.BusinessToken;
import org.jetbrains.annotations.NotNull;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.*;
import java.util.UUID;

/**
 * @author w.glanzer, 09.06.2019
 */
@Path("/business/token")
public class BusinessTokenResource
{

  private static final Duration _DEFAULT_TOKEN_VALIDITY = Duration.ofHours(24);


  /**
   * Generates a new session token for a specific client.
   * If a previous session token was issued, it will be invalidated and the associated caches will be cleared.
   *
   * @param pClientID ID of the requesting client
   * @return the session token
   */
  @Transactional
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/generate")
  public Response generateToken(@QueryParam("clientid") String pClientID)
  {
    // Find previous token and invalidate if found
    BusinessToken token = BusinessToken.findByClientID(pClientID);
    if(token != null)
      invalidateSessionToken(token.session_token.toString());

    // Validate
    //noinspection ConstantConditions
    if(pClientID == null || pClientID.trim().isEmpty())
      return Response.status(Response.Status.BAD_REQUEST).build();

    // Generate new
    token = new BusinessToken();
    token.client_id = pClientID;
    token.date_new = Instant.now();
    token.valid_until = token.date_new.plus(_DEFAULT_TOKEN_VALIDITY);
    token.persist();

    // return session token
    return Response.ok(token.session_token).build();
  }

  /**
   * Returns all information about a specific token, if valid
   *
   * @param pSessionToken session token
   * @return the client id
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{token}")
  public Response inspect(@PathParam("token") String pSessionToken)
  {
    // Search token
    BusinessToken token = BusinessToken.findById(UUID.fromString(pSessionToken));
    if(token == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    // Check token validity
    if(Instant.now().isAfter(token.valid_until))
      return Response.status(Response.Status.GONE).build();

    // Token is valid -> return client id
    return Response.ok(token).build();
  }

  /**
   * Invalidates a session token.
   * Does nothing, if the given token is invalid.
   *
   * @param pSessionToken session token
   */
  @Transactional
  @DELETE
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/{token}")
  public Response invalidateSessionToken(@PathParam("token") String pSessionToken)
  {
    // Invalidate all caches, we assume that the token is valid. If not, we should not have valid caches..
    _invalidateCaches(pSessionToken);

    // Invalidate token, if it is valid
    BusinessToken token = BusinessToken.findById(UUID.fromString(pSessionToken));
    if(token != null && Instant.now().isBefore(token.valid_until))
    {
      token.valid_until = Instant.now();
      return Response.ok().build();
    }

    // Nothing done, already invalidated
    return Response.status(Response.Status.NOT_MODIFIED).build();
  }

  /**
   * Invalidates all associated caches
   *
   * @param pSessionToken Token
   */
  private void _invalidateCaches(@NotNull String pSessionToken)
  {
    // todo
  }

}
