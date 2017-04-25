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

import java.io.*;
import java.util.*;
import junit.framework.*;

import org.parabuild.ci.TestHelper;

/**
 * Tests home page
 */
public class SATestSystemEnvironment extends TestCase {

  private File stdoutFile;
  private File stderrFile;
  private File mergedFile;


  public SATestSystemEnvironment(final String s) {
    super(s);
  }


  /**
   * Tests that env map is correct
   */
  public void test_getEnv() throws Exception {
    final Map env = RuntimeUtils.getStartupEnv();
    assertNotNull(env);

    for (Iterator iter = env.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry e = (Map.Entry)iter.next();
      assertNotNull(e.getKey());
      assertNotNull(e.getValue());
    }
  }


  /**
   * Tests that parameter nameValuePairs are correct
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
    RuntimeUtils.execute((File)null, command, (Map)null, (File)null, (File)null, mergedFile);
    assertTrue(mergedFile.exists());
    assertTrue(mergedFile.length() > 0);
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
    return new TestSuite(SATestSystemEnvironment.class);
  }
}
