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
package org.parabuild.ci.build.result;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AbstractResultHandlerTest;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.StepRun;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Tests SingleFileResultHandler
 *
 * @noinspection InstanceMethodNamingConvention, ResultOfMethodCallIgnored
 * @see org.parabuild.ci.build.AbstractResultHandlerTest
 */
public class SSTestFileListResultHandler extends AbstractResultHandlerTest {

  /**
   * @noinspection UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SSTestFileListResultHandler.class); // NOPMD
  private FileListResultHandler resultHandler = null;
  private String[] testResultFileNames = null;


  /**
   * @see AbstractResultHandlerTest#processResults
   */
  protected void processResults() throws IOException {
    this.resultHandler.process();
  }


  /**
   * @see AbstractResultHandlerTest#processResults
   */
  public void test_processResultsThatAreOld() throws IOException {
    final StepRun stepRun = cm.getStepRun(stepRunID());
    final int resultCountBefore = cm.getAllStepResults(stepRun).size();

    // NOTE: uses the fact that even the agent is remote, file is local
    for (int i = 0; i < testResultFileNames.length; i++) {
      final String testResultFileName = testResultFileNames[i];
      final File file = new File(testResultFileName);
      file.setLastModified(cm.getBuildRun(stepRun.getBuildRunID()).getStartedAt().getTime() - 2000L);
    }
    this.resultHandler.process();

    assertEquals(resultCountBefore, cm.getAllStepResults(stepRun).size());
  }


  /**
   * Should return ID of result config to be used to configure
   * result handler.
   */
  protected int resultConfigID() {
    return 1; // single file result congig ID
  }


  /**
   * @return result type handler being tested
   */
  protected byte resultTypeBeingTested() {
    return ResultConfig.RESULT_TYPE_FILE_LIST;
  }


  /**
   * Return a string to be found in search after calling
   * processResults.
   *
   * @return
   * @see AbstractResultHandlerTest - parent class that will call
   *      this method after calling processResults().
   * @see #processResults
   */
  protected String stringToBeFoundBySearch() throws IOException, AgentFailureException {
    return agent.getFileName(testResultFileNames[0]);
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();

    // Create handler
    this.resultHandler = new FileListResultHandler(super.agent, super.buildRunConfig,
            super.remoteCheckoutDir + '/' + super.relativeBuildDir, super.resultConfig, stepRunID());

    // Create test result files
    final List list = StringUtils.multilineStringToList(resultConfig.getPath());
    testResultFileNames = new String[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final String path = ((String) list.get(i)).trim();
      testResultFileNames[i] = super.remoteBuildDirName + super.agent.getSystemProperty("file.separator") + path;
      super.agent.createFile(testResultFileNames[i], "test result file content " + "build result file");
    }
  }


  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestFileListResultHandler.class,
            new String[]{
                    "test_process"
            });
  }


  public SSTestFileListResultHandler(final String s) {
    super(s);
  }


  public String toString() {
    return "SSTestSingleFileListResultHandler{" +
            "resultHandler=" + resultHandler +
            "} " + super.toString();
  }
}
