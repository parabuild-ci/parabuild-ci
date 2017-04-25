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

import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 *
 */
public class SSTestCleanCheckoutCounter extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestCleanCheckoutCounter.class);
  private CleanCheckoutCounter checkoutCounter = null;


  public SSTestCleanCheckoutCounter(final String s) {
    super(s);
  }


  public void test_createSelect() {
    for (int i = 0; i < 8; i++) checkoutCounter.increment();
    assertTrue("9-th should not be clean", !checkoutCounter.increment());
    assertTrue("10-th should be clean", checkoutCounter.increment());
  }


  public void test_bug714MarksCleanEveryTimeIfConfiguredEveryOne() {
    // alter
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ScheduleProperty scheduleSetting = cm.getScheduleSetting(TestHelper.TEST_CVS_VALID_BUILD_ID, ScheduleProperty.AUTO_CLEAN_CHECKOUT);
    scheduleSetting.setPropertyValue("1");
    cm.saveObject(scheduleSetting);

    // run couple of times
    assertTrue("Should be clean checkout but it was not", checkoutCounter.increment());
    assertTrue("Should be clean checkout but it was not", checkoutCounter.increment());
  }

  public void test_bug930ZeroSettingDisablesCleanCheckout() {
    // alter
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ScheduleProperty scheduleSetting = cm.getScheduleSetting(TestHelper.TEST_CVS_VALID_BUILD_ID, ScheduleProperty.AUTO_CLEAN_CHECKOUT);
    scheduleSetting.setPropertyValue("0");
    cm.saveObject(scheduleSetting);

    // run couple of times
    assertTrue("Should not be clean checkout but it was", !checkoutCounter.increment());
    assertTrue("Should not be clean checkout but it was", !checkoutCounter.increment());
  }

  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestCleanCheckoutCounter.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    checkoutCounter = new CleanCheckoutCounter(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }
}
