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
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.remote.internal.*;


public class SATestInputStreamToFileCopier extends TestCase {

  private static final Log log = LogFactory.getLog(SATestInputStreamToFileCopier.class);


  private InputStreamToFileCopier copier = null;
  private InputStreamToFileCopier zeroLengthcopier = null;
  private File outputFile = null;
  private File zeroLengthOutputFile = null;


  public void test_copyInputStreamToFile() throws Exception {
    // check if generates a file
    final long start = System.currentTimeMillis();
    copier.copyInputStreamToFile();
    if (log.isDebugEnabled()) log.debug("TIME = " + (System.currentTimeMillis() - start));
    assertTrue(outputFile.exists());
    // check if the file can be deleted
    outputFile.delete();
    assertTrue(!outputFile.exists());
  }


  public void test_zeroLengthResultCanBeDeleted() throws Exception {
    zeroLengthcopier.copyInputStreamToFile();
    assertTrue(zeroLengthOutputFile.exists());
    zeroLengthOutputFile.delete();
    assertTrue(!zeroLengthOutputFile.exists());
  }


  public void test_zeroLengthResultCanBeDeletedWhenThreaded() throws Exception {
    final Thread thread = new Thread(zeroLengthcopier);
    thread.start();
    thread.join();
    assertTrue(zeroLengthOutputFile.exists());
    zeroLengthOutputFile.delete();
    assertTrue(!zeroLengthOutputFile.exists());
  }


  protected void setUp() throws Exception {
    // output file
    outputFile = createTempFile("-output");
    final InputStream is = new BufferedInputStream(new FileInputStream(TestHelper.getTestDataDir() + "/" + "test_p4_describe.txt"), 600000);
    copier = new InputStreamToFileCopier(is, outputFile);

    // zero length input file
    final File zeroLengthFile = createTempFile("-zeroLengthSource");
    zeroLengthOutputFile = createTempFile("-zeroLengthOutput");
    zeroLengthFile.createNewFile();
    zeroLengthcopier = new InputStreamToFileCopier(new FileInputStream(zeroLengthFile), zeroLengthOutputFile);
  }


  private File createTempFile(final String suffix) {
    final File result = new File(TestHelper.getTestTempDir(), this.getClass().getName() + suffix);
    if (result.exists()) result.delete();
    assertTrue("Can't delete " + result, !result.exists());
    return result;
  }


  protected void tearDown() throws Exception {
    copier.close();
    zeroLengthcopier.close();
    super.tearDown();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestInputStreamToFileCopier.class);
  }


  public SATestInputStreamToFileCopier(final String s) {
    super(s);
  }
}
