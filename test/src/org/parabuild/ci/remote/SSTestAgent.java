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
package org.parabuild.ci.remote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.caucho.hessian.io.HessianProtocolException;
import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.process.TailBufferSizeImpl;
import org.parabuild.ci.remote.internal.RemoteAgentProxy;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

/**
 * Tests agent instances retuned from agent factory.
 */
public class SSTestAgent extends ServersideTestCase {

  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private static final Log log = LogFactory.getLog(SSTestAgent.class);
  private Agent agent = null;
  public static final String TEMP_FILE_PREFIX = "parabuild";
  private static final String TEMP_FILE_SUFFIX = ".tmp";
  private static final String FILE_PREFIX = "fp_";
  private static final String DIR_PREFIX = "dp_";
  private static final TailBufferSize ZERO_LENGTH_TAIL_BUFFER_SIZE = new TailBufferSizeImpl(0, 0);


  public SSTestAgent(final String s) {
    super(s);
  }


  /**
   */
  public void test_systemType() throws Exception {
    final int type = agent.systemType();
    assertTrue("System type should be one of known values",
            type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
                    || type == AgentEnvironment.SYSTEM_TYPE_SUNOS
                    || type == AgentEnvironment.SYSTEM_TYPE_UNIX
                    || type == AgentEnvironment.SYSTEM_TYPE_LINUX
                    || type == AgentEnvironment.SYSTEM_TYPE_WIN95
                    || type == AgentEnvironment.SYSTEM_TYPE_WINNT);
  }


  public void test_commandIsAvailable() throws Exception {
    final int type = agent.systemType();
    String command = null;
    if (type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX) {
      command = "/bin/ls";
    } else if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      String windowsSystemRoot = agent.getEnvVariable("SystemRoot");
      if (StringUtils.isBlank(windowsSystemRoot)) {
        windowsSystemRoot = agent.getEnvVariable("SYSTEMROOT");
      }
      command = windowsSystemRoot + "\\system32\\cmd.exe";
    } else {
      fail("Unknown system");
    }
    assertTrue("Command " + command + " should be available",
            agent.commandIsAvailable(command));
  }


  public void test_isUnix() throws Exception {
    final int type = agent.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX) {
      assertTrue(agent.isUnix());
    }
  }


  public void test_isWindows() throws Exception {
    final int type = agent.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      assertTrue(agent.isWindows());
    }
  }


  public void test_executeMerged() throws Exception {
    File stdoutFile = null;
    File stderrFile = null;
    File mergedFile = null;
    try {
      stdoutFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".out", TestHelper.getTestTempDir());
      stderrFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".err", TestHelper.getTestTempDir());
      mergedFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".mrg", TestHelper.getTestTempDir());
      final String command = (agent.isWindows()) ? "cmd /C dir" : "/bin/sh -c ls -al";
      final String tempDirName = agent.getTempDirName();
      agent.createTempFile(TEMP_FILE_PREFIX, ".test", "test file");
      agent.execute(0, tempDirName, command, null, ZERO_LENGTH_TAIL_BUFFER_SIZE, stdoutFile, stderrFile, mergedFile);
      if (log.isDebugEnabled()) log.debug("stderrFile: " + stderrFile.length());
      if (log.isDebugEnabled()) log.debug("stdoutFile: " + stdoutFile.length());
      if (log.isDebugEnabled()) log.debug("mergedFile: " + mergedFile.length());
      assertTrue(stdoutFile.length() > 0);
      assertTrue(stderrFile.length() == 0);
      assertTrue(mergedFile.length() >= 0);
      assertEquals(stdoutFile.length(), mergedFile.length());
    } finally {
      IoUtils.deleteFileHard(stdoutFile);
      IoUtils.deleteFileHard(stderrFile);
      IoUtils.deleteFileHard(mergedFile);
    }
  }


  public void test_executeFailsOnNonExistingCurrentDir() throws Exception {
    File stdoutFile = null;
    File stderrFile = null;
    final String unexistingCurrentDir = "neverexisted" + System.currentTimeMillis();
    try {
      final String command = (agent.isWindows()) ? "cmd /C dir" : "/bin/sh -c ls";
      stdoutFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".out", TestHelper.getTestTempDir());
      stderrFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".err", TestHelper.getTestTempDir());
      try {
        agent.execute(0, unexistingCurrentDir, command, null, ZERO_LENGTH_TAIL_BUFFER_SIZE, stdoutFile, stdoutFile, null);
        TestHelper.failNoExceptionThrown();
      } catch (IOException e) {
      }
    } finally {
      IoUtils.deleteFileHard(stdoutFile);
      IoUtils.deleteFileHard(stderrFile);
    }
  }


  /**
   * Tests that proxy fails on non-existing host.
   */
  public void test_failsOnNonExistingHost() {
    final AgentHost nonexistingAgentHost = new AgentHost("host_" + System.currentTimeMillis(), "");
    final RemoteAgentProxy proxy = new RemoteAgentProxy(TEST_BUILD_ID, nonexistingAgentHost, null);
    try {
      proxy.systemType();
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_createTempFile() throws Exception {
    final String fileName = agent.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, "test content");
    assertNotNull(fileName);
    assertTrue(fileName.endsWith(TEMP_FILE_SUFFIX));
  }


  public void test_deleteTempFile() throws Exception {
    final String fileName = agent.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, "test content");
    assertTrue(agent.deleteTempFile(fileName)); // confirms deleted
    assertTrue(!agent.absolutePathExists(fileName)); // second call should return false - does not exist
  }


  public void test_failsOnInvalidPassword() throws Exception {
    final AgentHost agentHost = new AgentHost(TestHelper.remoteTestBuilderHostName(), "blah" + System.currentTimeMillis());
    final RemoteAgentProxy misconfiguredProxy = new RemoteAgentProxy(TEST_BUILD_ID, agentHost, null);
    try {
      misconfiguredProxy.isUnix();
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
      assertTrue(!(e instanceof HessianProtocolException));
    }
  }


  public void test_getFileFailsOnNonExistingFile() throws IOException, AgentFailureException {
    File tempFile = null;
    try {
      tempFile = IoUtils.createTempFile("test", "test", TestHelper.getTestTempDir());
      agent.readFile("blah", tempFile);
      TestHelper.failNoExceptionThrown();
    } catch (FileNotFoundException e) {
      // expected
    } catch (IOException e) {
      fail("Should never get here: " + e.getClass().getName() + " / " + e.toString());
    } finally {
      IoUtils.deleteFileHard(tempFile);
    }
  }


  public void test_getDirectory() throws Exception {
    try {
      // create test files and directories
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final SourceControl scm = VersionControlFactory.makeVersionControl(cm.getBuildConfiguration(TEST_BUILD_ID));
      scm.setAgentHost(agent.getHost());
      final String remoteCheckoutDir = agent.getCheckoutDirName();
      final String relativeBuildDir = scm.getRelativeBuildDir();
      final String remoteBuildDirName = remoteCheckoutDir + '/' + relativeBuildDir;
      final String dirToGet = remoteBuildDirName + '/' + this.getClass().getName();
      String dirName = dirToGet;
      long timeToCreateRemoteDir = 0;
      long timeToCreateRemoteFile = 0;
      int pathsCreatedCounter = 0;
      for (int i = 0; i < 5; i++) {
        dirName += ('/' + DIR_PREFIX + i);
        final long startDirTime = System.currentTimeMillis();
        agent.mkdirs(dirName);
        timeToCreateRemoteDir += (System.currentTimeMillis() - startDirTime);
        pathsCreatedCounter++;
        for (int j = 0; j < 5; j++) {
          final String fileName = dirName + '/' + FILE_PREFIX + j;
          final long startFileTime = System.currentTimeMillis();
          agent.createFile(fileName, "content_" + j);
          timeToCreateRemoteFile += (System.currentTimeMillis() - startFileTime);
          pathsCreatedCounter++;
        }
      }

      if (log.isDebugEnabled())
        log.debug("average time to create remote dir = " + (timeToCreateRemoteDir / pathsCreatedCounter));
      if (log.isDebugEnabled())
        log.debug("average time to create remote file = " + (timeToCreateRemoteFile / pathsCreatedCounter));

      // get remote build dir
      final File ttd = TestHelper.getTestTempDir();
      final File destDir = new File(ttd, this.getClass().getName());
      destDir.mkdirs();
      long timeToGetDir = 0;
      final long startGetTime = System.currentTimeMillis();
//      agent.getDirectory(dirToGet, destDir); // this additional slash increases length of the dir path
      agent.getDirectory(dirToGet + "//", destDir); // this additional slashes increases length of the dir path
      timeToGetDir = (System.currentTimeMillis() - startGetTime);
      if (log.isDebugEnabled()) log.debug("time to get remote dir = " + timeToGetDir);
      if (log.isDebugEnabled()) log.debug("average time to get remote path = " + (timeToGetDir / pathsCreatedCounter));

      // assert
      final List result = new LinkedList();
      listFilesUnderDir(result, destDir, true);
//      if (log.isDebugEnabled()) log.debug("result: " + result);
      assertEquals(pathsCreatedCounter, result.size());
      for (int i = 0; i < result.size(); i++) {
        final String name = ((File) result.get(i)).getName();
        if (name.startsWith(DIR_PREFIX)) continue;
        assertTrue("File name should start with \"" + FILE_PREFIX + "\" but it is \"" + name + '\"', name.startsWith(FILE_PREFIX));
      }
    } finally {
      // remove stuff from the checkout dir
      agent.emptyCheckoutDir();
    }
  }


  public void test_defaultLocale() throws IOException, AgentFailureException {
    final Locale locale = agent.defaultLocale();
    assertNotNull(locale);
  }


  public void test_getFileDescriptor() throws Exception {
    final String testContent = "test content";
    final String fileName = agent.createTempFile(TEMP_FILE_PREFIX, "test", testContent);
    final RemoteFileDescriptor fileDescriptor = agent.getFileDescriptor(fileName);

    assertNotNull(fileDescriptor);
    assertTrue(fileDescriptor.isFile());
    assertTrue(fileDescriptor.lastModified() > 0);
    assertEquals(fileDescriptor.length(), testContent.length());

    final File f = new File(fileName);
    assertEquals(f.length(), fileDescriptor.length());
    assertEquals(f.lastModified(), fileDescriptor.lastModified());
  }


  public void test_bug740_deleteCheckoutDir() throws IOException, AgentFailureException {
    agent.getCheckoutDirName();
    assertTrue("Checkout dir should exist", agent.checkoutDirExists());
    assertTrue("Delete should be successful", agent.deleteCheckoutDir());
    assertTrue("Checkout dir should not exist after delete", !agent.checkoutDirExists());
  }


  public void test_deleteBuildFiles() throws IOException, AgentFailureException {
    agent.createBuildDirs();
    final String checkoutDirName = agent.getCheckoutDirName();
    final String absoluteFile = checkoutDirName + '/' + getClass().getName();
    agent.createFile(absoluteFile, "test content");
    assertTrue(agent.deleteBuildFiles());
    assertTrue(!agent.absolutePathExists(absoluteFile));
  }


  /**
   * Helper recursive file lister. Does not return dir itself.
   *
   * @param result
   * @param path
   * @throws java.io.IOException
   */
  private void listFilesUnderDir(final List result, final File path, final boolean firstTime) throws IOException {
    if (path == null || !path.exists()) return;
    if (!firstTime) result.add(path);
    if (path.isDirectory()) {
      final File[] files = path.listFiles();
      for (int ii = 0; ii < files.length; ii++) {
        listFilesUnderDir(result, files[ii], false);
      }
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestAgent.class, new String[]{
            "test_bug740_deleteCheckoutDir",
            "test_defaultLocale",
            "test_getDirectory",
            "test_getFileFailsOnNonExistingFile",
            "test_failsOnInvalidPassword",
            "test_createTempFile",
            "test_deleteTempFile",
            "test_getFileDescriptor"
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID);
  }
}
