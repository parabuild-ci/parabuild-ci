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

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;

/**
 * Tests EmailNotificationManager
 */
public class SSTestEmailNotificationManager extends ServersideTestCase {

  private EmailNotificationManager notificationManager = null;
  private ErrorManager errorManager;


  public SSTestEmailNotificationManager(final String s) {
    super(s);
  }


  /**
   * Make sure doesn't throw any exception
   */
  public void test_notifyBuildFailedDoesNotFailOnUnsavedBuildRun() throws Exception {
    // get existing build run and simmulate it's not saved
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun buildRun = cm.getBuildRun(3);
    buildRun.setBuildRunID(BuildRun.UNSAVED_ID);

    // call, there should not be number of errors inclreased
    final int initialErrorCount = errorManager.errorCount();
    notificationManager.notifyBuildStepFailed(buildRun, new Exception("test"));
    assertEquals(initialErrorCount, errorManager.errorCount());
  }


  public void test_notifyChangeListsWaitingForMerge() {
    notificationManager.notifyChangeListsWaitingForMerge(0);
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
    errorManager = ErrorManagerFactory.getErrorManager();
    notificationManager = new EmailNotificationManager();
    notificationManager.enableNotification(false);
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestEmailNotificationManager.class);
  }
}
