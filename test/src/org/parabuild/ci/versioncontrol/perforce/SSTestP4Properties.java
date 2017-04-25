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
package org.parabuild.ci.versioncontrol.perforce;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.SourceControlSetting;

/**
 *
 */
public class SSTestP4Properties extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestP4Properties.class);

  private P4Properties props = null;
  private ConfigurationManager cm = null;
  public static final int TEST_P_4_VALID_BUILD_ID = TestHelper.TEST_P4_VALID_BUILD_ID;


  public SSTestP4Properties(final String s) {
    super(s);
  }


  public void test_load() throws Exception {
    props.load(getPropertyMap());
    assertNotNull(props.getP4DepotPath());
    assertNotNull(props.getP4Exe());
    assertNotNull(props.getP4Port());
  }


  public void test_getUnsetProperty() throws Exception {
    final Map map = getPropertyMap();
    map.remove(SourceControlSetting.P4_PORT);
    props.load(map);
    try {
      props.getP4Port();
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
      // expected
    }
  }


  public void test_isAdvancedViewMode() throws Exception {
    final Map map = getPropertyMap();
    map.put(SourceControlSetting.P4_ADVANCED_VIEW_MODE, new SourceControlSetting(TEST_P_4_VALID_BUILD_ID, SourceControlSetting.P4_ADVANCED_VIEW_MODE, SourceControlSetting.OPTION_CHECKED));
    map.put(SourceControlSetting.P4_RELATIVE_BUILD_DIR, new SourceControlSetting(TEST_P_4_VALID_BUILD_ID, SourceControlSetting.P4_RELATIVE_BUILD_DIR, "test/test"));
    props.load(map);
    assertNotNull(props.getRelativeBuildDir());
    assertTrue(props.isAdvancedViewMode());
  }


  public void test_defaultOverride() throws Exception {
    assertTrue(!props.getP4VariablesOverride());
  }


  public void test_getP4VariablesOverride() throws Exception {
    final Map map = getPropertyMap();
    map.put(SourceControlSetting.P4_VARS_OVERRIDE, new SourceControlSetting(TEST_P_4_VALID_BUILD_ID, SourceControlSetting.P4_VARS_OVERRIDE, SourceControlSetting.OPTION_CHECKED));
    props.load(map);
    assertTrue(props.getP4VariablesOverride());
  }


  public void test_getModtimeOptionReturnsModtime() {
    final Map map = getPropertyMap();
    map.put(SourceControlSetting.P4_MODTIME_OPTION, new SourceControlSetting(TEST_P_4_VALID_BUILD_ID,
      SourceControlSetting.P4_MODTIME_OPTION, String.valueOf(SourceControlSetting.P4_OPTION_VALUE_MODTIME)));
    props.load(map);
    assertEquals("modtime", props.getModtimeOption());
  }


  public void test_getModtimeOptionReturnsNomodtime() {
    final Map map = getPropertyMap();
    map.put(SourceControlSetting.P4_MODTIME_OPTION, new SourceControlSetting(TEST_P_4_VALID_BUILD_ID,
      SourceControlSetting.P4_MODTIME_OPTION, String.valueOf(SourceControlSetting.P4_OPTION_VALUE_NOMODTIME)));
    props.load(map);
    assertEquals("nomodtime", props.getModtimeOption());
  }


  public void test_getClobberOptionReturnsClobber() {
    final Map map = getPropertyMap();
    map.put(SourceControlSetting.P4_CLOBBER_OPTION, new SourceControlSetting(TEST_P_4_VALID_BUILD_ID,
      SourceControlSetting.P4_CLOBBER_OPTION, String.valueOf(SourceControlSetting.P4_OPTION_VALUE_CLOBBER)));
    props.load(map);
    assertEquals("clobber", props.getClobberOption());
  }


  public void test_getClobberOptionReturnsNoclobber() {
    final Map map = getPropertyMap();
    map.put(SourceControlSetting.P4_CLOBBER_OPTION, new SourceControlSetting(TEST_P_4_VALID_BUILD_ID,
      SourceControlSetting.P4_CLOBBER_OPTION, String.valueOf(SourceControlSetting.P4_OPTION_VALUE_NOCLOBBER)));
    props.load(map);
    assertEquals("noclobber", props.getClobberOption());
  }


  /**
   * Helper method to get property map
   */
  private Map getPropertyMap() {
    return cm.getEffectiveSourceControlSettingsAsMap(TEST_P_4_VALID_BUILD_ID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestP4Properties.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    props = new P4Properties();
  }
}
