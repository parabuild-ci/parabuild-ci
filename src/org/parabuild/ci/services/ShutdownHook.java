package org.parabuild.ci.services;

import org.parabuild.ci.Version;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import static org.parabuild.ci.common.IoUtils.printToStdout;

/**
 * A hook to run on JVM shutdown.
 *
 * @see ServiceManagerServlet#init(ServletConfig)
 */
public final class ShutdownHook implements Runnable {

  /**
   * Pre-read version.
   */
  private final String versionAsString;

  /**
   * The servlet to shutdown.
   */
  private final Servlet servlet;


  /**
   * Creates a new instance of {@link ShutdownHook}.
   *
   * @param servlet the servlet to destroy.
   */
  ShutdownHook(final Servlet servlet) {
    this.versionAsString = Version.versionToString(true);
    this.servlet = servlet;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    final String shuttingDownMessage = "Shutting down " + versionAsString;
    printToStdout(shuttingDownMessage);
    servlet.destroy();
  }
}
