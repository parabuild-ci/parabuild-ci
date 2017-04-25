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
package org.parabuild.ci.relnotes;

import java.util.*;
import junit.framework.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

/**
 * Tests home page
 */
public class SSTestReleaseNotesHandlerFactory extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestReleaseNotesHandlerFactory.class);
  private ReleaseNotesHandler handler;


  public SSTestReleaseNotesHandlerFactory(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_mandatoryIssueHandlersArePresent() throws Exception {

    boolean pendingIssueHandlerFound = false;
    boolean prevBuildIssueHandlerFound = false;

    // make sure pending issue and prev build handler is there.
    final CompositeReleaseNotesHandler compositeHandler = (CompositeReleaseNotesHandler)handler;
    final Collection handlers = (Collection)junitx.util.PrivateAccessor.getField(compositeHandler, "handlers");
    for (Iterator iter = handlers.iterator(); iter.hasNext();) {
      final ReleaseNotesHandler currentHandler = (ReleaseNotesHandler)iter.next();
      if (currentHandler instanceof PendingReleaseNotesHandler) {
        pendingIssueHandlerFound = true;
      } else if (currentHandler instanceof PreviousBuildRunReleaseNotesHandler) {
        prevBuildIssueHandlerFound = true;
      }
    }
    if (!pendingIssueHandlerFound) fail("PendingIssueHanler was not found in a list of issue handlers in CompositeIssueHandler");
    if (!prevBuildIssueHandlerFound) fail("PreviousBuildRunReleaseNotesHandler was not found in a list of issue handlers in CompositeIssueHandler");
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestReleaseNotesHandlerFactory.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    handler = ReleaseNotesHandlerFactory.getHandler(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertTrue(handler instanceof CompositeReleaseNotesHandler);
  }
}
