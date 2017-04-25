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

import java.io.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.*;


public class SATestLogAnalizer extends TestCase {

  public void test_analizeAntBuildfileDoesntExist() throws Exception {
    final BuildSequence buildSequence = makeTestSequence("", "");
    final BuildLogAnalyzer logAnalyzer = new BuildLogAnalyzer(buildSequence);
    final File f = new File(TestHelper.getTestDataDir(), "test_ant_build_file_doesnt_exist.log");
    final BuildLogAnalyzer.Result result = logAnalyzer.analyze(f);
    assertEquals(BuildRun.BUILD_RESULT_BROKEN, result.getResult());
    assertTrue(!result.getLogWindowLines().isEmpty());
  }


  public void test_findsCustomSuccessPattern() throws Exception {
    final BuildSequence buildSequence = makeTestSequence("", "BUILD SUCCESSFUL");
    final BuildLogAnalyzer logAnalyzer = new BuildLogAnalyzer(buildSequence);
    final File f = new File(TestHelper.getTestDataDir(), "test_ant_successful_build.log");
    final BuildLogAnalyzer.Result result = logAnalyzer.analyze(f);
    assertEquals(BuildRun.BUILD_RESULT_SUCCESS, result.getResult());
    assertTrue(!result.getLogWindowLines().isEmpty());
  }


  public void test_findsCustomRegexSuccessPattern() throws Exception {
    final BuildSequence buildSequence = makeTestSequence("", "^Total time: 39 seconds$");
    final BuildLogAnalyzer logAnalyzer = new BuildLogAnalyzer(buildSequence);
    final File f = new File(TestHelper.getTestDataDir(), "test_ant_successful_build.log");
    final BuildLogAnalyzer.Result result = logAnalyzer.analyze(f);
    assertEquals(BuildRun.BUILD_RESULT_SUCCESS, result.getResult());
    assertTrue(!result.getLogWindowLines().isEmpty());
  }


  public void test_findsCustomFailurePattern() throws Exception {
    final BuildSequence buildSequence = makeTestSequence("BUILD FAILED\n\n\n", "BUILD SUCCESSFUL");
    final BuildLogAnalyzer logAnalyzer = new BuildLogAnalyzer(buildSequence);
    final File f = new File(TestHelper.getTestDataDir(), "test_ant_failed_build.log");
    final BuildLogAnalyzer.Result result = logAnalyzer.analyze(f);
    assertEquals(BuildRun.BUILD_RESULT_BROKEN, result.getResult());
    assertTrue(!result.getLogWindowLines().isEmpty());
  }


  public void test_findsCustomRegexFailurePattern() throws Exception {
    final BuildSequence buildSequence = makeTestSequence("^file:D:/viewtier/bt/build-tests.xml:6: Compile failed; see the compiler error output for details.$\n\n\n", "BUILD SUCCESSFUL");
    final BuildLogAnalyzer logAnalyzer = new BuildLogAnalyzer(buildSequence);
    final File f = new File(TestHelper.getTestDataDir(), "test_ant_failed_build.log");
    final BuildLogAnalyzer.Result result = logAnalyzer.analyze(f);
    assertEquals(BuildRun.BUILD_RESULT_BROKEN, result.getResult());
    assertTrue(!result.getLogWindowLines().isEmpty());
  }


  public void test_returnsCorrectInformationOnNoPatterns() throws Exception {
    final BuildSequence buildSequence = makeTestSequence("does_not_exist\n\n\n", "neither_does");
    final BuildLogAnalyzer logAnalizer = new BuildLogAnalyzer(buildSequence);
    final BuildLogAnalyzer.Result result = logAnalizer.analyze(new File(TestHelper.getTestDataDir(), "test_ant_failed_build.log"));
    assertEquals(BuildRun.BUILD_RESULT_BROKEN, result.getResult());
    assertTrue(!result.isPatternFound());
  }


  /**
   * Makes test sequence and fills it with error and success patterns
   */
  private static BuildSequence makeTestSequence(final String errorPatterns, final String successPatterns) {
    final BuildSequence buildSequence = new BuildSequence();
    buildSequence.setFailurePatterns(errorPatterns);
    buildSequence.setSuccessPatterns(successPatterns);
    return buildSequence;
  }


  protected void setUp() throws Exception {
  }


  public static TestSuite suite() {
    return new TestSuite(SATestLogAnalizer.class);
  }


  public SATestLogAnalizer(final String s) {
    super(s);
  }
}
