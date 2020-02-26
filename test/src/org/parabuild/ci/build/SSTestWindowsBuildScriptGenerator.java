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

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests WindowsBuildScriptGenerator
 */
public class SSTestWindowsBuildScriptGenerator extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestWindowsBuildScriptGenerator.class);

  private WindowsBuildScriptGenerator scriptGenerator = null;
  private BuildSequence sequence = null;
  private Agent agent = null;


  public void test_generateScript() throws Exception {
    if (!agent.isWindows()) return; // dont run if not winwdows
    final String scriptFileName = scriptGenerator.generateScriptFile(sequence);
    assertTrue(new File(scriptFileName).length() > 0);
  }


  /**
   * Validates that PARABUILD_CHANGE_LIST_NUMBER is in the
   * script;
   */
  public void test_bug283_generateScript() throws Exception {
    // create script
    final String scriptFileName = generateTestScript();

    // check if export string is there
    final boolean found = TestHelper.findStringInTextFile(scriptFileName, "set " + BuildScriptGenerator.VAR_PARABUILD_CHANGE_LIST_NUMBER);
    assertTrue("set PARABUILD_CHANGE_LIST_NUMBER not found", found);
  }


  /**
   * Validates that paths don't contain slashes (windows should
   * have backslashes.
   */
  public void test_bug627_generateScript() throws Exception {
    // create script
    final String scriptFileName = generateTestScript();

    // does NOT contian wrong path
    assertTrue("Should not contain slashes",
            !TestHelper.findStringInTextFile(scriptFileName, "/sourceline/alwaysvalid"));

    // does contian right path
    assertTrue("Should contain slashes",
            TestHelper.findStringInTextFile(scriptFileName, "\\sourceline\\alwaysvalid"));
  }


  public void test_addVariables() throws BuildException, IOException, AgentFailureException {
    // test names and values
    final String testName1 = "PARABUILD_ADDITIONAL_VAR_NAME1";
    final String testValue1 = "PARABUILD_ADDITIONAL_VAR_VALUE1";
    final String testName2 = "PARABUILD_ADDITIONAL_VAR_NAME2";
    final String testValue2 = "PARABUILD_ADDITIONAL_VAR_VALUE2";

    // add
    final Map testVars = new HashMap(3);
    testVars.put(testName1, testValue1);
    testVars.put(testName2, testValue2);
    scriptGenerator.addVariables(testVars);

    // generate
    final String scriptFileName = generateTestScript();

    // check if name and value is there
    assertNameValuePairIsSet(scriptFileName, testName1, testValue1);
    assertNameValuePairIsSet(scriptFileName, testName2, testValue2);
  }


  public void test_addVariable() throws BuildException, IOException, AgentFailureException {
    // test names and values
    final String testName1 = "PARABUILD_ADDITIONAL_VAR_NAME1";
    final String testValue1 = "PARABUILD_ADDITIONAL_VAR_VALUE1";
    final String testName2 = "PARABUILD_ADDITIONAL_VAR_NAME2";
    final String testValue2 = "PARABUILD_ADDITIONAL_VAR_VALUE2";

    // add
    scriptGenerator.addVariable(testName1, testValue1);
    scriptGenerator.addVariable(testName2, testValue2);

    // generate
    final String scriptFileName = generateTestScript();

    // check if name and value is there
    assertNameValuePairIsSet(scriptFileName, testName1, testValue1);
    assertNameValuePairIsSet(scriptFileName, testName2, testValue2);
  }


  public void test_buildTimestampDatePresent() throws BuildException, IOException, AgentFailureException {
    final String scriptFileName = generateTestScript();
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_BUILD_TIMESTAMP, TestHelper.findStringInTextFile(scriptFileName, "set " + BuildScriptGenerator.VAR_PARABUILD_BUILD_TIMESTAMP));
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_BUILD_DATE, TestHelper.findStringInTextFile(scriptFileName, "set " + BuildScriptGenerator.VAR_PARABUILD_BUILD_DATE));
  }


  public void test_previousChangeListNumberPresent() throws BuildException, IOException, AgentFailureException {
    final String scriptFileName = generateTestScript();
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_PREVIOUS_CHANGE_LIST_DATETIME, TestHelper.findStringInTextFile(scriptFileName, "set " + BuildScriptGenerator.VAR_PARABUILD_PREVIOUS_CHANGE_LIST_DATETIME));
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_PREVIOUS_CHANGE_LIST_NUMBER, TestHelper.findStringInTextFile(scriptFileName, "set " + BuildScriptGenerator.VAR_PARABUILD_PREVIOUS_CHANGE_LIST_NUMBER));
  }


  public void test_buildStartedByUserPresent() throws BuildException, IOException, AgentFailureException {
    final String scriptFileName = generateTestScript();
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_BUILD_STARTED_BY_USER,
            TestHelper.findStringInTextFile(scriptFileName,
                    "set " + BuildScriptGenerator.VAR_PARABUILD_BUILD_STARTED_BY_USER));
  }


  public void test_bug720NoDuplicatesInTheEnvironment() throws IOException, BuildException, CommandStoppedException, AgentFailureException {
    if (!agent.isWindows()) return; // windows only
    BufferedReader br = null;
    try {
      // get sequence
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final List sequences = cm.getAllBuildSequences(TestHelper.TEST_CVS_VALID_BUILD_ID, BuildStepType.BUILD);
      final BuildSequence bs = (BuildSequence) sequences.get(0);
      assertNotNull(bs);
      bs.setScriptText("set Path=%Path%;signature_path;\nset\n");
      cm.saveObject(bs); // just in case it is retrieved some where

      // generate and execute
      final File merged = TestHelper.getTestFile(this.getClass().getName() + "-merged");
      final String scriptFileName = scriptGenerator.generateScriptFile(bs);
      final WindowsBuildScriptRunner scriptRunner = new WindowsBuildScriptRunner(agent);
      scriptRunner.setMergedFile(merged);
      scriptRunner.addTimeoutMatch(scriptFileName);
      final BuildTimeoutCallback timeoutCallback = new BuildTimeoutCallback();
      timeoutCallback.setScriptRunner(scriptRunner);
      timeoutCallback.setNotificationEnabled(false);
      scriptRunner.setTimeoutCallback(timeoutCallback);
      scriptRunner.executeBuildScript(scriptFileName);

//      if (log.isDebugEnabled()) log.debug("IoUtils.fileToString(merged): " + IoUtils.fileToString(merged));

      // validate no dupes
      final Map scriptEnv = new HashMap(23);
      String inputLine = null;
      br = new BufferedReader(new FileReader(merged));
      while ((inputLine = br.readLine()) != null) {
        //if (log.isDebugEnabled()) log.debug("inputLine: " + inputLine);
        String varName = null, varValue = null;
        //if (log.isDebugEnabled()) log.debug("inputLine: " + inputLine);
        final int equalsCharIndex = inputLine.indexOf('=');
        if (equalsCharIndex == -1) continue;
        varName = inputLine.substring(0, equalsCharIndex);
        if (equalsCharIndex + 1 > inputLine.length()) {
          varValue = "";
        } else {
          varValue = inputLine.substring(equalsCharIndex + 1);
        }
        assertTrue("Variable " + varName + " should not be found", scriptEnv.get(varName) == null);
        scriptEnv.put(varName, varValue);
      }

      // validate no excluded are present
      final String[] varsToErase = AbstractBuildScriptGenerator.getVarsToErase();
      for (int i = 0; i < varsToErase.length; i++) {
        final String varNameToErase = varsToErase[i];
        assertTrue("Variable " + varNameToErase + " should not be found", scriptEnv.get(varNameToErase) == null);
      }

    } finally {
      IoUtils.closeHard(br);
    }
  }


  public void test_generatesOptionalVersion() throws BuildException, IOException, AgentFailureException {
    this.scriptGenerator.setVersion("TEST_VERSION");
    this.scriptGenerator.setVersionCounter(9999);
    final String scriptFileName = generateTestScript();
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_VERSION, TestHelper.findStringInTextFile(scriptFileName, "set " + BuildScriptGenerator.VAR_PARABUILD_VERSION));
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_VERSION_COUNTER, TestHelper.findStringInTextFile(scriptFileName, "set " + BuildScriptGenerator.VAR_PARABUILD_VERSION_COUNTER));
  }


  public void test_generatesCleanCheckout() throws BuildException, IOException, AgentFailureException {
    this.scriptGenerator.setCleanCheckout(true);
    final String scriptFileName = generateTestScript();
    assertNameValuePairIsSet(scriptFileName, BuildScriptGenerator.VAR_PARABUILD_CLEAN_CHECKOUT, "true");
  }


  public void test_doesNotGenerateCleanCheckout() throws BuildException, IOException, AgentFailureException {
    this.scriptGenerator.setCleanCheckout(false);
    final String scriptFileName = generateTestScript();
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_CLEAN_CHECKOUT + " should not be present", !TestHelper.findStringInTextFile(scriptFileName, "set " + BuildScriptGenerator.VAR_PARABUILD_CLEAN_CHECKOUT));
  }


  private void assertNameValuePairIsSet(final String scriptFileName, final String name, final String value) throws IOException {
    assertTrue(TestHelper.findStringInTextFile(scriptFileName, "set " + name + '=' + value));
  }


  /**
   * Helper.
   *
   * @return script file name.
   * @throws IOException
   */
  private String generateTestScript() throws IOException, AgentFailureException {
    // get sequence
    final List sequences = ConfigurationManager.getInstance().getAllBuildSequences(TestHelper.TEST_CVS_VALID_BUILD_ID, BuildStepType.BUILD);
    final BuildSequence bs = (BuildSequence) sequences.get(0);
    assertNotNull(bs);
    // generate
    return scriptGenerator.generateScriptFile(bs);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestWindowsBuildScriptGenerator.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    // get test config
    final ConfigurationManager configManager = ConfigurationManager.getInstance();
    final BuildConfig buildConfig = configManager.getBuildConfiguration(TestHelper.TEST_CVS_VALID_BUILD_ID);

    // get test sequence
    final List sequences = configManager.getAllBuildSequences(buildConfig.getBuildID(), BuildStepType.BUILD);
    assertTrue(!sequences.isEmpty());
    sequence = (BuildSequence) sequences.get(0);

    // create agent
    agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CVS_VALID_BUILD_ID);

    // create SC
    final SourceControl sourceControl = VersionControlFactory.makeVersionControl(buildConfig);
    sourceControl.setAgentHost(agent.getHost());

    // create generator
    scriptGenerator = new WindowsBuildScriptGenerator(agent);
    scriptGenerator.setRelativeBuildDir(sourceControl.getRelativeBuildDir());
    scriptGenerator.setBuildNumber(0);
    scriptGenerator.setBuildName("TEST_BUILD_NAME");
    scriptGenerator.setStepName("TEST_STEP_NAME");
    scriptGenerator.setChangeListNumber("1");
    scriptGenerator.setChangeListDate(new Date());
    scriptGenerator.setBuildRunID(1);
    scriptGenerator.setBuildStartedAt(new Date());
  }


  public SSTestWindowsBuildScriptGenerator(final String s) {
    super(s);
  }
}
