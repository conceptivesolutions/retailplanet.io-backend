package io.retailplanet.backend.userauth.impl.userdirectory;

import io.retailplanet.backend.common.util.i18n.ListUtil;
import io.retailplanet.backend.userauth.impl.IUser;
import org.jetbrains.annotations.*;

import java.util.List;

/**
 * @author w.glanzer, 11.09.2019
 */
class DummyUser implements IUser, IUser.IMutable
{

  private String id;
  private String avatar;
  private List<String> roles;

  public DummyUser(@NotNull String pId, @Nullable String pAvatar, @Nullable List<String> pRoles)
  {
    id = pId;
    avatar = pAvatar;
    roles = pRoles;
  }

  @NotNull
  @Override
  public String getID()
  {
    return id;
  }

  @Nullable
  @Override
  public String getAvatar()
  {
    return avatar;
  }

  @NotNull
  @Override
  public List<String> getRoles()
  {
    return roles == null ? ListUtil.of() : roles;
  }

  @NotNull
  @Override
  public IMutable getMutable()
  {
    return this;
  }

  @Override
  public void setAvatarFromURL(@Nullable String pURL)
  {

  }

  @Override
  public void setAvatarFromBase64(@Nullable String pBase64)
  {

  }

}
