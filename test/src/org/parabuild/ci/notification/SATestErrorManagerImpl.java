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
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.impl.*;

/**
 * Intentionally tests ErrorManagerImpl outside of server
 * context.
 */
public class SATestErrorManagerImpl extends TestCase {

  private static final Log log = LogFactory.getLog(SATestErrorManagerImpl.class);
  private ErrorManagerImpl errorManager = null;


  public SATestErrorManagerImpl(final String s) {
    super(s);
  }


  public void test_reportAndClearAllActiveErrors() throws Exception {
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
    final Error error = new Error(description);
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
    return new OrderedTestSuite(SATestErrorManagerImpl.class, new String[]{
      "test_reportAndClearAllActiveErrors"
    });
  }
}
