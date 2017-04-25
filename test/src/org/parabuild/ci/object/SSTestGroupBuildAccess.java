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
import org.parabuild.ci.security.SecurityManager;

/**
 *
 */
public class SSTestGroupBuildAccess extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestGroupBuildAccess.class);


  public SSTestGroupBuildAccess(final String s) {
    super(s);
  }


  /**
   * Tests that can save object created with given constrcutor param
   * set.
   */
  public void test_save() throws Exception {
    final int groupID = 2;
    final int buildID = 6;
    SecurityManager.getInstance().save(new GroupBuildAccess(groupID, buildID));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestGroupBuildAccess.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
