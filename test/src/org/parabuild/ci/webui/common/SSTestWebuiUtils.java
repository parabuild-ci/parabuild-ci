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
package org.parabuild.ci.webui.common;

import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;

/**
 */
public final class SSTestWebuiUtils extends ServletTestCase {

  public void test_makeBuildResultURL() {
    final String buildManagerHostAndPort = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
    final String url = WebuiUtils.makeBuildResultURL(1, 1, "test_entry_name");
    assertEquals(buildManagerHostAndPort + "/parabuild/build/result/1/1/test_entry_name", url);
  }


  public void test_makeBuildResultURLBug1392() {
    final String buildManagerHostAndPort = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
    final String url = WebuiUtils.makeBuildResultURL(1, 1, "gcc_bin_1MB+.dwl");
    assertEquals(buildManagerHostAndPort + "/parabuild/build/result/1/1/gcc_bin_1MB%2B.dwl", url);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestWebuiUtils.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SSTestWebuiUtils(final String s) {
    super(s);
  }
}
