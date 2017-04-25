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
package org.parabuild.ci.installer;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests concept of using regex to identify bug IDs in change
 * list descriptions.
 */
public class SATestUsingRegexToFindLowMemoryFile extends TestCase {

  private static final Log LOG = LogFactory.getLog(SATestUsingRegexToFindLowMemoryFile.class); // NOPMD


  public SATestUsingRegexToFindLowMemoryFile(final String s) {
    super(s);
  }


  /**
   * Tests that we can read parabuild.vmoptions and parabuild.sh
   * that have low memory options and identify that they actually do.
   */
  public void test_findMX() throws Exception {
    assertTrue(findMx("test-parabuild.vmoptions"));
    assertTrue(findMx("test-parabuild.sh"));
  }


  private boolean findMx(String fileName) throws IOException {
    final File file = TestHelper.getTestFile(fileName);

    // Read into string
    final StringBuffer sb = new StringBuffer(300);
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      String ln = br.readLine();
      while (ln != null) {
        sb.append(ln).append('\n');
        ln = br.readLine();
      }
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException ignore) {
        }
      }
    }

    // Find matchs
    final Pattern pattern = Pattern.compile("-Xmx([0-9]+)m", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(sb);
    while (matcher.find()) {
      // add to set of key to look up in the DB
      final String maxHeapMegabytes = matcher.group(1);
      final int heap = Integer.parseInt(maxHeapMegabytes);
      if (heap < 300) {
        return true;
      }
    }

    // Did not find the mx don't install.
    return false;
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestUsingRegexToFindLowMemoryFile.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}