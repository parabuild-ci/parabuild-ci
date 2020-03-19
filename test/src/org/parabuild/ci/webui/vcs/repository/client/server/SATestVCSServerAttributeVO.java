package org.parabuild.ci.webui.vcs.repository.client.server;

import junit.framework.TestCase;

/**
 * A tester for {@link VCSServerAttributeVO}.
 */
public final class SATestVCSServerAttributeVO extends TestCase {

  private static final String TEST_VALUE = "Test value";

  private static final String TEST_NAME = "Test name";

  private static final Integer TEST_SERVER_ID = 222;

  private static final long TEST_TIMESTAMP = 333L;

  private static final Integer TEST_ID = 111;

  /**
   * Object under test.
   */
  private VCSServerAttributeVO vcsServerAttributeVO;


  /**
   * Tests {@link VCSServerAttributeVO#getServerId()}.
   */
  public void testGetServerId() {

    vcsServerAttributeVO.setServerId(TEST_SERVER_ID);
    assertEquals(TEST_SERVER_ID, vcsServerAttributeVO.getServerId());
  }


  /**
   * Tests {@link VCSServerAttributeVO#setServerId(Integer)}.
   */
  public void testSetServerId() {

    vcsServerAttributeVO.setServerId(TEST_SERVER_ID);
    assertEquals(TEST_SERVER_ID, vcsServerAttributeVO.getServerId());
  }


  /**
   * Tests {@link VCSServerAttributeVO#getId()}.
   */
  public void testGetId() {

    vcsServerAttributeVO.setId(TEST_ID);
    assertEquals(TEST_ID, vcsServerAttributeVO.getId());
  }


  /**
   * Tests {@link VCSServerAttributeVO#setId(Integer)}.
   */
  public void testSetId() {

    vcsServerAttributeVO.setId(TEST_ID);
    assertEquals(TEST_ID, vcsServerAttributeVO.getId());
  }


  /**
   * Tests {@link VCSServerAttributeVO#getName()}.
   */
  public void testTestGetName() {

    vcsServerAttributeVO.setName(TEST_NAME);
    assertEquals(TEST_NAME, vcsServerAttributeVO.getName());
  }


  /**
   * Tests {@link VCSServerAttributeVO#setName(String)}.
   */
  public void testTestSetName() {

    vcsServerAttributeVO.setName(TEST_NAME);
    assertEquals(TEST_NAME, vcsServerAttributeVO.getName());
  }


  /**
   * Tests {@link VCSServerAttributeVO#getValue()}.
   */
  public void testGetValue() {

    vcsServerAttributeVO.setValue(TEST_VALUE);
    assertEquals(TEST_VALUE, vcsServerAttributeVO.getValue());
  }


  /**
   * Tests {@link VCSServerAttributeVO#setValue(String)}.
   */
  public void testSetValue() {

    vcsServerAttributeVO.setValue(TEST_VALUE);
    assertEquals(TEST_VALUE, vcsServerAttributeVO.getValue());
  }


  /**
   * Tests {@link VCSServerAttributeVO#getTimeStamp()}.
   */
  public void testGetTimeStamp() {

    vcsServerAttributeVO.setTimeStamp(TEST_TIMESTAMP);
    assertEquals(TEST_VALUE, vcsServerAttributeVO.getValue());
  }


  /**
   * Tests {@link VCSServerAttributeVO#setTimeStamp(long)}.
   */
  public void testSetTimeStamp() {

    vcsServerAttributeVO.setTimeStamp(TEST_TIMESTAMP);
    assertEquals(TEST_VALUE, vcsServerAttributeVO.getValue());

  }


  /**
   * Tests {@link VCSServerAttributeVO#toString()}.
   */
  public void testTestToString() {

    assertNotNull(vcsServerAttributeVO.toString());
  }


  /**
   * Set up the tester.
   */
  public void setUp() throws Exception {

    super.setUp();

    vcsServerAttributeVO = new VCSServerAttributeVO();
  }


  /**
   * Tear down the tester.
   */
  public void tearDown() throws Exception {

    vcsServerAttributeVO = null;

    super.tearDown();
  }
}