package org.parabuild.ci.webui.agent.status;

import junit.framework.TestCase;

public final class SATestImmutableImage extends TestCase {

  /**
   * Constructs a test case with the given name.
   */
  public SATestImmutableImage(final String name) {
    super(name);
  }


  public void testToString() {
    assertNotNull(ImmutableImage.ZERO_SIZE_IMAGE.toString());
  }
}