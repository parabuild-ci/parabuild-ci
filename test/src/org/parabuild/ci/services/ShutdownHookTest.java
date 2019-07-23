package org.parabuild.ci.services;

import junit.framework.TestCase;

import javax.servlet.Servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * A tester for {@link ShutdownHook}.
 */
public final class ShutdownHookTest extends TestCase {


  /**
   * Object under tests.
   */
  private ShutdownHook shutdownHook;

  /**
   * Servlet to shutdown.
   */
  private Servlet servletMock;


  /**
   * A tester for {@link ShutdownHook#run()}.
   */
  public void testRun() {

    shutdownHook.run();

    verify(servletMock).destroy();
  }


  public void setUp() throws Exception {

    super.setUp();

    servletMock = mock(Servlet.class);

    shutdownHook = new ShutdownHook(servletMock);
  }


  public void tearDown() throws Exception {
    super.tearDown();
    shutdownHook = null;
  }
}