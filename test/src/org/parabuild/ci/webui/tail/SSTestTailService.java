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
package org.parabuild.ci.webui.tail;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.services.TailUpdate;
import org.parabuild.ci.services.TailUpdateImpl;

public final class SSTestTailService extends ServersideTestCase {

  private TailService tailService;


  public void test_create() {
    // created in setUp
  }


  public void test_getUpdate() {
    final TailUpdate update = tailService.getUpdate(TestHelper.TEST_CVS_VALID_BUILD_ID, 0);
    assertNotNull(update);
    assertEquals(TailUpdateImpl.EMPTY_UPDATE, update);
  }


  protected void setUp() throws Exception {
    super.setUp();
    tailService = new TailService();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestTailService.class);
  }


  public SSTestTailService(final String s) {
    super(s);
  }
}
