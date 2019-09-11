package io.retailplanet.backend.userauth.impl.userdirectory;

import io.quarkus.test.Mock;
import io.retailplanet.backend.common.util.i18n.ListUtil;
import io.retailplanet.backend.userauth.impl.*;
import org.jetbrains.annotations.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

/**
 * @author w.glanzer, 11.09.2019
 */
@Mock
@ApplicationScoped
public class DummyUserDirectoryImpl implements IUserDirectory
{

  private final List<IUser> users = ListUtil.of(
      new DummyUser("xxx", "BASE64:xxx_AVATAR_PLACEHOLDER", null),
      new DummyUser("user1", "BASE64:user1_AVATAR_PLACEHOLDER", null),
      new DummyUser("user2", "BASE64:user2_AVATAR_PLACEHOLDER", null),
      new DummyUser("user3", "BASE64:user3_AVATAR_PLACEHOLDER", null)
  );

  @Nullable
  @Override
  public IUser findUser(@NotNull String pID)
  {
    return users.stream()
        .filter(pUser -> Objects.equals(pUser.getID(), pID))
        .findFirst()
        .orElse(null);
  }

}
