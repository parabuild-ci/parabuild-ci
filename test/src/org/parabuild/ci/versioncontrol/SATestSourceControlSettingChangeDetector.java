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
package org.parabuild.ci.versioncontrol;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.object.SourceControlSetting;

import java.util.HashMap;
import java.util.Map;


public class SATestSourceControlSettingChangeDetector extends TestCase {

  private static final String TEST_NAME = VersionControlSystem.CVS_REPOSITORY_PATH;
  private static final String TEST_VALUE = "test";

  private SourceControlSettingChangeDetector changeDetector = null;
  private Map currentSettings = null;
  private Map newSettings = null;


  public void test_detectNullCurrent() {
    final SourceControlSetting scs = makeTestSetting();
    newSettings.put(scs.getPropertyName(), scs);
    assertTrue(changeDetector.settingHasChanged(TEST_NAME));
  }


  public void test_detectNullNew() {
    final SourceControlSetting scs = makeTestSetting();
    currentSettings.put(scs.getPropertyName(), scs);
    assertTrue(changeDetector.settingHasChanged(TEST_NAME));
  }


  public void test_detectBothNull() {
    assertTrue(!changeDetector.settingHasChanged(TEST_NAME));
  }


  public void test_detectValueChange() {
    final SourceControlSetting scsCurr = makeTestSetting();
    currentSettings.put(scsCurr.getPropertyName(), scsCurr);

    final SourceControlSetting scsNew = makeTestSetting();
    scsNew.setPropertyValue(String.valueOf(System.currentTimeMillis()));
    scsNew.setPropertyTimeStamp(scsNew.getPropertyTimeStamp() + 1);
    currentSettings.put(scsNew.getPropertyName(), scsNew);

    assertTrue(changeDetector.settingHasChanged(TEST_NAME));
  }


  public void test_doesNotDetectOnlyTimeStampChange() {
    final SourceControlSetting scsCurr = makeTestSetting();
    currentSettings.put(scsCurr.getPropertyName(), scsCurr);

    final SourceControlSetting scsNew = makeTestSetting();
    scsNew.setPropertyTimeStamp(scsNew.getPropertyTimeStamp() + 1);
    currentSettings.put(scsNew.getPropertyName(), scsNew);

    assertTrue(changeDetector.settingHasChanged(TEST_NAME));
  }


  private SourceControlSetting makeTestSetting() {
    final SourceControlSetting scs = new SourceControlSetting();
    scs.setPropertyName(TEST_NAME);
    scs.setPropertyValue(TEST_VALUE);
    return scs;
  }


  protected void setUp() throws Exception {
    currentSettings = new HashMap(5);
    newSettings = new HashMap(5);
    changeDetector = new SourceControlSettingChangeDetector(currentSettings, newSettings);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestSourceControlSettingChangeDetector.class);
  }


  public SATestSourceControlSettingChangeDetector(final String s) {
    super(s);
  }
}
