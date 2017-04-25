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
package org.parabuild.ci.object;

import junit.framework.TestCase;
import junit.framework.TestSuite;


public final class SATestBuildConfig extends TestCase {

  private BuildConfig buildConfig = null;

  public static final String TEST_BUILD_NAME = "Test Build";
  public static final int TEST_BUILD_ID = 1000;


  public void testSetGetBuildName() {
    buildConfig.setBuildName(TEST_BUILD_NAME);
    assertEquals(TEST_BUILD_NAME, buildConfig.getBuildName());
  }


  public void testSetGetBuildID() {
    buildConfig.setBuildID(TEST_BUILD_ID);
    assertEquals(TEST_BUILD_ID, buildConfig.getBuildID());
  }


  protected void setUp() throws Exception {
    buildConfig = new BuildConfig();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestBuildConfig.class);
  }


  public SATestBuildConfig(final String s) {
    super(s);
  }
}
