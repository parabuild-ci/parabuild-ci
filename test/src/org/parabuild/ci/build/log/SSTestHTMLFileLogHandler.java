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

import junit.framework.*;
import org.apache.commons.logging.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.build.AbstractCustomLogTest;


/**
 * Tests TextFileLogHandler
 *
 * @see org.parabuild.ci.build.AbstractCustomLogTest
 */
public class SSTestHTMLFileLogHandler extends AbstractCustomLogTest {

  private static final Log log = LogFactory.getLog(SSTestHTMLFileLogHandler.class);
  private HTMLFileLogHandler logHandler = null;


  /**
   * @see AbstractCustomLogTest#processLogs
   */
  protected void processLogs() {
    this.logHandler.process();
  }


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected int logConfigID() {
    return 5; // single file log congig ID
  }


  /**
   * @return log type handler being tested
   */
  protected byte logTypeBeingTested() {
    return LogConfig.LOG_TYPE_HTML_FILE;
  }


  /**
   * Return a string to be found in search after calling
   * processLogs.
   *
   * @return
   *
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
    this.logHandler = new HTMLFileLogHandler(super.agent, super.buildRunConfig,
      super.remoteCheckoutDir + '/' + super.relativeBuildDir,
      super.logConfig, TEST_STEP_RUN_ID);

    // create fully qualified test log file name
    final String testLogFileName = super.remoteBuildDirName
      + super.agent.getSystemProperty("file.separator")
      + logConfig.getPath().trim();
    if (log.isDebugEnabled()) log.debug("testLogFileName = " + testLogFileName);

    // create test log file
    super.agent.createFile(testLogFileName, "<p>" + stringToBeFoundBySearch() + "</p>");

    // enable error manager traces
    System.setProperty("parabuild.print.stacktrace", "true");
  }


  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestHTMLFileLogHandler.class,
      new String[]{
        "test_process"
      });
  }


  public SSTestHTMLFileLogHandler(final String s) {
    super(s);
  }
}
