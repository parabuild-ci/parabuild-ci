package org.parabuild.ci.webui.agent.status;

import junit.framework.TestCase;

public final class SATestImmutableImage extends TestCase {

  public void testToString() {
    assertNotNull(ImmutableImage.ZERO_SIZE_IMAGE.toString());
  }
}