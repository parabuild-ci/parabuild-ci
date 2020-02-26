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
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.StepRun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Tests TextDirResultHandler
 *
 * @see org.parabuild.ci.build.AbstractResultHandlerTest
 */
public class SSTestDirResultHandler extends AbstractResultHandlerTest {

  private static final Log log = LogFactory.getLog(SSTestDirResultHandler.class);

  private static final int TEST_IN_DIR_RESULT_COUNT = 3;

  private DirResultHandler handler = null;
  private String testFileNameToSearch = null;
  private final List testFilePaths = new ArrayList(TEST_IN_DIR_RESULT_COUNT);


  /**
   */
  public void test_processResultsThatAreOld() throws IOException {
    final StepRun stepRun = cm.getStepRun(stepRunID());
    final int resultCountBefore = cm.getAllStepResults(stepRun).size();

    // NOTE: uses the fact that even the agent is remote, file is local
    for (int i = 0; i < testFilePaths.size(); i++) {
      final String testResultFileName = (String) testFilePaths.get(i);
      final File file = new File(testResultFileName);
      file.setLastModified(cm.getBuildRun(stepRun.getBuildRunID()).getStartedAt().getTime() - 2000L);
    }
    handler.process();

    assertEquals(resultCountBefore, cm.getAllStepResults(stepRun).size());
  }


  /**
   * @see AbstractResultHandlerTest#processResults
   */
  protected void processResults() {
    this.handler.process();
  }


  /**
   * @return result type handler being tested
   */
  protected byte resultTypeBeingTested() {
    return ResultConfig.RESULT_TYPE_DIR;
  }


  /**
   * Should return ID of result config to be used to configure
   * result handler.
   */
  protected int resultConfigID() {
    return 2; // directory with text files result congig ID
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
  protected String stringToBeFoundBySearch() {
    return testFileNameToSearch;
  }


  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.handler = new DirResultHandler(agent, buildRunConfig,
            remoteCheckoutDir + '/' + relativeBuildDir,
            resultConfig, stepRunID());

    // create test result file to simulate presence of the result

    // create dir
    final String remoteFileSeparator = agent.getSystemProperty("file.separator");
    final String testBuildResultDirName = remoteBuildDirName + remoteFileSeparator + resultConfig.getPath().trim();
    if (log.isDebugEnabled()) log.debug("testBuildResultDirName = " + testBuildResultDirName);
    agent.mkdirs(testBuildResultDirName);

    // create files in the dir
    testFileNameToSearch = null;
    for (int i = 0; i < TEST_IN_DIR_RESULT_COUNT; i++) {
      final String testResultFileToCreate = testBuildResultDirName + remoteFileSeparator + "in_dir_file_" + i + ".dat";
      agent.createFile(testResultFileToCreate, "test result content " + stringToBeFoundBySearch());
      testFilePaths.add(testResultFileToCreate);
      // init search file
      if (StringUtils.isBlank(testFileNameToSearch)) {
        testFileNameToSearch = agent.getFileName(testResultFileToCreate);
      }
    }

    // double-check that the field is there.
    assertTrue(!StringUtils.isBlank(testFileNameToSearch));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestDirResultHandler.class,
            new String[]{
                    "test_process"
            });
  }


  public SSTestDirResultHandler(final String s) {
    super(s);
  }
}
