package io.retailplanet.backend.userauth.impl;

import org.jetbrains.annotations.*;

/**
 * A user directory contains all known users of the whole system
 *
 * @author w.glanzer, 24.07.2019
 */
public interface IUserDirectory
{

  /**
   * Search for a user with the current userID
   *
   * @param pID user id
   * @return User, or <tt>null</tt>
   */
  @Nullable
  IUser findUser(@NotNull String pID);

}
