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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;

/**
 * Tests UnixBuildScriptGenerator
 */
public class SSTestUnixBuildScriptGenerator extends ServersideTestCase {

  private UnixBuildScriptGenerator scriptGenerator = null;
  private Agent agent;
  private ConfigurationManager cm;


  public void test_bug262_generateScript() throws Exception {
    // read a sequence
    final BuildSequence bs = getTestBuildSequence();

    // generate
    final String scriptFileName = scriptGenerator.generateScriptFile(bs);

    // check if export string is there
    boolean found = TestHelper.findStringInTextFile(scriptFileName, "export " + BuildScriptGenerator.VAR_PARABUILD_BUILD_NUMBER);
    assertTrue("export PARABUILD_BUILD_NUMBER not found", found);
    found = TestHelper.findStringInTextFile(scriptFileName, BuildScriptGenerator.VAR_PARABUILD_BUILD_NUMBER + "=0");
    assertTrue("PARABUILD_BUILD_NUMBER= not found", found);
  }


  /**
   * Validates that PARABUILD_CHANGE_LIST_NUMBER is in the
   * script;
   */
  public void test_bug283_generateScript() throws Exception {
    final BufferedReader br = null;
    try {
      // read a sequence
      final BuildSequence bs = getTestBuildSequence();

      // generate
      final String scriptFileName = scriptGenerator.generateScriptFile(bs);

      // check if export string is there
      boolean found = TestHelper.findStringInTextFile(scriptFileName, "export " + BuildScriptGenerator.VAR_PARABUILD_CHANGE_LIST_NUMBER);
      assertTrue("export PARABUILD_CHANGE_LIST_NUMBER not found", found);
      found = TestHelper.findStringInTextFile(scriptFileName, BuildScriptGenerator.VAR_PARABUILD_CHANGE_LIST_NUMBER + "=1");
      assertTrue("PARABUILD_CHANGE_LIST_NUMBER= not found", found);
    } finally {
      IoUtils.closeHard(br);
    }
  }


  public void test_unsetsSystemParabuildEnvVariables() throws Exception {

    final String[] varsToErase = AbstractBuildScriptGenerator.getVarsToErase();
    final boolean[] testSetCleared = new boolean[varsToErase.length];

    // read a sequence
    final BuildSequence bs = getTestBuildSequence();

    // generate
    final String scriptFileName = scriptGenerator.generateScriptFile(bs);

    // check if export string is there
    for (int i = 0; i < varsToErase.length; i++) {
      testSetCleared[i] = TestHelper.findStringInTextFile(scriptFileName, "unset " + varsToErase[i]);
    }

    for (int i = 0; i < testSetCleared.length; i++) {
      if (!testSetCleared[i]) fail("Variable " + varsToErase[i] + " has not been cleared");
    }
  }


  public void test_bug720NoDuplicatesInTheEnvironment() throws IOException, BuildException, CommandStoppedException, AgentFailureException {
    if (!agent.isUnix()) return;
    BufferedReader br = null;
    try {
      // get sequence
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final List sequences = cm.getAllBuildSequences(TestHelper.TEST_CVS_VALID_BUILD_ID, BuildStepType.BUILD);
      final BuildSequence bs = (BuildSequence) sequences.get(0);
      assertNotNull(bs);
      bs.setScriptText("export PATH=$PATH:signature_path:\nenv\n");
      cm.saveObject(bs); // just in case it is retrieved some where

      // generate and execute
      final File merged = TestHelper.getTestFile(this.getClass().getName() + "-merged");
      final String scriptFileName = scriptGenerator.generateScriptFile(bs);
      final UnixBuildScriptRunner scriptRunner = new UnixBuildScriptRunner(agent);
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
    final BuildSequence bs = getTestBuildSequence();
    final String scriptFileName = scriptGenerator.generateScriptFile(bs);

    // check if name and value is there
    assertNameValuePairIsExported(scriptFileName, testName1, testValue1);
    assertNameValuePairIsExported(scriptFileName, testName2, testValue2);
  }


  private void assertNameValuePairIsExported(final String scriptFileName, final String name, final String value) throws IOException {
    assertTrue(TestHelper.findStringInTextFile(scriptFileName, "export " + name));
    assertTrue(TestHelper.findStringInTextFile(scriptFileName, name + '=' + value));
    assertTrue(!TestHelper.findStringInTextFile(scriptFileName, "export " + name + '=' + value));
  }


  public void test_buildRunIDIsPresent() throws BuildException, IOException, AgentFailureException {
    // read a sequence
    final BuildSequence bs = getTestBuildSequence();

    // generate
    final String scriptFileName = scriptGenerator.generateScriptFile(bs);

    // check if export string is there
    final boolean found = TestHelper.findStringInTextFile(scriptFileName, "export " + BuildScriptGenerator.VAR_PARABUILD_BUILD_RUN_ID);
    assertTrue("export PARABUILD_BUILD_RUN_ID not found", found);
  }


  public void test_buildTimestampDatePresent() throws BuildException, IOException, AgentFailureException {
    final BuildSequence bs = getTestBuildSequence();
    final String scriptFileName = scriptGenerator.generateScriptFile(bs);
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_BUILD_TIMESTAMP, TestHelper.findStringInTextFile(scriptFileName, "export " + BuildScriptGenerator.VAR_PARABUILD_BUILD_TIMESTAMP));
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_BUILD_DATE, TestHelper.findStringInTextFile(scriptFileName, "export " + BuildScriptGenerator.VAR_PARABUILD_BUILD_DATE));
  }


  public void test_sequenceNumberPresent() throws BuildException, IOException, AgentFailureException {
    final BuildSequence bs = getTestBuildSequence();
    final String scriptFileName = scriptGenerator.generateScriptFile(bs);
    assertTrue(BuildScriptGenerator.VAR_PARABUILD_SEQUENCE_NUMBER, TestHelper.findStringInTextFile(scriptFileName, "export " + BuildScriptGenerator.VAR_PARABUILD_SEQUENCE_NUMBER));
  }


  /**
   * Tests that when set change number is not equal build nuber.
   */
  public void test_bug558() {
    scriptGenerator.setBuildNumber(999);
    scriptGenerator.setChangeListNumber("777");
    scriptGenerator.setChangeListDate(new Date());
    final int validBuildNumber = scriptGenerator.getValidBuildNumber();
    final int validChangeListNumber = Integer.parseInt(scriptGenerator.getValidChangeListNumber());
    assertTrue(validBuildNumber != validChangeListNumber);
  }


  /**
   * Helper method.
   */
  private BuildSequence getTestBuildSequence() {
    final List sequences = cm.getAllBuildSequences(TestHelper.TEST_CVS_VALID_BUILD_ID, BuildStepType.BUILD);
    final BuildSequence bs = (BuildSequence) sequences.get(0);
    assertNotNull(bs);
    return bs;
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CVS_VALID_BUILD_ID);
    scriptGenerator = new UnixBuildScriptGenerator(agent);
    scriptGenerator.setBuildNumber(0);
    scriptGenerator.setBuildName("TEST_BUILD_NAME");
    scriptGenerator.setStepName("TEST_STEP_NAME");
    scriptGenerator.setChangeListNumber("1");
    scriptGenerator.setChangeListDate(new Date());
    scriptGenerator.setRelativeBuildDir("test");
    scriptGenerator.setBuildRunID(1);
    scriptGenerator.setBuildStartedAt(new Date());
  }


  public SSTestUnixBuildScriptGenerator(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestUnixBuildScriptGenerator.class);
  }
}
