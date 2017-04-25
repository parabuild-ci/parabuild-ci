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

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 * Tests home page
 */
public class SSTestScheduleTypeDropDown extends ServersideTestCase {

  private ScheduleTypeDropDown scheduleTypeDropDown = null;


  public SSTestScheduleTypeDropDown(final String s) {
    super(s);
  }


  /**
   */
  public void test_defaultSelection() throws Exception {
    assertEquals(BuildConfig.SCHEDULE_TYPE_AUTOMATIC, scheduleTypeDropDown.getScheduleType());
  }


  /**
   */
  public void test_setScheduleType() throws Exception {
    setAndAssertScheduleTypeIsSet(BuildConfig.SCHEDULE_TYPE_AUTOMATIC);
    setAndAssertScheduleTypeIsSet(BuildConfig.SCHEDULE_TYPE_RECURRENT);
    setAndAssertScheduleTypeIsSet(BuildConfig.SCHEDULE_TYPE_PARALLEL);
  }


  private void setAndAssertScheduleTypeIsSet(final byte access) {
    scheduleTypeDropDown.setScheduleType(access);
    assertEquals(access, scheduleTypeDropDown.getScheduleType());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestScheduleTypeDropDown.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    scheduleTypeDropDown = new ScheduleTypeDropDown();
  }
}
