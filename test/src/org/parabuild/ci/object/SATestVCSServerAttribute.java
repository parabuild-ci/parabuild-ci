package org.parabuild.ci.object;

import junit.framework.TestCase;

/**
 * A tester for {@link VCSServerAttribute}.
 */
public final class SATestVCSServerAttribute extends TestCase {

  private static final Integer ID = 777;
  private static final int TYPE = 888;
  private static final int SERVER_ID = 999;
  private static final String NAME = "Test name";
  private static final boolean DELETED = true;
  private static final int TIME_STAMP = 666;

  /**
   * Object under test.
   */
  private VCSServerAttribute vcsServerAttribute;


  /**
   * Tests {@link VCSServerAttribute#setServerId(int)} and {@link VCSServerAttribute#getServerId()} .
   */
  public void testSetGetServerId() {

    vcsServerAttribute.setServerId(SERVER_ID);
    assertEquals(SERVER_ID, vcsServerAttribute.getServerId());
  }


  /**
   * Tests {@link VCSServerAttribute#setName(String)} and {@link VCSServerAttribute#getName()}.
   */
  public void testTestSetGetName() {

    vcsServerAttribute.setName(NAME);
    assertEquals(NAME, vcsServerAttribute.getName());
  }


  /**
   * Tests {@link VCSServerAttribute#setTimeStamp(long)} and {@link VCSServerAttribute#getTimeStamp()}.
   */
  public void testSetGetTimeStamp() {

    vcsServerAttribute.setTimeStamp(TIME_STAMP);
    assertEquals(TIME_STAMP, vcsServerAttribute.getTimeStamp());
  }


  /**
   * Tests {@link VCSServerAttribute#toString()}.
   */
  public void testToString() {

    assertNotNull(vcsServerAttribute.toString());
  }


  public void setUp() throws Exception {

    super.setUp();
    vcsServerAttribute = new VCSServerAttribute();
  }


  public void tearDown() throws Exception {

    vcsServerAttribute = null;
    super.tearDown();
  }
}