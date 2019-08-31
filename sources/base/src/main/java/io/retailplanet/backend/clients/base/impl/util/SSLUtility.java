package io.retailplanet.backend.clients.base.impl.util;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * @author w.glanzer, 12.01.2019
 */
public class SSLUtility
{

  @NotNull
  public static SSLContext createTrustAllContext()
  {
    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
    {
      public X509Certificate[] getAcceptedIssuers()
      {
        return null;
      }

      public void checkClientTrusted(X509Certificate[] certs, String authType)
      {
      }

      public void checkServerTrusted(X509Certificate[] certs, String authType)
      {
      }
    }
    };

    try
    {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      return sc;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
