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
package org.parabuild.ci.configuration;

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.SystemProperty;

public class SSTestSystemConfigurationManagerImpl extends ServersideTestCase {

  private SystemConfigurationManagerImpl systemCM;


  public void test_timeIsInScheduleGap() {
    TestHelper.setSystemProperty(SystemProperty.ENABLE_SCHEDULE_GAP, SystemProperty.OPTION_CHECKED);
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_FROM, Integer.toString(1));
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_TO, Integer.toString(2));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 0, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 1, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 2, 0, 0)));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 3, 0, 0)));
  }


  public void test_disabledScheduleGap() {
    TestHelper.setSystemProperty(SystemProperty.ENABLE_SCHEDULE_GAP, SystemProperty.OPTION_UNCHECKED);
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_FROM, Integer.toString(2));
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_TO, Integer.toString(1));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 0, 0, 0)));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 1, 0, 0)));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 2, 0, 0)));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 3, 0, 0)));
  }


  public void test_bug_885_InterDayscheduleGap() {
    TestHelper.setSystemProperty(SystemProperty.ENABLE_SCHEDULE_GAP, SystemProperty.OPTION_CHECKED);
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_FROM, Integer.toString(22));
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_TO, Integer.toString(6));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 21, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 22, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 23, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 0, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 5, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 6, 0, 0)));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 7, 0, 0)));

    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_FROM, Integer.toString(22));
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_TO, Integer.toString(0));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 21, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 22, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 23, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 0, 0, 0)));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 1, 0, 0)));

    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_FROM, Integer.toString(3));
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_TO, Integer.toString(1));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 1, 0, 0)));
    assertTrue(!systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 2, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 3, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 4, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 0, 0, 0)));
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 13, 0, 0)));
  }


  public void test_leadingZeroScheduleGap() {
    TestHelper.setSystemProperty(SystemProperty.ENABLE_SCHEDULE_GAP, SystemProperty.OPTION_CHECKED);
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_FROM, "08");
    TestHelper.setSystemProperty(SystemProperty.SCHEDULE_GAP_TO, "09");
    assertTrue(systemCM.isTimeInScheduleGap(TestHelper.makeDate(2006, 4, 9, 8, 0, 0)));
  }


  public void test_getLDAPSearchEntireSubtree() {
    TestHelper.setSystemProperty(SystemProperty.LDAP_SEARCH_ENTIRE_SUBTREE, SystemProperty.OPTION_CHECKED);
    assertTrue(systemCM.getLDAPSearchEntireSubtree());
  }


  public void test_getLDAPUseCredentialsDigest() {
    TestHelper.setSystemProperty(SystemProperty.LDAP_USE_CREDENTIALS_DIGEST, SystemProperty.OPTION_CHECKED);
    assertTrue(systemCM.getLDAPUseCredentialsDigest());
  }


  public void test_incrementBuildSequenceNumber() {
    assertTrue(systemCM.incrementBuildSequenceNumber() != systemCM.incrementBuildSequenceNumber());
  }


  public void test_getDashboardRowSize() {
    assertEquals(ConfigurationConstants.DEFAULT_DASHBOARD_ROW_SIZE, systemCM.getDashboardRowSize());
  }


  protected void setUp() throws Exception {
    super.setUp();

    systemCM = new SystemConfigurationManagerImpl();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestSystemConfigurationManagerImpl.class);
  }


  public SSTestSystemConfigurationManagerImpl(final String s) {
    super(s);
  }
}
