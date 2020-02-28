package org.parabuild.ci.webui.vcs.repository.server;

import junit.framework.TestCase;

/**
 * A tester for {@link NotAuthenticatedException}.
 */
public final class SATestNotAuthenticatedException extends TestCase {

  /**
   * Object under test
   */
  private NotAuthenticatedException exception;


  public void testGetMessage() {

    assertNotNull(exception.getMessage());
  }


  public void setUp() throws Exception {

    super.setUp();
    exception = new NotAuthenticatedException();
  }


  public void tearDown() throws Exception {

    exception = null;
    super.tearDown();
  }
}