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
 * @author Kostya
 *
 */
public class SATestAutoBuildSh extends TestCase {

  /**
   * Tests that startup scripts does not contain malformed paths.
   */
  public void test_parabuildShContent() throws Exception {
    final String[] sarFile = new String[]{"bin/parabuild", "bin/parabuild.sh"};
    final String[] sarCont = new String[]{"$PARABUILD_HOME/startup.sh", "$PARABUILD_HOME/shutdown.sh"};

    for (int ix = 0; ix < sarFile.length; ix++) {
      BufferedReader in = null;
      try {
        in =
          new BufferedReader(
            new InputStreamReader(new FileInputStream(new File(TestHelper.getProductDir(), sarFile[ix])), "8859_1"));
        while (true) {
          // Get next line
          final String line = in.readLine();
          if (line == null)
            break;

          for (int iy = 0; iy < sarCont.length; iy++) {
            if (line.indexOf(sarCont[iy]) >= 0) {
              fail(
                "Broken content found: "
                + sarCont[iy]
                + " in file: "
                + sarFile[ix]);
            }
          }
        }
      } finally {
        IoUtils.closeHard(in);
      }
    }
  }


  /**
   * @param arg0
   */
  public SATestAutoBuildSh(final String arg0) {
    super(arg0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestAutoBuildSh.class);
  }


  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

}
