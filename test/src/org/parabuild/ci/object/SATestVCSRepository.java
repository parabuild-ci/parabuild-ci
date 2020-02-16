package org.parabuild.ci.object;

import junit.framework.TestCase;

/**
 * A tester for {@link VCSRepository}.
 */
public final class SATestVCSRepository extends TestCase {

  private static final Integer ID = 777;
  private static final int TYPE = 888;
  private static final int SERVER_ID = 999;
  private static final String NAME = "Test name";
  private static final boolean DELETED = true;
  private static final int TIME_STAMP = 666;

  /**
   * Object under test.
   */
  private VCSRepository vcsRepository;


  /**
   * Tests {@link VCSRepository#setId(Integer)} and {@link VCSRepository#getId()}.
   */
  public void testSetGetId() {

    vcsRepository.setId(ID);
    assertEquals(ID, vcsRepository.getId());
  }


  /**
   * Tests {@link VCSRepository#setType(int)} and {@link VCSRepository#getType()}.
   */
  public void testSetGetType() {

    vcsRepository.setType(TYPE);
    assertEquals(TYPE, vcsRepository.getType());
  }


  /**
   * Tests {@link VCSRepository#setServerId(int)} and {@link VCSRepository#getServerId()} .
   */
  public void testSetGetServerId() {

    vcsRepository.setServerId(SERVER_ID);
    assertEquals(SERVER_ID, vcsRepository.getServerId());
  }


  /**
   * Tests {@link VCSRepository#setName(String)} and {@link VCSRepository#getName()}.
   */
  public void testTestSetGetName() {

    vcsRepository.setName(NAME);
    assertEquals(NAME, vcsRepository.getName());
  }


  /**
   * Tests {@link VCSRepository#setDeleted(boolean)} and {@link VCSRepository#isDeleted()}.
   */
  public void testSetGetDeleted() {

    assertFalse(vcsRepository.isDeleted());
    vcsRepository.setDeleted(DELETED);
    assertEquals(DELETED, vcsRepository.isDeleted());
  }


  /**
   * Tests {@link VCSRepository#setTimeStamp(long)} and {@link VCSRepository#getTimeStamp()}.
   */
  public void testSetGetTimeStamp() {

    vcsRepository.setTimeStamp(TIME_STAMP);
    assertEquals(TIME_STAMP, vcsRepository.getTimeStamp());
  }


  /**
   * Tests {@link VCSRepository#toString()}.
   */
  public void testToString() {

    assertNotNull(vcsRepository.toString());
  }


  public void setUp() throws Exception {

    super.setUp();
    vcsRepository = new VCSRepository();
  }


  public void tearDown() throws Exception {

    vcsRepository = null;
    super.tearDown();
  }
}