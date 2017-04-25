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
package org.parabuild.ci.webui.admin.usermanagement;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;

/**
 * Tests SSTestUserCommandsFlow
 */
public class SSTestUserCommandsFlow extends ServersideTestCase {

  private static final int TEST_ADMIN_USER_ID = 1;


  public SSTestUserCommandsFlow(final String s) {
    super(s);
  }


  /**
   */
  public void test_create() throws Exception {
    //noinspection UNUSED_SYMBOL
    final UserCommandsFlow userCommandsFlow = new UserCommandsFlow(TEST_ADMIN_USER_ID);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestUserCommandsFlow.class);
  }
}
