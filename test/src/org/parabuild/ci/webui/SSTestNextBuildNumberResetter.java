/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.webui;

import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.util.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 * Tests NextBuildNumberResetter
 */
public class SSTestNextBuildNumberResetter extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestNextBuildNumberResetter.class);
  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private static final int TEST_BUILD_ID_WITH_NO_SEQUENCE_ATTRIBUTE = 2;
  private static final int TEST_BUILD_NUMBER_GAP = 10;

  private NextBuildNumberResetter resetter = null;


  public SSTestNextBuildNumberResetter(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_createValidatesGap() throws Exception {
    try {
      new NextBuildNumberResetter(TEST_BUILD_ID, 0);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
      IoUtils.ignoreExpectedException(e);
    }

    try {
      new NextBuildNumberResetter(TEST_BUILD_ID, 1);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  /**
   * Test validate
   */
  public void test_validate() throws Exception {
    final int intCurrent = getCurrentBuildNumber();

    int newNumber = intCurrent + TEST_BUILD_NUMBER_GAP - 1;
    assertValidateThrowsException(Integer.toString(newNumber));

    newNumber = intCurrent + TEST_BUILD_NUMBER_GAP;
    assertValidateThrowsException(Integer.toString(newNumber));

    assertValidateThrowsException("string instead of integer");
  }


  /**
   * Tests that correct message is provided.
   */
  public void test_validateProvidesCorrectMessage() throws Exception {
    final int newNumber = getCurrentBuildNumber() + TEST_BUILD_NUMBER_GAP - 1;
    try {
      resetter.validate(Integer.toString(newNumber));
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
      assertTrue(e.toString().indexOf("greater than 13") > 0);
    }
  }


  /**
   * Test reset
   */
  public void test_reset() throws Exception {
    // first changes
    int newNumber = getCurrentBuildNumber() + TEST_BUILD_NUMBER_GAP + 1;
    if (log.isDebugEnabled()) log.debug("newNumber = " + newNumber);
    resetter.reset(Integer.toString(newNumber));
    assertEquals(getCurrentBuildNumber(), newNumber - 1);
    // second change
    newNumber = getCurrentBuildNumber() + TEST_BUILD_NUMBER_GAP + 1;
    if (log.isDebugEnabled()) log.debug("newNumber = " + newNumber);
    resetter.reset(Integer.toString(newNumber));
    assertEquals(getCurrentBuildNumber(), newNumber - 1);
  }


  /**
   * Test reset
   */
  public void test_resetThrowsExceptionOnInvalidBuildID() throws Exception {
    final NextBuildNumberResetter invalidBuildIDResetter = new NextBuildNumberResetter(BuildConfig.UNSAVED_ID);
    invalidBuildIDResetter.validate("1000"); // should be OK for validation
    try {
      invalidBuildIDResetter.reset("1000");
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  /**
   * Tests that can reset a build number if attrbute is not
   * there.
   */
  public void test_bug554_canResetIfAttributeDoesnExist() throws ValidationException {
    final NextBuildNumberResetter resetter = new NextBuildNumberResetter(TEST_BUILD_ID_WITH_NO_SEQUENCE_ATTRIBUTE,
      TEST_BUILD_NUMBER_GAP);
    final int number = TEST_BUILD_NUMBER_GAP + 100;
    resetter.validate(Integer.toString(number));
    resetter.reset(Integer.toString(number));
  }


  /**
   * Helper
   */
  private void assertValidateThrowsException(final String newNumber) {
    try {
      resetter.validate(newNumber);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  /**
   * Helper
   */
  private int getCurrentBuildNumber() {
    final ActiveBuildAttribute current = resetter.currentNumber();
    final int intCurrent = current.getPropertyValueAsInteger();
    return intCurrent;
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestNextBuildNumberResetter.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    resetter = new NextBuildNumberResetter(TEST_BUILD_ID, TEST_BUILD_NUMBER_GAP);
  }
}
