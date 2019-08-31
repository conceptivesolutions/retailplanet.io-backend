package io.retailplanet.backend.clients.base.impl.util;

import io.retailplanet.backend.common.util.i18n.ISupplierEx;
import org.jetbrains.annotations.*;
import org.slf4j.Logger;

import java.util.Random;

/**
 * @author w.glanzer, 09.01.2019
 */
public class RetryManager
{

  public static <T, Ex extends Exception> T retry(@NotNull ISupplierEx<T, Ex> pFn, int pRetryCount, int pRetryTimeout,
                                                  @Nullable String pFailMessage, @NotNull Logger pLogger) throws Ex
  {
    int retryCount = 0;
    while (true)
    {
      try
      {
        return pFn.get();
      }
      catch (Exception e)
      {
        if (retryCount++ >= pRetryCount)
          throw e;
        else
        {
          try
          {
            if (pFailMessage != null)
            {
              pLogger.warn(pFailMessage + ". Retrying .. " + retryCount + "/" + pRetryCount);
            }

            Thread.sleep((int) (new Random().nextDouble() * (double) pRetryTimeout));
          }
          catch (InterruptedException ignored)
          {
          }
        }
      }
    }
  }

}
