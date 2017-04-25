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

import org.parabuild.ci.webui.admin.FishEyeSettingsPanel;
import org.parabuild.ci.webui.admin.WebSVNSettingsPanel;
import viewtier.util.StringUtils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Stored build configuration
 *
 * @hibernate.class table="SOURCE_CONTROL_PROPERTY"
 * dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class SourceControlSetting implements Serializable, ObjectConstants {

  /**
   * This comparator is used to sort SourceControlSetting lists
   * in direct property name order.
   */
  public static final Comparator PROPERTY_NAME_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      final SourceControlSetting c1 = (SourceControlSetting) o1;
      final SourceControlSetting c2 = (SourceControlSetting) o2;
      //noinspection ObjectEquality
      if (c1 == c2) {
        return 0; // NOPMD
      }
      return c1.getPropertyName().compareTo(c2.getPropertyName());
    }
  };


  private static final long serialVersionUID = -4844436413027227167L; // NOPMD

  // possible SC properties for CVS
  public static final String CVS_ROOT = "cvs.root";
  public static final String CVS_PASSWORD = "cvs.password";
  public static final String CVS_PATH_TO_RSH = "cvs.path.to.rsh";
  public static final String CVS_PATH_TO_PRIVATE_KEY = "cvs.path.to.private.key";
  public static final String CVS_PATH_TO_CLIENT = "cvs.path.to.client";
  public static final String CVS_REPOSITORY_PATH = "cvs.source.line.path";
  public static final String CVS_BRANCH_NAME = "cvs.branch.name";
  public static final String CVS_CHANGE_WINDOW = "cvs.change.window";
  public static final String CVS_CHANGE_PRECHECK = "cvs.change.precheck";
  public static final String CVS_COMPRESSION = "cvs.compression";
  public static final String VIEWCVS_URL = "cvs.viewcvs.url";
  public static final String VIEWCVS_ROOT = "cvs.viewcvs.root";
  public static final String GITHUB_URL = "github.url";
  public static final String CVS_CUSTOM_RELATIVE_BUILD_DIR = "cvs.relative.build.dir";
  public static final String CVS_SUPPRESS_LOG_OUTPUT_IF_NO_CHANGES = "cvs.suppress.log.output.if.no.changes";

  // possible SC properties for P4
  public static final String P4_ADVANCED_VIEW_MODE = "p4.advanced.view.mode";
  public static final String P4_AUTHENTICATION_MODE = "p4.authentication.mode";
  public static final String P4_CLIENT = "p4.client";
  public static final String P4_CLIENT_NAME_TEMPLATE = "p4.client.name.template";
  public static final String P4_CLIENT_VIEW_BY_DEPOT_PATH = "p4.client.view.by.depot.path";
  public static final String P4_CLIENT_VIEW_BY_CLIENT_NAME = "p4.client.view.by.client.name";
  public static final String P4_CLIENT_VIEW_SOURCE = "p4.client.view.source";
  public static final String P4_CLOBBER_OPTION = "p4.option.clobber";
  public static final String P4_COUNTER = "p4.counter";
  public static final String P4_DEPOT_PATH = "p4.depot.path";
  public static final String P4_DEPOT_PATH_PART_PREFIX = P4_DEPOT_PATH + ".part";
  public static final String P4_MODTIME_OPTION = "p4.option.modtime";
  public static final String P4_P4WEB_URL = "p4.p4web.url";
  public static final String P4_PASSWORD = "p4.password";
  public static final String P4_PATH_TO_CLIENT = "p4.path.to.client";
  public static final String P4_PORT = "p4.port";
  public static final String P4_RELATIVE_BUILD_DIR = "p4.custom.rel.build.dir";
  public static final String P4_UPDATE_HAVE_LIST = "p4.update.have.list";
  public static final String P4_USE_UNC_PATHS = "p4.use.unc.paths";
  public static final String P4_USER = "p4.user";
  public static final String P4_VARS_OVERRIDE = "p4.variables.override";
  public static final String P4_CASE_SENSITIVE_USER_NAMES = "p4.case.sensitive.user.names";
  public static final String P4_LINE_END = "p4.line.end";

  /**
   * Native change list number user to start build request.
   */
  public static final String P4_CHANGE_LIST_NUMBER = "p4.change.list.number";

  /**
   * Defines where Parabuild gets depot view.
   */
  public static final String P4_DEPOT_VIEW_MODE = "p4.depot.view.mode";

  /**
   * Keeps path to a file in the depot.
   */
  public static final String P4_DEPOT_VIEW_FILE_PATH = "p4.depot.view.file.path";

  /**
   * Keeps template client name.
   */
  public static final String P4_DEPOT_VIEW_TEMPLATE_CLIENT = "p4.depot.view.template.client";

  /**
   * Depot view is entered from UI.
   */
  public static final int P4_DEPOT_VIEW_MODE_DIRECT = 0;

  /**
   * Depot view is fetched from a file in depot
   */
  public static final int P4_DEPOT_VIEW_MODE_DEPOT_FILE = 1;

  /**
   * Depot view is fetched from a template client
   */
  public static final int P4_DEPOT_VIEW_MODE_DEPOT_CLIENT = 2;


  // possible SC properties for VSS
  public static final String VSS_BRANCH_NAME = "vss.branch.name";
  public static final String VSS_CHANGE_WINDOW = "vss.change.window";
  public static final String VSS_DATABASE_PATH = "vss.db.path";
  public static final String VSS_PASSWORD = "vss.password";
  public static final String VSS_EXE_PATH = "vss.path.to.client";
  public static final String VSS_PROJECT_PATH = "vss.project.path";
  public static final String VSS_READONLY_CHECKOUT = "vss.readonly.checkout";
  public static final String VSS_USER = "vss.user";

  // possible SC properties for SVN (Subversion)
  public static final String SVN_PASSWORD = "svn.password";
  public static final String SVN_USER = "svn.user";
  public static final String SVN_DEPOT_PATH = "svn.depot.path";
  public static final String SVN_PATH_TO_EXE = "svn.exe.path";
  public static final String SVN_URL = "svn.url";
  /**
   * Native change list number user to start build request.
   */
  public static final String SVN_CHANGE_LIST_NUMBER = "svn.change.list.number";

  /**
   * If checked, means that builds will start on changes detected for non-recursive path and under.
   */
  public static final String SVN_WATCH_NON_RECURSIVE_PATHS = "svn.watch.nonrecursive.paths";


  /**
   * Adds option '"--trust-server-cert" to Subversion integration.
   */
  public static final String SVN_ADD_OPTION_TRUST_SERVER_CERT = "svn.add.option.trust.server.cert";


  /**
   * Adds option '"--ignore-externals" to Subversion integration.
   */
  public static final String SVN_IGNORE_EXTERNALS = "svn.ignore.externals";


  // possible SC properties for GIT
  public static final String GIT_PASSWORD = "git.password";
  public static final String GIT_USER = "git.user";
  public static final String GIT_DEPOT_PATH = "git.depot.path";
  public static final String GIT_PATH_TO_EXE = "git.exe.path";
  public static final String GIT_REPOSITORY = "git.repository";
  public static final String GIT_BRANCH = "git.branch";

  /**
   * Native change list number user to start build request.
   */
  public static final String GIT_CHANGE_LIST_NUMBER = "git.change.list.number";

  // possible SC properties for Surround SCM
  public static final String SURROUND_PASSWORD = "surround.password";
  public static final String SURROUND_USER = "surround.user";
  public static final String SURROUND_BRANCH = "surround.branch";
  public static final String SURROUND_PATH_TO_EXE = "surround.exe";
  public static final String SURROUND_HOST = "surround.address";
  public static final String SURROUND_REPOSITORY = "surround.repository";
  public static final String SURROUND_PORT = "surround.port";

  // ClearCase properties
  public static final String CLEARCASE_BRANCH = "cc.branch";
  public static final String CLEARCASE_CHANGE_WINDOW = "cc.change.window";
  public static final String CLEARCASE_PATH_TO_EXE = "cc.cleartool";
  public static final String CLEARCASE_RELATIVE_BUILD_DIR = "cc.relative.build.dir";
  public static final String CLEARCASE_VIEW_STORAGE_LOCATION = "cc.view.storage.location";
  public static final String CLEARCASE_VIEW_CONFIG_SPEC = "cc.view.config.spec";
  public static final String CLEARCASE_VIEW_NAME_TEMPLATE = "cc.view.name.template";
  public static final String CLEARCASE_TEXT_MODE = "cc.tmode.auto";
  public static final String CLEARCASE_VIEW_STORAGE_LOCATION_CODE = "cc.view.storage.location.code";
  public static final String CLEARCASE_IGNORE_LINES = "cc.ignore.lines";
  public static final String CLEARCASE_START_DATE = "cc.start.date";

  // CLEARCASE_TEXT_MODE values
  public static final byte CLEARCASE_TEXT_MODE_NOT_SET = 0;
  public static final byte CLEARCASE_TEXT_MODE_AUTO = 1;
  public static final byte CLEARCASE_TEXT_MODE_MSDOS = 2;
  public static final byte CLEARCASE_TEXT_MODE_UNIX = 3;
  public static final byte CLEARCASE_TEXT_MODE_INSERT_CR = 4;
  public static final byte CLEARCASE_TEXT_MODE_STRIP_CR = 5;
  public static final byte CLEARCASE_TEXT_MODE_TRANSPARENT = 6;

  // storage locations
  public static final byte CLEARCASE_STORAGE_CODE_AUTOMATIC = 0;
  public static final byte CLEARCASE_STORAGE_CODE_STGLOC = 1;
  public static final byte CLEARCASE_STORAGE_CODE_VWS = 2;


  // possible SC properties for StarTeam
  public static final String STARTEAM_PASSWORD = "starteam.password";
  public static final String STARTEAM_USER = "starteam.user";
  public static final String STARTEAM_HOST = "starteam.host";
  public static final String STARTEAM_PORT = "starteam.port";
  public static final String STARTEAM_PROJECT_PATH = "starteam.project.path";
  public static final String STARTEAM_PATH_TO_EXE = "starteam.exe.path";
  public static final String STARTEAM_ENCRIPTION = "starteam.encription";
  public static final String STARTEAM_EOL_CONVERSION = "starteam.eol.conversion";

  public static final byte STARTEAM_ENCRYPTION_NO_ENCRYPTION = 0;
  public static final byte STARTEAM_ENCRYPTION_RSA_R4_STREAM_CIPHER = 1;
  public static final byte STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_ECB = 2;
  public static final byte STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_CBC = 3;
  public static final byte STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_CF = 4;

  public static final byte STARTEAM_EOL_OFF = 1;
  public static final byte STARTEAM_EOL_ON = 2;
  public static final byte STARTEAM_EOL_CR = 3;
  public static final byte STARTEAM_EOL_LF = 4;
  public static final byte STARTEAM_EOL_CRLF = 5;

  // P4 modtime option values
  public static final byte P4_OPTION_VALUE_NOMODTIME = 0;
  public static final byte P4_OPTION_VALUE_MODTIME = 1;
  public static final byte P4_OPTION_VALUE_NOCLOBBER = 0;
  public static final byte P4_OPTION_VALUE_CLOBBER = 1;

  // P4 line end codes
  public static final byte P4_LINE_END_LOCAL = 0;
  public static final byte P4_LINE_END_UNIX = 1;
  public static final byte P4_LINE_END_MAC = 2;
  public static final byte P4_LINE_END_WIN = 3;
  public static final byte P4_LINE_END_SHARE = 4;

  // P4 line end values
  public static final String P4_LINE_END_VALUE_LOCAL = "local";
  public static final String P4_LINE_END_VALUE_UNIX = "unix";
  public static final String P4_LINE_END_VALUE_MAC = "mac";
  public static final String P4_LINE_END_VALUE_SHARE = "share";

  // P4 authentication mode values
  public static final byte P4_AUTHENTICATION_MODE_VALUE_P4PASSWD = 0;
  public static final byte P4_AUTHENTICATION_MODE_VALUE_P4LOGIN = 1;

  // P4 client view source option values
  public static final byte P4_CLIENT_VIEW_SOURCE_VALUE_FIELD = 0;
  public static final byte P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH = 1;
  public static final byte P4_CLIENT_VIEW_SOURCE_VALUE_CLIENT_NAME = 2;


  // SourceGear Vault
  public static final String VAULT_EXE = "vault.exe";
  public static final String VAULT_HOST = "vault.host";
  public static final String VAULT_PASSWORD = "vault.password";
  public static final String VAULT_PROXY_DOMAIN = "vault.proxy.domain";
  public static final String VAULT_PROXY_PASSWORD = "vault.proxy.password";
  public static final String VAULT_PROXY_PORT = "vault.proxy.port";
  public static final String VAULT_PROXY_SERVER = "vault.proxy.server";
  public static final String VAULT_PROXY_USER = "vault.proxy.user";
  public static final String VAULT_REPOSITORY = "vault.repository";
  public static final String VAULT_REPOSITORY_PATH = "vault.repository.path";
  public static final String VAULT_USE_SSL = "vault.use.ssl";
  public static final String VAULT_USER = "vault.user";

  // PVCS
  public static final String PVCS_BRANCH_NAME = "pvcs.branch";
  public static final String PVCS_LABEL = "pvcs.label";
  public static final String PVCS_PROJECT = "pvcs.project";
  public static final String PVCS_USER = "pvcs.user";
  public static final String PVCS_PROMOTION_GROUP = "pvcs.promotion.group";
  public static final String PVCS_REPOSITORY = "pvcs.repository";
  public static final String PVCS_PASSWORD = "pvcs.password";
  public static final String PVCS_EXE_PATH = "pvcs.pcli.path";
  public static final String PVCS_CHANGE_WINDOW = "pvcs.change.window";

  // MKS
  public static final String MKS_CO_DATE_FORMAT = "mks.co.date.format";
  public static final String MKS_DEVELOPMENT_PATH = "mks.development.path";
  public static final String MKS_HOST = "mks.host";
  public static final String MKS_LINE_TERMINATOR = "mks.line.terminator";
  public static final String MKS_PASSWORD = "mks.password";
  public static final String MKS_PATH_TO_EXE = "mks.path.to.exe";
  public static final String MKS_PORT = "mks.port";
  public static final String MKS_PROJECT = "mks.project";
  public static final String MKS_PROJECT_REVISION = "mks.project.revision";
  public static final String MKS_RLOG_DATE_FORMAT = "mks.rlog.date.format";
  public static final String MKS_USER = "mks.user";

  // MKS line terminator options
  public static final int MKS_LINE_TERMINATOR_LF = 0;
  public static final int MKS_LINE_TERMINATOR_CRLF = 1;
  public static final int MKS_LINE_TERMINATOR_NATIVE = 2;

  // "Command" VCS
  public static final String COMMAND_VCS_LABEL_COMMAND = "command.vcs.label.command";
  public static final String COMMAND_VCS_REMOVE_LABEL_COMMAND = "command.vcs.remove.label.command";
  public static final String COMMAND_VCS_SYNC_TO_CHANGE_LIST_COMMAND = "command.vcs.sync.to.change.list.command";
  public static final String COMMAND_VCS_COLUMN_DIVIDER = "command.vcs.column.divider";
  public static final String COMMAND_VCS_END_OF_RECORD = "command.vcs.end.of.record";
  public static final String COMMAND_VCS_CHANGE_DATE_FORMAT = "command.vcs.change.date.format";
  public static final String COMMAND_VCS_CHANGE_WINDOW = "command.vcs.change.window";

  // "Generic" VCS
  public static final String GENERIC_VCS_GET_CHANGES_COMMAND = "command.vcs.get.changes.command";

  // "File system" VCS
  public static final String FILESYSTEM_VCS_PATH = "command.vcs.path";
  public static final String FILESYSTEM_VCS_USER = "command.vcs.user";

  // CM Synergy VCS
  public static final String SYNGERGY_HOST = "synergy.host";
  public static final String SYNGERGY_PASSWORD = "synergy.password";
  public static final String SYNGERGY_PATH_TO_EXE = "synergy.path.to.exe";
  public static final String SYNGERGY_USER = "synergy.user";

  // Accurev VCS
  public static final String ACCUREV_DEPOT = "accurev.depot";
  public static final String ACCUREV_EXE_PATH = "accurev.exe.path";
  public static final String ACCUREV_HOST = "accurev.host";
  public static final String ACCUREV_PORT = "accurev.port";
  public static final String ACCUREV_STREAM = "accurev.stream";
  public static final String ACCUREV_USER = "accurev.user";
  public static final String ACCUREV_PASSWORD = "accurev.password";
  public static final String ACCUREV_PATH = "accurev.path";
  public static final String ACCUREV_EOL_TYPE = "accurev.line.terminator";
  public static final String ACCUREV_WORKSPACE_LOCK = "accurev.workspace.kind";

  public static final byte ACCUREV_EOL_PLATFORM = 1;
  public static final byte ACCUREV_EOL_UNIX = 2;
  public static final byte ACCUREV_EOL_WINDOWS = 3;

  public static final byte ACCUREV_WORKSPACE_LOKING_NONE = 1;
  public static final byte ACCUREV_WORKSPACE_LOKING_EXCLUSIVE = 2;
  public static final byte ACCUREV_WORKSPACE_LOKING_ANCHOR = 3;

  public static final String BAZAAR_PASSWORD = "bzr.password";
  public static final String BAZAAR_USER = "bzr.user";
  public static final String BAZAAR_BRANCH_LOCATION = "bzr.branch.location";
  public static final String BAZAAR_EXE_PATH = "bazaar.exe.path";
  public static final String BAZAAR_REVISION_NUMBER = "bazaar.revision.number";

  // ==================================================================================================================
  //
  // Mercurial
  //
  // ==================================================================================================================
  public static final String MERCURIAL_EXE_PATH = "hg.exe.path";
  public static final String MERCURIAL_URL = "hg.url";
  public static final String MERCURIAL_REVISION_NUMBER = "hg.revision.number";
  public static final String MERCURIAL_BRANCH = "hg.branch";

  public static final int MERCURIAL_PATH_TYPE_FILE = 0;
  public static final int MERCURIAL_PATH_TYPE_URL = 1;
  public static final int MERCURIAL_PATH_TYPE_SSH = 2;

  // dependant build ID
  public static final String REFERENCE_BUILD_ID = "build.id.reference";

  // Exlusion field is shared by all version control systems.
  public static final String VCS_EXCLUSION_PATHS = "vcs.exclusion.paths";


  // An optional custom checkout dir
  public static final String VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE = "vcs.custom.checkout.dir.template";


  /**
   * Defines what repository browser type is used when
   * displaying changes. Possible values include {@link
   * #CODE_FISHEYE} and {@link #CODE_VIEWVC}.
   *
   * @see #CODE_VIEWVC
   * @see #CODE_FISHEYE
   */
  public static final String REPOSITORY_BROWSER_TYPE = "repository.browser.type";

  /**
   * URL for Cenqua FishEye integration.
   *
   * @see FishEyeSettingsPanel
   */
  public static final String FISHEYE_URL = "fisheye.url";

  /**
   * Root for Cenqua FishEye integration.
   *
   * @see FishEyeSettingsPanel
   */
  public static final String FISHEYE_ROOT = "fisheye.root";

  /**
   * URL for WebSVN integration.
   *
   * @see WebSVNSettingsPanel
   */
  public static final String WEB_SVN_URL = "web.svn.url";

  /**
   * Repository name for Web SVN integration.
   *
   * @see WebSVNSettingsPanel
   */
  public static final String WEB_SVN_REPNAME = "web.svn.repname";

  /**
   * Code for a "not-selected" repository browser.
   */
  public static final int CODE_NOT_SELECTED = 0;

  /**
   * Code for selection of ViewVC as a repository browser.
   */
  public static final int CODE_VIEWVC = 1;

  /**
   * Code for selection of FishEye as a repository browser.
   */
  public static final int CODE_FISHEYE = 2;

  /**
   * Code for selection of WebSVN as a repository browser.
   */
  public static final int CODE_WEB_SVN = 3;

  /**
   * Code for selection of Github as a repository browser.
   */
  public static final int CODE_GITHUB = 4;

  /**
   * Tells to have a no-checkout build.
   */
  public static final String DO_NOT_CHECKOUT = "do.not.checkout";

  private int buildID = BuildConfig.UNSAVED_ID;
  private int propertyID = UNSAVED_ID;
  private String propertyName = null;
  private String propertyValue = null;
  private long propertyTimeStamp = 1;


  /**
   * Default constructor.
   */
  public SourceControlSetting() {
  }


  /**
   * Constructor.
   *
   * @param buildID       build ID
   * @param propertyName  property name
   * @param propertyValue property value
   */
  public SourceControlSetting(final int buildID, final String propertyName, final String propertyValue) {
    this.buildID = buildID;
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }


  /**
   * Constructor.
   *
   * @param buildID       build ID
   * @param propertyName  property name
   * @param propertyValue property value
   */
  public SourceControlSetting(final int buildID, final String propertyName, final int propertyValue) {
    this.buildID = buildID;
    this.propertyName = propertyName;
    this.propertyValue = Integer.toString(propertyValue);
  }


  /**
   * Returns build ID
   *
   * @return String
   * @hibernate.property column="BUILD_ID" unique="false"
   * null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * The getter method for this property ID generator-parameter-1="SEQUENCE_GENERATOR"
   * generator-parameter-2="SEQUENCE_ID"
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getPropertyID() {
    return propertyID;
  }


  public void setPropertyID(final int propertyID) {
    this.propertyID = propertyID;
  }


  /**
   * Returns property name
   *
   * @return String
   * @hibernate.property column="NAME" unique="true"
   * null="false"
   */
  public String getPropertyName() {
    return propertyName;
  }


  public void setPropertyName(final String propertyName) {
    this.propertyName = propertyName;
  }


  /**
   * Returns configuration name
   *
   * @return String
   * @hibernate.property column="VALUE" unique="true"
   * null="false"
   */
  public String getPropertyValue() {
    return propertyValue;
  }


  public void setPropertyValue(final String propertyValue) {
    this.propertyValue = propertyValue;
  }


  public void setPropertyValue(final int intPropertyValue) {
    this.propertyValue = Integer.toString(intPropertyValue);
  }


  /**
   * Helper method to return property value as int
   *
   * @return return property value as int.
   * @throws NumberFormatException if this setting is not a valid integer.
   */
  public int getPropertyValueAsInt() throws NumberFormatException {
    return Integer.parseInt(propertyValue);
  }


  /**
   * Returns timestamp
   *
   * @return long
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getPropertyTimeStamp() {
    return propertyTimeStamp;
  }


  public void setPropertyTimeStamp(final long propertyTimeStamp) {
    this.propertyTimeStamp = propertyTimeStamp;
  }


  public static String getValue(final SourceControlSetting setting, final String defaultValue) {
    if (setting == null) {
      return defaultValue;
    }
    return StringUtils.isBlank(setting.propertyValue) ? defaultValue : setting.propertyValue;
  }


  public String toString() {
    return "SourceControlSetting{" +
            "buildID=" + buildID +
            ", propertyID=" + propertyID +
            ", propertyName='" + propertyName + '\'' +
            ", propertyValue='" + propertyValue + '\'' +
            ", propertyTimeStamp=" + propertyTimeStamp +
            '}';
  }
}
