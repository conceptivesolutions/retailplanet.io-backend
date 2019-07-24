package io.retailplanet.backend.userauth.impl;

import org.jetbrains.annotations.*;

import java.util.List;

/**
 * Contains information about a single user
 *
 * @author w.glanzer, 24.07.2019
 */
public interface IUser
{

  /**
   * @return Unique ID of the user
   */
  @NotNull
  String getID();

  /**
   * @return Current avatar
   */
  @Nullable
  String getAvatar();

  /**
   * @return all roles that are assigned to this user
   */
  @NotNull
  List<String> getRoles();

  /**
   * @return Object to modify this user
   */
  @NotNull
  IMutable getMutable();

  interface IMutable
  {

    /**
     * Set a new avatar by URL
     *
     * @param pURL URL
     */
    void setAvatarFromURL(@Nullable String pURL);

    /**
     * Set a new avatar by base64
     *
     * @param pBase64 Base64 String
     */
    void setAvatarFromBase64(@Nullable String pBase64);

  }

}
