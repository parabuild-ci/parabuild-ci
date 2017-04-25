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
package org.parabuild.ci.common;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.remote.AgentEnvironment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Tests RuntimeUtils
 */
public final class SATestRuntimeUtils extends TestCase {

  private static final Log log = LogFactory.getLog(SATestRuntimeUtils.class);

  private File stdoutFile;
  private File stderrFile;
  private File mergedFile;


  public SATestRuntimeUtils(final String s) {
    super(s);
  }


  /**
   * Tests that env map is correct.
   */
  public void test_getEnv() throws Exception {
    final Map env = RuntimeUtils.getStartupEnv();
    assertNotNull(env);

    for (Iterator iter = env.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry e = (Map.Entry) iter.next();
      assertNotNull(e.getKey());
      assertNotNull(e.getValue());
    }

    if (RuntimeUtils.isWindows()) {
      assertNotNull(env.get("USERNAME"));
    }
  }


  /**
   * Tests that parameter nameValuePairs are correct.
   */
  public void test_getEnvironmentParametersFromMap() throws Exception {
    final Map env = RuntimeUtils.getStartupEnv();
    final String[] nameValuePairs = RuntimeUtils.getEnvironmentParametersFromMap(env);
    for (int i = 0; i < nameValuePairs.length; i++) {
      final String nameValuePair = nameValuePairs[i];
      assertNotNull(nameValuePair);
    }
  }


  /**
   *
   */
  public void test_Bug165_canHandleLargeOuput() throws Exception {
    BufferedReader br = null;
    try {
      if (!RuntimeUtils.isWindows()) return;
      final File dataDir = TestHelper.getTestDataDir();
      final File commandToExecute = new File(dataDir, "generate10000Lines.bat");
      RuntimeUtils.execute(null, "cmd /C " + commandToExecute.getAbsolutePath(), null, stdoutFile, stderrFile, null);
      assertTrue(stdoutFile.exists());
      assertTrue(!stderrFile.exists() || stderrFile.length() == 0);

      // count number of lines
      br = new BufferedReader(new FileReader(stdoutFile));
      int lineCount = 0;
      while (br.readLine() != null) {
        lineCount++;
      }
      assertEquals(10000, lineCount);
    } finally {
      IoUtils.closeHard(br);
    }
  }


  public void test_mergedExecuteHandlesNullFiles() throws Exception {
    String command = null;
    if (RuntimeUtils.isWindows()) {
      command = "cmd /C dir";
    } else {
      command = "ls";
    }
    RuntimeUtils.execute((File) null, command, (Map) null, (File) null, (File) null, mergedFile);
    assertTrue(mergedFile.exists());
    assertTrue(mergedFile.length() > 0);
  }


  public void test_commandIsAvailable() throws IOException {
    final int type = RuntimeUtils.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX
            || type == AgentEnvironment.SYSTEM_TYPE_MACOSX
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX) {
      assertCommandAvailable("/bin/ls");
    } else if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      // test plain
      String windowsSystemRoot = RuntimeUtils.getEnvVariable("SystemRoot");
      if (StringUtils.isBlank(windowsSystemRoot)) {
        windowsSystemRoot = RuntimeUtils.getEnvVariable("SYSTEMROOT");
      }
      assertCommandAvailable(windowsSystemRoot + "\\system32\\cmd.exe");
      // test command with space in path
      final File testTempDir = new File(TestHelper.getTestTempDir(), "spaced path");
      testTempDir.mkdirs();
      final File commandWithSpaceInPath = new File(testTempDir, "testcmd.exe");
      commandWithSpaceInPath.createNewFile();
      final String spacedPath = commandWithSpaceInPath.getCanonicalPath();
      assertTrue(spacedPath.indexOf(' ') >= 0);
      if (log.isDebugEnabled()) log.debug("canonicalPath: " + spacedPath);
      assertCommandAvailable('\"' + spacedPath + '\"');
      assertCommandAvailable(spacedPath);
      commandWithSpaceInPath.delete();
    } else {
      fail("Unknown system");
    }
  }


  public void test_commandIsAvailableRecognizesCommandsInPath() {
    assertCommandAvailable("cvs");
  }

  public void testGetMacAddressList() {
    if (!RuntimeUtils.isWindows()) return;
    assertTrue(!RuntimeUtils.getMacAddressList().isEmpty());
  }


  private void assertCommandAvailable(final String command) {
    assertTrue("Command " + command + " should be available", RuntimeUtils.commandIsAvailable(command));
  }


  protected void setUp() throws Exception {
    super.setUp();
    final File tempDir = TestHelper.getTestTempDir();
    stdoutFile = new File(tempDir, this.getClass().getName() + "-stdout");
    stderrFile = new File(tempDir, this.getClass().getName() + "-stderr");
    mergedFile = new File(tempDir, this.getClass().getName() + "-merged");
    if (stdoutFile.exists()) stdoutFile.delete();
    if (stderrFile.exists()) stderrFile.delete();
    if (mergedFile.exists()) mergedFile.delete();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestRuntimeUtils.class);
  }
}
