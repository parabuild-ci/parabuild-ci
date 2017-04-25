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
package org.parabuild.ci.object;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class SourceControlSettingVO {

  public static final String SVN_DEPOT_PATH = SourceControlSetting.SVN_DEPOT_PATH;
  public static final String SVN_CHANGE_LIST_NUMBER = SourceControlSetting.SVN_CHANGE_LIST_NUMBER;

  public static final String CVS_BRANCH_NAME = SourceControlSetting.CVS_BRANCH_NAME;

  public static final String P4_DEPOT_PATH = SourceControlSetting.P4_DEPOT_PATH;
  public static final String P4_CHANGE_LIST_NUMBER = SourceControlSetting.P4_CHANGE_LIST_NUMBER;


  public static final String BAZAAR_BRANCH_LOCATION = SourceControlSetting.BAZAAR_BRANCH_LOCATION;
  public static final String BAZAAR_REVISION_NUMBER = SourceControlSetting.BAZAAR_REVISION_NUMBER;

  public static final String MERCURIAL_BRANCH = SourceControlSetting.MERCURIAL_BRANCH;
  public static final String MERCURIAL_REVISION_NUMBER = SourceControlSetting.MERCURIAL_REVISION_NUMBER;
  public static final String MERCURIAL_URL_PATH = SourceControlSetting.MERCURIAL_URL;

  /**
   * A quick lookup map.
   *
   * @see #scmSettingIsSupported(String)
   */
  private static final Map SUPPORTED_SCM_SETTINGS = makeSupportedSCMSettingsMap(
          new String[]{
                  SVN_DEPOT_PATH, SVN_CHANGE_LIST_NUMBER, CVS_BRANCH_NAME,
                  P4_DEPOT_PATH, P4_CHANGE_LIST_NUMBER, BAZAAR_BRANCH_LOCATION,
                  BAZAAR_REVISION_NUMBER, MERCURIAL_REVISION_NUMBER,
                  MERCURIAL_URL_PATH, MERCURIAL_BRANCH
          });


  private String name = null;
  private String value = null;
  private boolean nativeChangeListNumber = false;


  public SourceControlSettingVO() {
  }


  public SourceControlSettingVO(final String name, final String value) {
    this.name = name;
    this.value = value;
  }


  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
    this.nativeChangeListNumber = isNativeChangeList(name);
  }


  public String getValue() {
    return value;
  }


  public void setValue(final String value) {
    this.value = value;
  }


  /**
   * Helper method.
   *
   * @param settingNames - an array of String setting names.
   * @return unmodifieable map.
   * @see #SUPPORTED_SCM_SETTINGS
   */
  private static Map makeSupportedSCMSettingsMap(final String[] settingNames) {
    final Map result = new HashMap(3);
    for (int i = 0; i < settingNames.length; i++) {
      result.put(settingNames[i], Boolean.TRUE); // TRUE is used as a placeholder
    }
    return Collections.unmodifiableMap(result);
  }


  /**
   * @param propertyName property name.
   * @return true if a given SCM setting is supported.
   */
  public static boolean scmSettingIsSupported(final String propertyName) {
    return SUPPORTED_SCM_SETTINGS.containsKey(propertyName);
  }


  /**
   * @param propertyName property name.
   * @return true if a given SCM setting is supported.
   */
  private static boolean isNativeChangeList(final String propertyName) {
    return propertyName.equals(SVN_CHANGE_LIST_NUMBER)
            || propertyName.equals(P4_CHANGE_LIST_NUMBER)
            || propertyName.equals(BAZAAR_REVISION_NUMBER);
  }


  /**
   * @return true if value of this property is native change list number
   */
  public boolean isNativeChangeListNumber() {
    return nativeChangeListNumber;
  }


  public String toString() {
    return "SourceControlSettingVO{" +
            "name='" + name + '\'' +
            ", value='" + value + '\'' +
            ", nativeChangeListNumber='" + nativeChangeListNumber + '\'' +
            '}';
  }
}
