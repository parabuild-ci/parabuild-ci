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

import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;

/**
 *
 */
public class SSTestBuildRunAttribute extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestBuildRunAttribute.class);
  private static final int TEST_BUILD_RUN_ID = 999;


  /**
   */
  public void test_create() throws Exception {
    final BuildRunAttribute attr = new BuildRunAttribute(TEST_BUILD_RUN_ID, BuildRunAttribute.ATTR_CLEAN_CHECKOUT, true);
    assertEquals(TEST_BUILD_RUN_ID, attr.getBuildRunID());
    assertEquals(BuildRunAttribute.ATTR_CLEAN_CHECKOUT, attr.getName());
    assertEquals(true, Boolean.valueOf(attr.getValue()).booleanValue());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildRunAttribute.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SSTestBuildRunAttribute(final String s) {
    super(s);
  }
}
