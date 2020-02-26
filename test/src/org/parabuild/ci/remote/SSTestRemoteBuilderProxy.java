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
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.process.TailBufferSizeImpl;
import org.parabuild.ci.remote.internal.RemoteAgentProxy;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

/**
 * Tests proxy to remote agent
 */
public class SSTestRemoteBuilderProxy extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestRemoteBuilderProxy.class);
  private static final TailBufferSizeImpl ZERO_LOG_TAIL_SIZE = new TailBufferSizeImpl(0, 0);

  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private RemoteAgentProxy proxy = null;
  public static final String TEMP_FILE_PREFIX = "parabuild";
  private static final String TEMP_FILE_SUFFIX = ".tmp";
  private AgentHost agentHost;


  public SSTestRemoteBuilderProxy(final String s) {
    super(s);
  }


  /**
   */
  public void test_systemType() throws Exception {
    final int type = proxy.systemType();
    assertTrue("System type should be one of known values",
            type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
                    || type == AgentEnvironment.SYSTEM_TYPE_SUNOS
                    || type == AgentEnvironment.SYSTEM_TYPE_UNIX
                    || type == AgentEnvironment.SYSTEM_TYPE_LINUX
                    || type == AgentEnvironment.SYSTEM_TYPE_WIN95
                    || type == AgentEnvironment.SYSTEM_TYPE_WINNT);
  }


  public void test_commandIsAvailable() throws Exception {
    final int type = proxy.systemType();
    String command = null;
    if (type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX) {
      command = "/bin/ls";
    } else if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      String windowsSystemRoot = proxy.getEnvVariable("SystemRoot");
      if (StringUtils.isBlank(windowsSystemRoot)) {
        windowsSystemRoot = proxy.getEnvVariable("SYSTEMROOT");
      }
      command = windowsSystemRoot + "\\system32\\cmd.exe";
    } else {
      fail("Unknown system");
    }
    assertTrue("Command " + command + " should be available",
            proxy.commandIsAvailable(command));
  }


  public void test_isUnix() throws Exception {
    final int type = proxy.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX) {
      assertTrue(proxy.isUnix());
    }
  }


  public void test_isWindows() throws Exception {
    final int type = proxy.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      assertTrue(proxy.isWindows());
    }
  }


  public void test_executeMerged() throws Exception {
    File stdoutFile = null;
    File stderrFile = null;
    File mergedFile = null;
    try {
      final boolean windows = proxy.isWindows();
      final String command = windows ? "cmd /C dir" : "/bin/sh -c ls";
      stdoutFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".out", TestHelper.getTestTempDir());
      stderrFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".err", TestHelper.getTestTempDir());
      mergedFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".mrg", TestHelper.getTestTempDir());
      proxy.execute(0, null, command, null, ZERO_LOG_TAIL_SIZE, stdoutFile, stderrFile, mergedFile);
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
      final String command = (proxy.isWindows()) ? "cmd /C dir" : "sh -c ls";
      stdoutFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".out", TestHelper.getTestTempDir());
      stderrFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".err", TestHelper.getTestTempDir());
      try {
        proxy.execute(0, unexistingCurrentDir, command, null, ZERO_LOG_TAIL_SIZE, stdoutFile, stdoutFile, null);
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
    final String fileName = proxy.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, "test content");
    assertNotNull(fileName);
    assertTrue(fileName.endsWith(TEMP_FILE_SUFFIX));
  }


  public void test_deleteTempFile() throws Exception {
    final String fileName = proxy.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, "test content");
    assertTrue(proxy.deleteTempFile(fileName)); // confirms deleted
    assertTrue(!proxy.absolutePathExists(fileName)); // second call should return false - does not exist
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
      proxy.readFile("blah", tempFile);
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
      scm.setAgentHost(agentHost);
      final String remoteCheckoutDir = proxy.getCheckoutDirName();
      final String relativeBuildDir = scm.getRelativeBuildDir();
      final String remoteBuildDirName = remoteCheckoutDir + '/' + relativeBuildDir;
      final String dirToGet = remoteBuildDirName + '/' + this.getClass().getName();
      String dirName = dirToGet;
      int pathsCreatedCounter = 0;
      for (int i = 0; i < 10; i++) {
        dirName += ("/d" + i);
        proxy.mkdirs(dirName);
        pathsCreatedCounter++;
        for (int j = 0; j < 10; j++) {
          final String fileName = dirName + "/f" + j;
          proxy.createFile(fileName, "content_" + j);
          pathsCreatedCounter++;
        }
      }

      // get remote build dir
      final File destDir = new File(TestHelper.getTestTempDir(), this.getClass().getName());
      destDir.mkdirs();
      proxy.getDirectory(dirToGet, destDir);

      // assert
      final List result = new LinkedList();
      listFilesUnderDir(result, destDir, true);
      assertEquals(pathsCreatedCounter, result.size());
    } finally {
      // remove stuff from the checkout dir
      proxy.emptyCheckoutDir();
    }
  }


  public void test_defaultLocale() throws IOException, AgentFailureException {
    final Locale locale = proxy.defaultLocale();
    assertNotNull(locale);
  }


  public void test_getFileDescriptor() throws Exception {
    final String testContent = "test content";
    final String fileName = proxy.createTempFile(TEMP_FILE_PREFIX, "test", testContent);
    final RemoteFileDescriptor fileDescriptor = proxy.getFileDescriptor(fileName);

    assertNotNull(fileDescriptor);
    assertTrue(fileDescriptor.isFile());
    assertTrue(fileDescriptor.lastModified() > 0);
    assertEquals(fileDescriptor.length(), testContent.length());

    final File f = new File(fileName);
    assertEquals(f.length(), fileDescriptor.length());
    assertEquals(f.lastModified(), fileDescriptor.lastModified());
  }


  /**
   * Helper recursive file lister. Does not return dir itself.
   *
   * @param result
   * @param path
   * @throws IOException
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
    return new OrderedTestSuite(SSTestRemoteBuilderProxy.class, new String[]{
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
    agentHost = TestHelper.validBuildHost();
    proxy = new RemoteAgentProxy(TEST_BUILD_ID, agentHost, null);
  }
}
