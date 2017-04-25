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

import org.parabuild.ci.common.IoUtils;

import java.io.File;

/**
 * Constants common for config subsystem.
 */
public final class ConfigurationConstants {

  public static final byte LDAP_CREDENTIAL_DIGEST_NOT_SELECTED = 0;
  public static final byte LDAP_CREDENTIAL_DIGEST_MD2 = 1;
  public static final byte LDAP_CREDENTIAL_DIGEST_MD5 = 2;
  public static final byte LDAP_CREDENTIAL_DIGEST_SHA1 = 3;

  public static final String LDAP_CREDENTIAL_DIGEST_NOT_SELECTED_VALUE = "Select";
  public static final String LDAP_CREDENTIAL_DIGEST_MD2_VALUE = "MD2";
  public static final String LDAP_CREDENTIAL_DIGEST_MD5_VALUE = "MD5";
  public static final String LDAP_CREDENTIAL_DIGEST_SHA1_VALUE = "SHA-1";

  public static final byte LDAP_VERSION_DEFAULT = 0;
  public static final byte LDAP_VERSION_TWO = 1;
  public static final byte LDAP_VERSION_THREE = 2;
  public static final String LDAP_VERSION_TWO_VALUE = "2";
  public static final String LDAP_VERSION_THREE_VALUE = "3";


  public static final byte LDAP_REFERRAL_DEFAULT = 0;
  public static final byte LDAP_REFERRAL_FOLLOW = 1;
  public static final byte LDAP_REFERRAL_IGNORE = 2;
  public static final byte LDAP_REFERRAL_THROW = 3;
  public static final String LDAP_REFERRAL_FOLLOW_VALUE = "follow";
  public static final String LDAP_REFERRAL_IGNORE_VALUE = "ignore";
  public static final String LDAP_REFERRAL_THROW_VALUE = "throw";


  public static final byte LDAP_USER_LOOKUP_BY_DN_TEMPLATE = 0;
  public static final byte LDAP_USER_LOOKUP_BY_SEARCH = 1;


  public static final int DEFAULT_DASHBOARD_ROW_SIZE = 10;
  public static final int DEFAULT_TAIL_WINDOW_SIZE = 30;
  public static final int TAIL_BUFFER_SIZE = 50;
  public static final int DEFAULT_MAX_RECENT_BUILD = 20;

//  static {
//    if (log.isDebugEnabled()) log.debug("System.getProperty(\"catalina.base\"): " + System.getProperty("catalina.base"));
//  }


  /**
   * Constants container constructor
   */
  private ConfigurationConstants() {
  }


  public static final String FS = File.separator;

  public static final String CATALINA_BASE = IoUtils.getCanonicalPathHard(System.getProperty("catalina.base"));
  public static final String CATALINA_HOME = IoUtils.getCanonicalPathHard(System.getProperty("catalina.home"));

  public static final File DATABASE_HOME = IoUtils.getCanonicalFileHard(new File(CATALINA_BASE, "data/parabuild"));
  public static final File INSTALL_HOME = IoUtils.getCanonicalFileHard(new File(CATALINA_BASE, ".."));
  public static final File INDEX_HOME = IoUtils.getCanonicalFileHard(new File(CATALINA_BASE, "index"));

  public static final int MAX_STEP_LOGS = 50; // defines maximum number of configured build logs for a given build
  public static final int MAX_BUILD_CONFIGS = 100; // defines maximum number of configured builds
  public static final int MAX_BUILD_STEPS = 100; // defines maximum number of configured builds

  public static final int DEFAULT_JABBER_PORT = 5222;
  public static final int DEFAULT_ERROR_LOG_QUOTE_SIZE = 50;
  public static final int DEFAULT_ERROR_LINE_QUOTE_LENGTH = 100;
  public static final int DEFAULT_CHANGE_LIST_DESCRIPTION_QUOTE_LENGTH = 60;

  public static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";
  public static final String DEFAULT_DATE_TIME_FORMAT = "HH:mm a MM/dd/yyyy";
  public static final String DEFAULT_SMTP_SERVER_PORT = Integer.toString(25);
  public static final String DEFAULT_SMTPS_SERVER_PORT = Integer.toString(465);
  public static final byte LDAP_CONNECTION_SECURITY_LEVEL_DEFAULT = 0;
  public static final byte LDAP_CONNECTION_SECURITY_LEVEL_NONE = 1;
  public static final byte LDAP_CONNECTION_SECURITY_LEVEL_SIMPLE = 2;
  public static final byte LDAP_CONNECTION_SECURITY_LEVEL_STRONG = 3;
  public static final String LDAP_CONNECTION_SECURITY_LEVEL_DEFAULT_VALUE = "default";
  public static final String LDAP_CONNECTION_SECURITY_LEVEL_NONE_VALUE = "none";
  public static final String LDAP_CONNECTION_SECURITY_LEVEL_SIMPLE_VALUE = "simple";
  public static final String LDAP_CONNECTION_SECURITY_LEVEL_STRONG_VALUE = "strong";
}
