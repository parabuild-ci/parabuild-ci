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
package org.parabuild.ci.remote.internal;

import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

/**
 * Tests LocalAgent
 */
public class SSTestLocalBuilder extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestLocalBuilder.class);
  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private LocalAgent localBuilder = null;

  public void test_create() {
    // create is done in setUp method
  }

  public void test_bug1066_canDeleteLongPaths() throws Exception {
    // REVIEWME: simeshev@parabuilci.org -> last time I tried creating a long directory
    // under Windows it toom me 2 hours to clean up. Ended up
    // moving it from temp to C: Theoretically it should
    // become shorter in length and could be deleted.
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestLocalBuilder.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    localBuilder = new LocalAgent(TestHelper.TEST_CVS_VALID_BUILD_ID, null);
  }


  public SSTestLocalBuilder(final String s) {
    super(s);
  }
}
