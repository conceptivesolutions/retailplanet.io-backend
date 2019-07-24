package io.retailplanet.backend.userauth.impl.userdirectory;

import io.retailplanet.backend.userauth.impl.*;
import org.jetbrains.annotations.*;

import javax.inject.Singleton;

/**
 * UserDirectory
 *
 * @author w.glanzer, 24.07.2019
 */
@Singleton
public class UserDirectoryImpl implements IUserDirectory
{

  @Nullable
  @Override
  public IUser findUser(@NotNull String pID)
  {
    // todo implement user directory
    return null;
  }

}
