package org.parabuild.ci.services;

import junit.framework.TestCase;

import static java.util.Collections.singletonList;

/**
 * A tester for {@link BuildStartRequestParameter}.
 */
public final class SABuildStartRequestParameter extends TestCase {


  private static final String TEST_VARIABLE_VALUE = "test.variable.value";
  private static final String TEST_VARIABLE_NAME = "test.variable.name";
  private static final String TEST_DESCRIPTION = "test.description";
  private static final int TEST_ORDER = 1;

  private BuildStartRequestParameter buildStartRequestParameter;


  /**
   * Constructs a test case with the given name.
   *
   * @param name the fixture name.
   */
  public SABuildStartRequestParameter(final String name) {
    super(name);
  }


  public void testGetName() {

    assertEquals(TEST_VARIABLE_NAME, buildStartRequestParameter.getName());
  }


  public void testGetValues() {

    assertEquals(singletonList(TEST_VARIABLE_VALUE), buildStartRequestParameter.getValues());
  }


  public void testGetDescription() {
    assertEquals(TEST_DESCRIPTION, buildStartRequestParameter.getDescription());
  }


  public void testGetOrder() {
    assertEquals(TEST_ORDER, buildStartRequestParameter.getOrder());
  }


  public void testToString() {
    assertNotNull(buildStartRequestParameter.toString());
  }


  public void setUp() throws Exception {

    super.setUp();

    buildStartRequestParameter = new BuildStartRequestParameter(TEST_VARIABLE_NAME, TEST_DESCRIPTION,
            TEST_VARIABLE_VALUE, TEST_ORDER);
  }


  public void tearDown() throws Exception {

    buildStartRequestParameter = null;

    super.tearDown();
  }
}