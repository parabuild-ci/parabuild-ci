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
import java.util.*;
import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.remote.internal.*;

/**
 * Tests directory helper
 */
public class SSTestBuildFiles extends ServersideTestCase {

  private static final int TEST_BUILD_ID = 1;

  private LocalBuilderFiles buildFiles = null;
  private BuildSequence sequence = null;


  public SSTestBuildFiles(final String s) {
    super(s);
  }


  public void test_createBuildDirecories() throws IOException {
    final File checkoutDir = buildFiles.getCheckoutDir(true);
    TestHelper.assertDirectoryExists(checkoutDir);
  }


  public void test_getWrapperScriptDirectory() {
    final File wrappersScriptsDir = buildFiles.getStepsScriptsDirectory();
    TestHelper.assertDirectoryExists(wrappersScriptsDir);
  }


  public void test_getMainBuildLogDirectory() {
    final File mainBuildLogDir = buildFiles.getBuildLogDir();
    TestHelper.assertDirectoryExists(mainBuildLogDir);
  }


  public void test_DirNamesAreDifferent() throws Exception {
    final File[] dirs = new File[]{
      buildFiles.getCheckoutDir(true),
      buildFiles.getStepsScriptsDirectory(),
      buildFiles.getBuildLogDir()
    };

    for (int i = 0; i < dirs.length; i++) {
      final File dir = dirs[i];
      for (int j = i + 1; j < dirs.length; j++) {
        final File dir2 = dirs[j];
        assertTrue(!dir.getCanonicalPath().equals(dir2.getCanonicalPath()));
      }
    }
  }


  public void test_getSequenceScriptFile() throws Exception {
    assertNotNull(buildFiles.getStepScriptFile(sequence.getSequenceID()));
  }


  public void test_getSequenceScriptFileRetunrsDifferentFilesForDifferentSequenceNumbers() throws Exception {
    // create sequence with seq ID 1000
    final BuildSequence seq1000 = new BuildSequence();
    seq1000.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    seq1000.setSequenceID(1000);
    // create sequence with seq ID 2000
    final BuildSequence seq2000 = new BuildSequence();
    seq2000.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    seq2000.setSequenceID(2000);
    // get files
    final File f1000 = buildFiles.getStepScriptFile(seq1000.getSequenceID());
    final File f2000 = buildFiles.getStepScriptFile(seq2000.getSequenceID());
    // test
    assertTrue(!f1000.equals(f2000));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildFiles.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    final BuildConfig buildConfiguration = configurationManager.getBuildConfiguration(TEST_BUILD_ID);
    buildFiles = new LocalBuilderFiles(buildConfiguration.getBuildID(), null);
    buildFiles.createBuildDirs();
    final List sequences = configurationManager.getAllBuildSequences(buildConfiguration.getBuildID(), BuildStepType.BUILD);
    assertTrue(!sequences.isEmpty());
    sequence = (BuildSequence)sequences.get(0);
  }
}
