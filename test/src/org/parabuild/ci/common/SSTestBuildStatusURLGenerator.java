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

import java.net.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

/**
 * Tests BuildStatusURLGenerator
 */
public class SSTestBuildStatusURLGenerator extends ServersideTestCase {

  private BuildStatusURLGenerator generator = null;


  public SSTestBuildStatusURLGenerator(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_makeBuildStatusURL() throws Exception {
    final String generatedURL = generator.makeBuildStatusURL(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertTrue(generatedURL.length() > 0);
    TestHelper.assertPageSmokes(new URL(generatedURL), "cvs_test_build", true);
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
    generator = new BuildStatusURLGenerator();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestBuildStatusURLGenerator.class);
  }
}
