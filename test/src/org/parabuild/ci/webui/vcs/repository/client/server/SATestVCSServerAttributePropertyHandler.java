package org.parabuild.ci.webui.vcs.repository.client.server;

import junit.framework.TestCase;

/**
 * A tester for {@link VCSServerAttributePropertyHandler}.
 */
public final class SATestVCSServerAttributePropertyHandler extends TestCase {


  private static final String TEST_PROPERTY_NAME = "test.property.name";
  private static final String TEST_PROPERTY_VALUE = "Test property value";
  private VCSServerAttributePropertyHandler propertyHandler;
  private VCSServerAttributeVO propertyVO;


  /**
   * Tests {@link VCSServerAttributePropertyHandler#makeProperty(String)}.
   */
  public void testMakeProperty() {

    final VCSServerAttributeVO vcsServerAttributeVO = propertyHandler.makeProperty(TEST_PROPERTY_NAME);
    assertEquals(TEST_PROPERTY_NAME, vcsServerAttributeVO.getName());
  }


  /**
   * Tests {@link VCSServerAttributePropertyHandler#setPropertyValue(VCSServerAttributeVO, String)}.
   */
  public void testSetPropertyValue() {

    propertyHandler.setPropertyValue(propertyVO, TEST_PROPERTY_VALUE);
    assertEquals(TEST_PROPERTY_VALUE, propertyVO.getValue());
  }


  /**
   * Tests {@link VCSServerAttributePropertyHandler#getPropertyValue(VCSServerAttributeVO)}.
   */
  public void testGetPropertyValue() {
    propertyHandler.setPropertyValue(propertyVO, TEST_PROPERTY_VALUE);
    assertEquals(TEST_PROPERTY_VALUE, propertyHandler.getPropertyValue(propertyVO));

  }


  /**
   * Tests {@link VCSServerAttributePropertyHandler#getPropertyValue(VCSServerAttributeVO)}.
   */
  public void testGetPropertyName() {
    propertyVO.setName(TEST_PROPERTY_NAME);
    assertEquals(TEST_PROPERTY_NAME, propertyHandler.getPropertyName(propertyVO));
  }


  /**
   * Set up.
   */
  public void setUp() throws Exception {

    super.setUp();

    propertyHandler = new VCSServerAttributePropertyHandler();
    propertyVO = new VCSServerAttributeVO();
  }


  /**
   * Tear down
   */
  public void tearDown() throws Exception {

    propertyHandler = null;
    propertyVO = null;

    super.tearDown();
  }
}