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
package org.parabuild.ci.notification;

import java.util.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.impl.*;

/**
 * Tests home page
 */
public class SSTestErrorManagerImpl extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestErrorManagerImpl.class);
  private ErrorManagerImpl errorManager = null;
  private static final String TEST_ERROR_MESSAGE_1 = "test error message";
  private static final String TEST_ERROR_MESSAGE_2 = "other test error message";


  public SSTestErrorManagerImpl(final String s) {
    super(s);
  }


  /**
   * Tests error retention
   */
  public void test_errorRetention() throws Exception {
    final Error error1 = makeTestError(TEST_ERROR_MESSAGE_1);
    errorManager.reportSystemError(error1);
    assertEquals(1, errorManager.errorCount());

    // report the same error again and make sure counter has not changed
    final Error error2 = makeTestError(TEST_ERROR_MESSAGE_1);
    errorManager.reportSystemError(error2);
    assertEquals(1, errorManager.errorCount());

    // report other error and make sure counter has changed
    final Error otherError = makeTestError(TEST_ERROR_MESSAGE_2);
    errorManager.reportSystemError(otherError);
    assertEquals(2, errorManager.errorCount());
  }


  public void test_guessBuildName() throws Exception {
    final Error error = makeTestError("test");
    error.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertTrue(StringUtils.isBlank(error.getBuildName()));

    // check if EM set build name
    errorManager.reportSystemError(error);
    assertTrue(!StringUtils.isBlank(error.getBuildName()));
  }


  public void test_clearAllActiveErrors() throws Exception {
    final Error error = makeTestError("test");
    errorManager.reportSystemError(error);
    assertTrue(errorManager.errorCount() > 0);
    errorManager.clearAllActiveErrors();
    assertEquals(0, errorManager.errorCount());
  }


  public void test_getActiveErrorIDs() throws Exception {
    final Error error = makeTestError("test");
    errorManager.reportSystemError(error);
    final List activeErrorIDs = errorManager.getActiveErrorIDs(1000);
    assertEquals(1, activeErrorIDs.size());
  }


  public void test_clearActiveError() throws Exception {
    final Error error = makeTestError("test");
    errorManager.reportSystemError(error);
    final List activeErrorIDs = errorManager.getActiveErrorIDs(1000);
    final String errorID = (String)activeErrorIDs.get(0);
    if (log.isDebugEnabled()) log.debug("errorID = " + errorID);
    errorManager.clearActiveError(errorID);
    assertEquals(0, errorManager.errorCount());
    assertEquals(0, errorManager.getActiveErrorIDs(1000).size());
  }


  private static Error makeTestError(final String description) {
    final Error error = new org.parabuild.ci.error.Error(description);
    error.setSendEmail(false);
    return error;
  }


  protected void setUp() throws Exception {
    super.setUp();
    errorManager = new ErrorManagerImpl();
    errorManager.clearAllActiveErrors();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestErrorManagerImpl.class, new String[]{
      "test_errorRetention",
      "test_guessBuildName",
      "test_clearAllActiveErrors",
      "test_getActiveErrorIDs",
      "test_clearActiveError"
    });
  }
}
