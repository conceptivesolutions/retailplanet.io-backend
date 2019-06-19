package io.retailplanet.backend.common.util;

import org.h2.tools.Server;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Class which provides an easy way to start an H2 instance
 *
 * @author w.glanzer, 17.06.2019
 */
public class H2Starter
{

  private static final Logger _LOGGER = Logger.getLogger(H2Starter.class.getName());
  private static H2Starter _INSTANCE;
  private Server server;

  /**
   * @return The singleton H2Starter-Instance
   */
  @NotNull
  public static H2Starter getInstance()
  {
    if(_INSTANCE == null)
      _INSTANCE = new H2Starter();
    return _INSTANCE;
  }

  /**
   * Start the instance
   */
  public void start()
  {
    try
    {
      server = Server.createTcpServer();
      server.start();
      _LOGGER.info("H2 database started in TCP server mode");
    }
    catch (SQLException e)
    {
      throw new RuntimeException("Failed to start H2 instance", e);
    }
  }

  /**
   * Stop the instance
   */
  public void stop()
  {
    if(server != null)
      server.stop();
    _LOGGER.info("H2 database was shut down");
  }
}
