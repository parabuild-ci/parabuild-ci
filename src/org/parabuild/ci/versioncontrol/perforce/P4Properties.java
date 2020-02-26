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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.security.SecurityManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Typed P4 properties
 */
public final class P4Properties implements Serializable {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(P4Properties.class); // NOPMD
  private static final long serialVersionUID = 3957929102069199564L; // NOPMD

  private static final byte DEFAULT_BYTE_MODTIME_OPTION_VALUE = SourceControlSetting.P4_OPTION_VALUE_NOMODTIME;
  private static final byte DEFAULT_BYTE_CLOBBER_OPTION_VALUE = SourceControlSetting.P4_OPTION_VALUE_NOCLOBBER;

  private static final String NOMODTIME = "nomodtime";
  private static final String MODTIME = "modtime";
  private static final String NOCLOBBER = "noclobber";
  private static final String CLOBBER = "clobber";

  /**
   * Map of {@link String} elements that contains Perforce
   * properties.
   */
  private final Map settings = new HashMap(11);


  /**
   * Default constructor.
   */
  public P4Properties() {
  }


  /**
   * Copy constructor.
   *
   * @param properties
   */
  public P4Properties(final P4Properties properties) {
    this.settings.putAll(properties.settings);
  }


  /**
   * Loads properties
   *
   * @param settings
   */
  public void load(final Map settings) {
    this.settings.clear();
    this.settings.putAll(settings);
  }


  /**
   * Returns P4 executable path
   */
  public String getP4Exe() {
    return StringUtils.putIntoDoubleQuotes(getMandatoryProperty(SourceControlSetting.P4_PATH_TO_CLIENT));
  }


  /**
   * Returns P4PORT
   */
  public String getP4Port() {
    return getMandatoryProperty(SourceControlSetting.P4_PORT);
  }


  /**
   * Returns P4 depot path
   */
  public String getP4DepotPath() {
    return getProperty(SourceControlSetting.P4_DEPOT_PATH, null);
  }


  /**
   * Returns P4 user
   */
  public String getP4User() {
    return getMandatoryProperty(SourceControlSetting.P4_USER);
  }


  /**
   * Returns P4 client name template
   */
  public String getClientNameTemplate() {
    return getProperty(SourceControlSetting.P4_CLIENT_NAME_TEMPLATE, "parabuild_on_${builder.host}_${build.id}");
  }


  /**
   * Returns P4 client name template
   */
  public String getDepotSourceClientNameTemplate() {
    return "parabuild_depot_view_on_${builder.host}_${build.id}";
  }


  /**
   * Returns P4 password
   */
  public String getP4Password() {
    final String encryptedPassword = getMandatoryProperty(SourceControlSetting.P4_PASSWORD);
    return SecurityManager.decryptPassword(encryptedPassword);
  }


  /**
   * Returns P4 counter or null if undefined.
   */
  public String getP4Counter() {
    return getProperty(SourceControlSetting.P4_COUNTER, null);
  }


  /**
   * Returns P4 variables override.
   */
  public boolean getP4VariablesOverride() {
    return getProperty(SourceControlSetting.P4_VARS_OVERRIDE, SourceControlSetting.OPTION_UNCHECKED)
            .equals(SourceControlSetting.OPTION_CHECKED);
  }


  /**
   * @return true if counter is defined
   */
  public boolean isCounterDefined() {
    final SourceControlSetting scs = (SourceControlSetting) settings.get(SourceControlSetting.P4_COUNTER);
    return scs != null && !StringUtils.isBlank(scs.getPropertyValue());
  }


  /**
   * @return relative build dir
   */
  public String getRelativeBuildDir() {
    return getProperty(SourceControlSetting.P4_RELATIVE_BUILD_DIR, null);
  }


  /**
   * @return relative build dir
   */
  public String getExclusionPaths() {
    return getProperty(SourceControlSetting.VCS_EXCLUSION_PATHS, "");
  }


  /**
   * @return true if in advanced view mode.
   */
  public boolean isAdvancedViewMode() {
    return getProperty(SourceControlSetting.P4_ADVANCED_VIEW_MODE, SourceControlSetting.OPTION_UNCHECKED)
            .equals(SourceControlSetting.OPTION_CHECKED);
  }


  /**
   * @return true if should use UNC paths.
   */
  public boolean isUseUNCPaths() {
    return getProperty(SourceControlSetting.P4_USE_UNC_PATHS, SourceControlSetting.OPTION_UNCHECKED)
            .equals(SourceControlSetting.OPTION_CHECKED);
  }


  /**
   * Helper method to return String property value by it's name
   * from the internal map.
   */
  private String getMandatoryProperty(final String name) {
    final String result = getProperty(name, null);
    if (result == null) {
      throw new IllegalStateException("P4 setting \"" + p4SettingNameToUserFriendlyString(name) + "\" is not defined.");
    }
    return result;
  }


  /**
   * Helper method to get a SourceControlSetting from the wrapped
   * properties.
   *
   * @param propName     name of the property.
   * @param defaultValue default value if not found.
   * @return SourceControlSetting from the wrapped properties or
   *         default value if not found.
   */
  private String getProperty(final String propName, final String defaultValue) {
    final SourceControlSetting scs = (SourceControlSetting) settings.get(propName);
    if (scs != null && !StringUtils.isBlank(scs.getPropertyValue())) {
      return scs.getPropertyValue();
    }
    return defaultValue;
  }


  private static String p4SettingNameToUserFriendlyString(final String name) {
    if (name.equals(SourceControlSetting.P4_CLIENT)) {
      return "P4CLIENT";
    }
    if (name.equals(SourceControlSetting.P4_DEPOT_PATH)) {
      return "Depot path";
    }
    if (name.equals(SourceControlSetting.P4_PASSWORD)) {
      return "P4PASSWD";
    }
    if (name.equals(SourceControlSetting.P4_PATH_TO_CLIENT)) {
      return "Path to client";
    }
    if (name.equals(SourceControlSetting.P4_PORT)) {
      return "P4PORT";
    }
    if (name.equals(SourceControlSetting.P4_USER)) {
      return "P4USER";
    }
    return name;
  }


  /**
   * @return value of configured modtime option.
   */
  public String getModtimeOption() {
    final String strValue = getProperty(SourceControlSetting.P4_MODTIME_OPTION, Byte.toString(DEFAULT_BYTE_MODTIME_OPTION_VALUE));
    final byte code = StringUtils.isValidInteger(strValue) ? Byte.parseByte(strValue) : DEFAULT_BYTE_MODTIME_OPTION_VALUE;
    if (code == SourceControlSetting.P4_OPTION_VALUE_NOMODTIME) {
      return NOMODTIME;
    } else if (code == SourceControlSetting.P4_OPTION_VALUE_MODTIME) {
      return MODTIME;
    } else {
      return NOMODTIME; // default is "nomodtime" if value cnnot be recognized
    }
  }


  /**
   * @return value of configured modtime option.
   */
  public String getClobberOption() {
    final String strValue = getProperty(SourceControlSetting.P4_CLOBBER_OPTION, Byte.toString(DEFAULT_BYTE_CLOBBER_OPTION_VALUE));
    final byte code = StringUtils.isValidInteger(strValue) ? Byte.parseByte(strValue) : DEFAULT_BYTE_CLOBBER_OPTION_VALUE;
    if (code == SourceControlSetting.P4_OPTION_VALUE_NOCLOBBER) {
      return NOCLOBBER;
    } else if (code == SourceControlSetting.P4_OPTION_VALUE_CLOBBER) {
      return CLOBBER;
    } else {
      return NOCLOBBER; // default is "noclobber" if value cannot be recognized
    }
  }


  /**
   * @return value of configured modtime option.
   */
  public byte getAuthenticationMode() {
    return Byte.parseByte(getProperty(SourceControlSetting.P4_AUTHENTICATION_MODE, Byte.toString(SourceControlSetting.P4_AUTHENTICATION_MODE_VALUE_P4PASSWD)));
  }


  public String getClientViewByDepotPath() {
    return getProperty(SourceControlSetting.P4_CLIENT_VIEW_BY_DEPOT_PATH, null);
  }


  public String getClientViewByWorkspaceName() {
    return getProperty(SourceControlSetting.P4_CLIENT_VIEW_BY_CLIENT_NAME, null);
  }


  public byte getClientViewSource() {
    return Byte.parseByte(getProperty(SourceControlSetting.P4_CLIENT_VIEW_SOURCE, Byte.toString(SourceControlSetting.P4_CLIENT_VIEW_SOURCE_VALUE_FIELD)));
  }


  /**
   * @return true if should use UNC paths.
   */
  public boolean updateHaveList() {
    return getProperty(SourceControlSetting.P4_UPDATE_HAVE_LIST, SourceControlSetting.OPTION_CHECKED)
            .equals(SourceControlSetting.OPTION_CHECKED);
  }


  /**
   * @return true if should use case-sensitive user names.
   */
  public boolean caseSensitiveUserNames() {
    return getProperty(SourceControlSetting.P4_CASE_SENSITIVE_USER_NAMES, SourceControlSetting.OPTION_CHECKED)
            .equals(SourceControlSetting.OPTION_CHECKED);
  }


  public boolean isDoNotSync() {
    return getProperty(SourceControlSetting.DO_NOT_CHECKOUT, SourceControlSetting.OPTION_UNCHECKED)
            .equals(SourceControlSetting.OPTION_CHECKED);
  }


  public String getLineEnd() {

    final byte lineEndCode = Byte.parseByte(getProperty(SourceControlSetting.P4_LINE_END, Byte.toString(SourceControlSetting.P4_LINE_END_LOCAL)));
    switch(lineEndCode) {
      case SourceControlSetting.P4_LINE_END_LOCAL:
        return SourceControlSetting.P4_LINE_END_VALUE_LOCAL;
      case SourceControlSetting.P4_LINE_END_MAC:
        return SourceControlSetting.P4_LINE_END_VALUE_MAC;
      case SourceControlSetting.P4_LINE_END_SHARE:
        return SourceControlSetting.P4_LINE_END_VALUE_SHARE;
      case SourceControlSetting.P4_LINE_END_UNIX:
        return SourceControlSetting.P4_LINE_END_VALUE_UNIX;
      default:
        throw new IllegalArgumentException("Uknown line end code: " + lineEndCode);
    }
  }


  public String toString() {
    return "P4Properties{" +
            "settings=" + settings +
            '}';
  }
}
