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
package org.parabuild.ci.build.log;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AbstractCustomLogTest;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepRun;

import java.io.File;


/**
 * Tests TextFileLogHandler
 *
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public final class SSTestTextFileLogHandler extends AbstractCustomLogTest {

  private static final Log log = LogFactory.getLog(SSTestTextFileLogHandler.class);
  private TextFileLogHandler logHandler = null;
  private String testLogFileName = null;


  /**
   * @see AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.logHandler.process();
  }


  /**
   * @see org.parabuild.ci.build.AbstractResultHandlerTest#processResults
   */
  public void test_processLogsThatAreOld() {
    final StepRun stepRun = cm.getStepRun(cm.getStepLog(stepResultID()).getStepRunID());
    final int logCountBefore = cm.getAllStepLogs(stepRun.getID()).size();

    // NOTE: uses the fact that even the agent is remote, file is local
    final File file = new File(testLogFileName);
    file.setLastModified(cm.getBuildRun(stepRun.getBuildRunID()).getStartedAt().getTime() - 2000L);
    this.logHandler.process();

    assertEquals(logCountBefore, cm.getAllStepLogs(stepRun.getID()).size());
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 1; // single file log congig ID
  }


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_TEXT_FILE;
  }


  /**
   * Return a string to be found in search after calling
   * processLogs.
   *
   * @return
   * @see AbstractCustomLogTest - parent class that will call
   *      this method after calling processLogs().
   * @see #processLogs
   */
  protected String stringToBeFoundBySearch() {
    return this.getClass().getName();
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.logHandler = new TextFileLogHandler(super.agent, super.buildRunConfig,
            super.remoteCheckoutDir + '/' + super.relativeBuildDir,
            super.logConfig, TEST_STEP_RUN_ID);

    // create fully qualified test log file name
    testLogFileName = super.remoteBuildDirName
            + super.agent.getSystemProperty("file.separator")
            + logConfig.getPath().trim();

    // create test log file
    if (log.isDebugEnabled()) log.debug("testLogFileName: " + testLogFileName);
    super.agent.createFile(testLogFileName, "test log content: " + stringToBeFoundBySearch());
  }


  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestTextFileLogHandler.class,
            new String[]{
                    "test_process"
            });
  }


  public SSTestTextFileLogHandler(final String s) {
    super(s);
  }
}
