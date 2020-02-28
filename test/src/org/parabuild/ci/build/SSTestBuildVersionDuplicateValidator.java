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
package org.parabuild.ci.build;

import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests BuildVersionDuplicateValidator
 */
public class SSTestBuildVersionDuplicateValidator extends ServersideTestCase {

  private static final String TEST_TEMPLATE = "2.0.${version.counter}.${build.number}";
  private static final String TEST_BUILD_NAME = "test_build";
  private static final int TEST_BUILD_COUNTER = 999;

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestBuildVersionDuplicateValidator.class);
  private BuildVersionDuplicateValidator validator;


  public void test_validate() throws Exception {
    // try nothing there
    validator.validate(TestHelper.TEST_CVS_VALID_BUILD_ID, TEST_TEMPLATE, TEST_BUILD_NAME, TEST_BUILD_COUNTER);
  }


  public void test_validateDuplicate() throws Exception {
    // try with a duplicate
    ConfigurationManager.getInstance().saveObject(new BuildRunAttribute(3, BuildRunAttribute.VERSION, "2.0.999.4"));
    try {
      validator.validate(TestHelper.TEST_CVS_VALID_BUILD_ID, TEST_TEMPLATE, TEST_BUILD_NAME, TEST_BUILD_COUNTER);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      // expected
    }
  }


  /**
   * Default increment mode is automatic as set in dataset.xml
   *
   * @throws Exception
   */
  public void test_validateAutomatic() throws Exception {

    // try with a duplicate with no counter in the DB
    final String duplicateVersion = "2.0.0.4";
    ConfigurationManager.getInstance().saveObject(new BuildRunAttribute(3, BuildRunAttribute.VERSION, duplicateVersion));
    try {
      // build counter is not set, in auto mode should get incremented automatically to 1 so that the would-be generated version would be "2.0.0.4"
      validator.validate(TestHelper.TEST_CVS_VALID_BUILD_ID, TEST_TEMPLATE, TEST_BUILD_NAME, -1);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      // expected
    }
  }


  /**
   *
   * @throws Exception
   */
  public void test_validateAutomaticWithCounterInDB() throws Exception {

    // try with a duplicate with no counter in the DB
    final String duplicateVersion = "2.0.1.4";
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    cm.saveObject(new BuildRunAttribute(3, BuildRunAttribute.VERSION, duplicateVersion));
    cm.saveObject(new ActiveBuildAttribute(1, ActiveBuildAttribute.VERSION_COUNTER_SEQUENCE, 0));
    try {
      // build counter is not set, in auto mode should get incremented automatically to 1 so that the would-be generated version would be "2.0.0.4"
      validator.validate(TestHelper.TEST_CVS_VALID_BUILD_ID, TEST_TEMPLATE, TEST_BUILD_NAME, -1);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      // expected
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildVersionDuplicateValidator.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    validator = new BuildVersionDuplicateValidator();
  }


  public SSTestBuildVersionDuplicateValidator(final String s) {
    super(s);
  }
}
