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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.IoUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 */
public class SATestAutoBuildBat extends TestCase {

  /**
   * Tests that startup scripts does not contain malformed paths.
   */
  public void test_parabuildShContent() throws Exception {
    BufferedReader in = null;
    try {
      final File script = new File(TestHelper.getProductDir(), "bin/parabuild.bat");
      in = new BufferedReader(new InputStreamReader(new FileInputStream(script)));
      String line = in.readLine();
      while (line != null) {
        assertTrue(!line.startsWith("PARABUILD")); // ensures nothing in the script start with PARABUILD
        line = in.readLine();
      }
    } finally {
      IoUtils.closeHard(in);
    }
  }


  /**
   * @param name
   */
  public SATestAutoBuildBat(final String name) {
    super(name);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestAutoBuildBat.class);
  }
}
