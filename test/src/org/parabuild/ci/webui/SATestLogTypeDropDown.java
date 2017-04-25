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
public class SATestLogTypeDropDown extends TestCase {

  private LogTypeDropDown logTypeDD = null;


  public SATestLogTypeDropDown(final String s) {
    super(s);
  }


  /**
   */
  public void test_failsOnUnknownSCMCode() throws Exception {
    try {
      logTypeDD.setCode(Integer.MAX_VALUE);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  /**
   */
  public void test_acceptsTypes() throws Exception {
    assertAcceptsType(LogConfig.LOG_TYPE_TEXT_DIR);
    assertAcceptsType(LogConfig.LOG_TYPE_TEXT_FILE);
  }


  private void assertAcceptsType(final byte logType) {
    logTypeDD.setCode(logType);
    assertEquals(logTypeDD.getCode(), logType);
  }


  protected void setUp() throws Exception {
    super.setUp();
    logTypeDD = new LogTypeDropDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestLogTypeDropDown.class);
  }
}
