package io.retailplanet.backend.userauth.api;

import io.retailplanet.backend.common.util.Utility;
import io.retailplanet.backend.userauth.impl.*;

import javax.inject.Inject;
import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * Endpoint for all profile requests
 *
 * @author w.glanzer, 24.07.2019
 */
@Path("/profile")
public class ProfileResource
{

  @Inject
  private IUserDirectory userDirectory;

  /**
   * Returns information about the current logged in user
   *
   * @param pBearerToken Token for the current user
   * @return Object for information
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Object getInformations(@HeaderParam("Authorization") String pBearerToken)
  {
    if (Utility.isNullOrEmptyTrimmedString(pBearerToken))
      return Response.status(Response.Status.BAD_REQUEST).build();

    //todo verify permissions

    // Profile-Infos
    IUser user = userDirectory.findUser("xxx"); //todo userid
    if (user == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    // Return POJO
    User response = new User();
    response.id = user.getID();
    response.roles = user.getRoles();
    return response;
  }

  /**
   * Returns the currently active avatar of a user
   *
   * @param pUserID           ID of the user to search for
   * @param pCacheIfNoneMatch Cache Parameter for the browser
   * @return response
   */
  @GET
  @Path("avatar/{userid}.png")
  @Produces("image/png")
  public Object getAvatar(@PathParam("userid") String pUserID, @HeaderParam("If-None-Match") String pCacheIfNoneMatch)
  {
    //todo cache

    // Profile-Infos
    IUser user = userDirectory.findUser(pUserID);
    if (user == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    return user.getAvatar();
  }

  /**
   * Updates the current users information
   *
   * @param pBearerToken Token to identify the "current" user
   * @param pUser        New user object, contains only the necessary information to update
   * @return Response
   */
  @PATCH
  public Response update(@HeaderParam("Authorization") String pBearerToken, User pUser)
  {
    if (pUser == null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    // Get User
    IUser loggedInUser = userDirectory.findUser("xxx"); //todo userid
    if (loggedInUser == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    // Update necessary
    String newAvatarURL = pUser.avatar != null ? pUser.avatar.url : null;
    if (newAvatarURL != null)
      loggedInUser.getMutable().setAvatarFromURL(newAvatarURL);
    String newAvatarBase64 = pUser.avatar != null ? pUser.avatar.base64 : null;
    if (newAvatarBase64 != null)
      loggedInUser.getMutable().setAvatarFromBase64(newAvatarBase64);

    return Response.ok().build();
  }

  /**
   * POJO für User-Interatkionen (GET / PATCH)
   */
  public static class User
  {
    @JsonbProperty
    public String id;

    @JsonbProperty
    public List<String> roles;

    @JsonbProperty
    public Avatar avatar;
  }

  /**
   * POJO für einen Avatar
   */
  public static class Avatar
  {
    @JsonbProperty
    public String url;

    @JsonbProperty
    public String base64;
  }

}
