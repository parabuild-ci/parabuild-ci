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
package org.parabuild.ci;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildRunner;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.build.RunnerStatus;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildService;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * Test constants holder
 */
public class TestHelper {

  private static final Log log = LogFactory.getLog(TestHelper.class);

  // constants
  public static final String PARAMETER_TEMP_DIR = "test.temp.dir";
  public static final String PARAMETER_DATA_DIR = "test.data.home";
  public static final String PARAMETER_PROD_DIR = "test.prod.home";

  public static final String CVS_VALID_ROOT = ":pserver:slava@localhost:/opt/cvs/cvsroot";
  public static final String CVS_ROOT_WITH_WRONG_USER = ":pserver:nobody@localhost:/opt/cvs/cvsroot";
  public static final String CVS_ROOT_WITH_UNKNOWN_HOST = ":pserver:nobody@nowhere:5555:/opt/cvs/cvsroot";
  public static final String CVS_ROOT_WITH_UNKNOWN_PORT = ":pserver:slava@localhost:5678:/opt/cvs/cvsroot";
  public static final String CVS_INVALID_SOURCE_LINE_PATH = "never/exested/source/line";

  public static final String CVS_VALID_PASSWORD_ROOT = ":pserver:testwpassword@localhost:/opt/cvs/cvsroot";
  public static final String CVS_VALID_PASSWORD = SecurityManager.encryptPassword("test_password");
  public static final String CVS_INVALID_PASSWORD = SecurityManager.encryptPassword("invalid_test_password");

  public static final String CVS_VALID_SOURCE_LINE_PATH = "test/sourceline/alwaysvalid";
  public static final String CVS_EMPTY_SOURCE_LINE_PATH = "test/sourceline/alwaysempty";

  public static final int TEST_CVS_VALID_BUILD_ID = 1;
  public static final int TEST_CVS_EMPTY_BUILD_ID = 5;
  public static final int TEST_P4_VALID_BUILD_ID = 3;
  public static final int TEST_RECURRENT_BUILD_ID = 4;
  public static final int TEST_REF_RECURRENT_BUILD_ID = 16;
  public static final int TEST_VSS_VALID_BUILD_ID = 6;
  public static final int TEST_SVN_VALID_BUILD_ID = 7;
  public static final int TEST_SVN_EMPTY_BUILD_ID = 8;
  public static final int TEST_SURROUND_VALID_BUILD_ID = 18;
  public static final int TEST_CLEARCASE_VALID_BUILD_ID = 19;
  public static final int TEST_VAULT_VALID_BUILD_ID = 20;
  public static final int TEST_PVCS_VALID_BUILD_ID = 21;
  public static final int TEST_STARTEAM_VALID_BUILD_ID = 22;

  // paraller build runs (P4 based)
  public static final int LEADING_BUILD_RUN_ID = 10;
  public static final int DEPENDENT_BUILD_RUN_ID_1 = 11;
  public static final int DEPENDENT_BUILD_RUN_ID_2 = 12;

  public static final int BUILD_STARTED_WAIT_SECONDS = 1000 * 240;
  public static final int BUILD_FINISHED_SECONDS = 1000 * 300;

  // SVN
  public static final String SVN_VALID_URL = "svn://localhost:11111";
  public static final String SVN_INVALID_HOST_URL = "svn://blah_blah_never_existed:11111";
  public static final String SVN_INVALID_PORT_URL = "svn://localhost:1234";
  public static final String SVN_INVALID_DEPOT_PATH = "never/exested/source/line";
  public static final String SVN_VALID_DEPOT_PATH = "test/sourceline/alwaysvalid";
  public static final String SVN_INVALID_USER = "never_existed_user";
  public static final String SVN_VALID_USER = "test_user";
  public static final String SVN_INVALID_PASSWORD = SecurityManager.encryptPassword("blah_blah");

  // P4
  public static final String P4_INVALID_PASSWORD = SecurityManager.encryptPassword("wrong_password");

  // VSS
  public static final String VSS_VALID_PASSWORD = SecurityManager.encryptPassword("test_password");
  public static final String VSS_INVALID_PASSWORD = SecurityManager.encryptPassword("invalid_pwd");
  public static final File JAVA_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));


  // MKS
  public static final int TEST_VALID_MKS_BUILD_ID = 23;

  // dependent parallel
  public static final int TEST_DEPENDENT_PARALLEL_BUILD_ID_1 = 28;


  public static final int TEST_LEADER_PARALLEL_BUILD_ID = 27;
  public static final int TEST_MERGE_ID = 0;

  /**
   * ID of a builder thats agents are offline.
   */
  public static final int FAILED_BUILDER_ID = 2;

  /**
   * Operational remote builder ID.
   */
  public static final int REMOTE_BUILDER_ID = 1;
  
  public static final int TEST_GIT_VALID_BUILD_ID = 34;


  /**
   * Helper style class constuctor.
   */
  private TestHelper() {
  }


  public static SourceControlSetting makeSourceControlSetting(final String name, final String value) {
    final SourceControlSetting setting = new SourceControlSetting();
    setting.setPropertyName(name);
    setting.setPropertyValue(value);
    return setting;
  }


  /**
   * Waits for given build runner status, waitTime ms
   *
   * @param runner
   * @param status
   * @param waitTime
   */
  public static void waitForStatus(final BuildRunner runner, final RunnerStatus status, final long waitTime) {
    if (log.isDebugEnabled()) log.debug("will wait for runner status: " + status);
    final long shouldBeReadyAt = System.currentTimeMillis() + waitTime;
    while (System.currentTimeMillis() < shouldBeReadyAt) {
      if (runner.getStatus().equals(status)) {
        break;
      }
    }
  }


  /**
   * Waits for given buildService status, waitTime ms
   *
   * @param buildService
   * @param status
   * @param waitTime
   */
  public static void waitForStatus(final BuildService buildService, final BuildStatus status, final long waitTime) {
    log.debug("started waiting when buildService status was: " + buildService.getBuildState().getStatus());
    final long shouldBeReadyAt = System.currentTimeMillis() + waitTime;
    while (System.currentTimeMillis() < shouldBeReadyAt) {
      if (buildService.getBuildState().getStatus().equals(status)) break;
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
    log.debug("finished waiting when buildService status was: " + buildService.getBuildState().getStatus());
  }


  /**
   * Returns file denoting temparary test dir
   */
  public static File getTestTempDir() {
    final String tempDirName = System.getProperty(PARAMETER_TEMP_DIR);
    final File result = new File(tempDirName);
    if (!result.exists()) result.mkdirs();
    if (!result.isDirectory())
      throw new IllegalStateException("Temporary test directory \"" + tempDirName + "\" is not a directory");
    return result;
  }


  /**
   * Returns file denoting temparary test dir
   */
  public static File getTestDataDir() {
    final String dataDirName = System.getProperty(PARAMETER_DATA_DIR);
    final File result = new File(dataDirName);
    if (!result.exists()) throw new IllegalStateException("Test data directory \"" + dataDirName + "\" not found");
    if (!result.isDirectory())
      throw new IllegalStateException("Temporary test directory \"" + dataDirName + "\" is not a directory");
    return result;
  }


  /**
   * Returns file denoting build build result home dir
   */
  public static File getProductDir() {
    final String dataDirName = System.getProperty(PARAMETER_PROD_DIR);
    final File result = new File(dataDirName);
    if (!result.exists()) throw new IllegalStateException("Build result directory \"" + dataDirName + "\" not found");
    if (!result.isDirectory())
      throw new IllegalStateException("Build result directory \"" + dataDirName + "\" is not a directory");
    return result;
  }


  public static void emptyCheckoutDir(final Agent agent) throws IOException, AgentFailureException {
    agent.emptyCheckoutDir();
    Assert.assertTrue("Checkout dir should exist after it was emptied", agent.checkoutDirExists());
    Assert.assertTrue("Checkout dir should be empty after it was emptied", agent.checkoutDirIsEmpty());
  }


  public static void assertCheckoutDirNotEmpty(final Agent agent) throws IOException, AgentFailureException {
    Assert.assertTrue("Checkout dir should not be empty", !agent.checkoutDirIsEmpty());
  }


  public static void assertDirIsNotEmpty(final File coDir) {
    assertExists(coDir);
    Assert.assertTrue("Directory should not be empty", coDir.listFiles().length > 0);
  }


  public static void assertDirIsEmpty(final File coDir) throws IOException {
    assertExists(coDir);
    Assert.assertTrue("Directory \"" + coDir.getCanonicalPath() + "\" should be empty", coDir.listFiles().length == 0);
  }


  public static void assertExists(final File file) {
    Assert.assertTrue("File or direcotry\"" + file.toString() + "\" does not exist", file.exists());
  }


  public static void assertExists(final String fileName) {
    assertExists(new File(fileName));
  }


  public static void assertExists(final String path, final Agent agent) throws IOException, AgentFailureException {
    assertExists(agent, path);
  }


  public static void assertExists(final Agent agent, final String path) throws IOException, AgentFailureException {
    Assert.assertTrue("File or direcotry \"" + path + "\" does not exist", agent.fileRelativeToCheckoutDirExists(path));
  }


  public static void assertNotExists(final File file) {
    Assert.assertTrue("File or direcotry \"" + file.toString() + "\" should not exist", !file.exists());
  }


  public static void assertNotExists(final Agent agent, final String relativePath) throws IOException, AgentFailureException {
    Assert.assertTrue("File or direcotry\"" + relativePath + "\" should not exist", !agent.fileRelativeToCheckoutDirExists(relativePath));
  }


  public static void assertCheckoutDirExistsAndEmpty(final Agent agent) throws IOException, AgentFailureException {
    Assert.assertTrue(agent.checkoutDirExists());
    Assert.assertTrue(agent.checkoutDirIsEmpty());
  }


  /**
   * Helper method to return las complete build run ID
   */
  public static int getLastCompleteBuildRunID(final int buildID) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun lastCompleteBuildRun = cm.getLastCompleteBuildRun(buildID);
    if (lastCompleteBuildRun == null) return BuildRun.UNSAVED_ID;
    Assert.assertNotNull(lastCompleteBuildRun);
    return lastCompleteBuildRun.getBuildRunID();
  }


  public static void failNoExceptionThrown() {
    Assert.fail("Expected exception but it was not thrown");
  }


  public static void assertOldBuildPathGoneAndNewAppeared(final SourceControl scm, final String oldRelativeBuildDir, final Agent agent) throws IOException, BuildException, AgentFailureException {
    log.debug("Asserting for SCM " + scm.getClass().getName());
    // assert
    Assert.assertTrue("Old relative path \"" + oldRelativeBuildDir + "\" should not exist", !agent.fileRelativeToCheckoutDirExists(oldRelativeBuildDir));
    final String newRelativeBuildDir = scm.getRelativeBuildDir();
    if (log.isDebugEnabled()) log.debug("oldRelativeBuildDir = " + oldRelativeBuildDir);
    if (log.isDebugEnabled()) log.debug("newAbsoluteBuildDir = " + newRelativeBuildDir);
    Assert.assertTrue("Directory does not exist: " + newRelativeBuildDir, agent.fileRelativeToCheckoutDirExists(newRelativeBuildDir));
    TestHelper.assertDirIsNotEmpty(agent, newRelativeBuildDir);
  }


  public static void assertDirIsNotEmpty(final Agent agent, final String pathRelativeToCheckout) throws IOException, AgentFailureException {
    Assert.assertTrue("Directory \"" + pathRelativeToCheckout + "\" relative to agent's checkout dir \"" + agent.getCheckoutDirName() + "\" should not be empty", !agent.dirRelativeToCheckoutDirIsEmpty(pathRelativeToCheckout));
  }


  /**
   * Returns build path that is relative to build's checkout
   * directory.
   */
  public static String assertCurrentBuildPathExists(final SourceControl scm, final Agent agent) throws IOException, BuildException, AgentFailureException {
    final String relativePath = scm.getRelativeBuildDir();
    assertCheckoutDirNotEmpty(agent);
    assertDirIsNotEmpty(agent, relativePath);
    return relativePath;
  }


  /**
   * Creates input stream from a file located in test/config
   * dir.
   */
  public static BufferedInputStream getTestFileAsInputStream(final String fileName) throws FileNotFoundException {
    return new BufferedInputStream(new FileInputStream(getTestFile(fileName)), 64000);
  }


  public static void assertDirectoryExists(final File dir) {
    Assert.assertNotNull(dir);
    Assert.assertTrue("Directory should exist", dir.exists());
    Assert.assertTrue("Dirctory should be a directory", dir.isDirectory());
  }


  public static String remoteTestBuilderHostName() {
    return "localhost:" + System.getProperty("test.builder.http.port");
  }


  /**
   * Tests that the build manager's page responces with 200 code
   * (HttpServletResponse.SC_OK) and contains a given string.
   *
   * @param page              - path after http://host:port, i.e. for
   *                          http://host:port/parabuild/index.htm it will be
   *                          /parabuild/index.htm.
   * @param stringToBePresent - string that must be present in
   *                          the responce.
   */
  public static final WebResponse assertPageSmokes(final String page, final String stringToBePresent)
          throws SAXException, IOException {
    return assertPageSmokes(ServiceManager.getInstance().getListenPort(), page, stringToBePresent, true);
  }


  public static final WebResponse assertPageSmokes(final String page, final String stringToBePresent, final boolean toBePresent)
          throws SAXException, IOException {
    return assertPageSmokes(ServiceManager.getInstance().getListenPort(), page, stringToBePresent, toBePresent);
  }


  public static WebResponse assertPageSmokes(final int port, final String page, final String str)
          throws IOException, SAXException {
    return assertPageSmokes(port, page, str, true);
  }


  /**
   * Tests that the page responces with 200 code
   * (HttpServletResponse.SC_OK) and contains a given string.
   *
   * @param port              - port
   * @param page              - path after http://host:port, i.e. for
   *                          http://host:port/parabuild/index.htm it will be
   *                          /parabuild/index.htm.
   * @param stringToBePresent - string that must be present in
   *                          the responce.
   */
  public static final WebResponse assertPageSmokes(final int port, final String page, final String stringToBePresent, final boolean toBePresent) throws SAXException, IOException {
    final String urlBase = "http://localhost:" + port;
    return assertPageSmokes(new URL(urlBase + page), stringToBePresent, toBePresent);
  }


  /**
   * Tests that the page responces with 200 code
   * (HttpServletResponse.SC_OK) and contains a given string.
   */
  public static final WebResponse assertPageSmokes(final URL url, final String stringToBePresent, final boolean toBePresent) throws SAXException, IOException {
    final WebConversation wc = new WebConversation();
    final WebResponse resp = wc.getResponse(url.toString());
    Assert.assertEquals(HttpServletResponse.SC_OK, resp.getResponseCode());
    if (StringUtils.isBlank(stringToBePresent)) return resp;
    Assert.assertEquals("String \"" + stringToBePresent + "\" in response.", toBePresent, resp.getText().indexOf(stringToBePresent) >= 0);
    return resp;
  }


  public static File getTestFile(final String fileName) {
    return new File(getTestDataDir(), fileName);
  }


  public static AgentHost validBuildHost() {
    return new AgentHost(remoteTestBuilderHostName());
  }


  /**
   * Helper method to search for a given string in a text file.
   *
   * @param fileName String file name
   * @param toFind   String test to find
   * @return true if found
   * @throws java.io.IOException
   */
  public static boolean findStringInTextFile(final String fileName, final String toFind) throws IOException {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(fileName));
      boolean found = false;
      for (String line = br.readLine(); line != null;) {
        if (line.indexOf(toFind) >= 0) {
          found = true;
          break;
        }
        line = br.readLine();
      }
      return found;
    } finally {
      IoUtils.closeHard(br);
    }
  }


  public static void assertPathsEqual(final String path1, final String path2) {
    Assert.assertEquals(new File(path1).toString(), new File(path2).toString());
  }


  public static void waitForThreadToDie(final Thread thread, final long t) throws InterruptedException {
    final long startedWaiting = System.currentTimeMillis();
    final long timeOut = startedWaiting + t;
    while (thread.isAlive() && (System.currentTimeMillis() <= timeOut)) {
      Thread.sleep(50);
    }
    if (log.isDebugEnabled()) log.debug("time for thread to die: " + (System.currentTimeMillis() - startedWaiting));
  }


  public static void setSourceControlProperty(final int buildID, final String propertyName, final String propertyValue) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    SourceControlSetting prop = cm.getSourceControlSetting(buildID, propertyName);
    if (prop == null) {
      prop = new SourceControlSetting(buildID, propertyName, propertyValue);
    } else {
      prop.setPropertyValue(propertyValue);
    }
    cm.saveObject(prop);
  }


  public static void setSequenceScript(final int id, final String scriptText) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildSequence bseq = (BuildSequence) cm.getObject(BuildSequence.class, id);
    bseq.setScriptText(scriptText);
    cm.save(bseq);
  }


  public static void setSystemProperty(final String propertyName, final String propertyValue) {
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    SystemProperty prop = systemCM.getSystemProperty(propertyName);
    if (prop == null) {
      prop = new SystemProperty(propertyName, propertyValue);
    } else {
      prop.setPropertyValue(propertyValue);
    }
    systemCM.saveSystemProperty(prop);
  }


  public static void setSourceControlProperty(final int buildID, final String name, final byte value) {
    setSourceControlProperty(buildID, name, Byte.toString(value));
  }


  public static Date makeDate(final int year, final int month, final int day, final int hour, final int minute, final int second) {
    final Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DATE, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    return calendar.getTime();
  }


  public static void changeScheduleType(final int buildID, final byte scheduleTypeManual) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ActiveBuildConfig activeBuildConfig = cm.getActiveBuildConfig(buildID);
    activeBuildConfig.setScheduleType(scheduleTypeManual);
    cm.save(activeBuildConfig);
  }


  public static String getTestDataFile(final String fileName) {
    return IoUtils.fileToString(new File(getTestDataDir(), fileName));
  }
}
