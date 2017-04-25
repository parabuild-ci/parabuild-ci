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

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.BuilderConfigurationManager;

/**
 *
 */
public class SSTestClusterManager extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestClusterManager.class); // NOPMD

  private BuilderConfigurationManager builderConfigurationManager;


  public SSTestClusterManager(final String s) {
    super(s);
  }


  public void test_createSelect() {
    assertEquals(false, builderConfigurationManager.builderMemberWithHostNameExists(99999999, "never_existing_host"));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestClusterManager.class, new String[]{
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    builderConfigurationManager = BuilderConfigurationManager.getInstance();
  }
}
