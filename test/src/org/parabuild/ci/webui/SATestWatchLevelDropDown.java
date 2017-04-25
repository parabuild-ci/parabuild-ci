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
package org.parabuild.ci.webui;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 */
public class SATestWatchLevelDropDown extends TestCase {

  private WatchLevelDropDown vcdd = null;


  public SATestWatchLevelDropDown(final String s) {
    super(s);
  }


  /**
   */
  public void test_failsOnUnknownCode() throws Exception {
    try {
      vcdd.setCode(Integer.MAX_VALUE);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  /**
   */
  public void test_acceptsRegisteredType() throws Exception {
    vcdd.setCode(BuildWatcher.LEVEL_SUCCESS);
    assertEquals(vcdd.getCode(), BuildWatcher.LEVEL_SUCCESS);
  }


  protected void setUp() throws Exception {
    super.setUp();
    vcdd = new WatchLevelDropDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestWatchLevelDropDown.class);
  }
}
