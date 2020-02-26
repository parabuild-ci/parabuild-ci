package org.parabuild.ci.common;

import org.parabuild.ci.webui.admin.FishEyeSettingsPanel;
import org.parabuild.ci.webui.admin.WebSVNSettingsPanel;

public interface VCSAttribute {

  // CVS
  String CVS_ROOT = "cvs.root";
  String CVS_PASSWORD = "cvs.password";
  String CVS_PATH_TO_RSH = "cvs.path.to.rsh";
  String CVS_PATH_TO_PRIVATE_KEY = "cvs.path.to.private.key";
  String CVS_PATH_TO_CLIENT = "cvs.path.to.client";
  String CVS_REPOSITORY_PATH = "cvs.source.line.path";
  String CVS_BRANCH_NAME = "cvs.branch.name";
  String CVS_CHANGE_WINDOW = "cvs.change.window";
  String CVS_CHANGE_PRECHECK = "cvs.change.precheck";
  String CVS_COMPRESSION = "cvs.compression";
  String VIEWCVS_URL = "cvs.viewcvs.url";
  String VIEWCVS_ROOT = "cvs.viewcvs.root";
  String GITHUB_URL = "github.url";
  String CVS_CUSTOM_RELATIVE_BUILD_DIR = "cvs.relative.build.dir";
  String CVS_SUPPRESS_LOG_OUTPUT_IF_NO_CHANGES = "cvs.suppress.log.output.if.no.changes";

  // Perforce
  String P4_ADVANCED_VIEW_MODE = "p4.advanced.view.mode";
  String P4_AUTHENTICATION_MODE = "p4.authentication.mode";
  String P4_CLIENT = "p4.client";
  String P4_CLIENT_NAME_TEMPLATE = "p4.client.name.template";
  String P4_CLIENT_VIEW_BY_DEPOT_PATH = "p4.client.view.by.depot.path";
  String P4_CLIENT_VIEW_BY_CLIENT_NAME = "p4.client.view.by.client.name";
  String P4_CLIENT_VIEW_SOURCE = "p4.client.view.source";
  String P4_CLOBBER_OPTION = "p4.option.clobber";
  String P4_COUNTER = "p4.counter";
  String P4_DEPOT_PATH = "p4.depot.path";
  String P4_DEPOT_PATH_PART_PREFIX = P4_DEPOT_PATH + ".part";
  String P4_MODTIME_OPTION = "p4.option.modtime";
  String P4_P4WEB_URL = "p4.p4web.url";
  String P4_PASSWORD = "p4.password";
  String P4_PATH_TO_CLIENT = "p4.path.to.client";
  String P4_PORT = "p4.port";
  String P4_RELATIVE_BUILD_DIR = "p4.custom.rel.build.dir";
  String P4_UPDATE_HAVE_LIST = "p4.update.have.list";
  String P4_USE_UNC_PATHS = "p4.use.unc.paths";
  String P4_USER = "p4.user";
  String P4_VARS_OVERRIDE = "p4.variables.override";
  String P4_CASE_SENSITIVE_USER_NAMES = "p4.case.sensitive.user.names";
  String P4_LINE_END = "p4.line.end";
  /**
   * Native change list number user to start build request.
   */
  String P4_CHANGE_LIST_NUMBER = "p4.change.list.number";
  /**
   * Defines where Parabuild gets depot view.
   */
  String P4_DEPOT_VIEW_MODE = "p4.depot.view.mode";
  /**
   * Keeps path to a file in the depot.
   */
  String P4_DEPOT_VIEW_FILE_PATH = "p4.depot.view.file.path";
  /**
   * Keeps template client name.
   */
  String P4_DEPOT_VIEW_TEMPLATE_CLIENT = "p4.depot.view.template.client";
  /**
   * Depot view is entered from UI.
   */
  int P4_DEPOT_VIEW_MODE_DIRECT = 0;
  /**
   * Depot view is fetched from a file in depot
   */
  int P4_DEPOT_VIEW_MODE_DEPOT_FILE = 1;
  /**
   * Depot view is fetched from a template client
   */
  int P4_DEPOT_VIEW_MODE_DEPOT_CLIENT = 2;
  // P4 modtime option values
  byte P4_OPTION_VALUE_NOMODTIME = 0;
  byte P4_OPTION_VALUE_MODTIME = 1;
  byte P4_OPTION_VALUE_NOCLOBBER = 0;
  byte P4_OPTION_VALUE_CLOBBER = 1;
  // P4 line end codes
  byte P4_LINE_END_LOCAL = 0;
  byte P4_LINE_END_UNIX = 1;
  byte P4_LINE_END_MAC = 2;
  byte P4_LINE_END_WIN = 3;
  byte P4_LINE_END_SHARE = 4;
  // P4 line end values
  String P4_LINE_END_VALUE_LOCAL = "local";
  String P4_LINE_END_VALUE_UNIX = "unix";
  String P4_LINE_END_VALUE_MAC = "mac";
  String P4_LINE_END_VALUE_SHARE = "share";
  // P4 authentication mode values
  byte P4_AUTHENTICATION_MODE_VALUE_P4PASSWD = 0;
  byte P4_AUTHENTICATION_MODE_VALUE_P4LOGIN = 1;
  // P4 client view source option values
  byte P4_CLIENT_VIEW_SOURCE_VALUE_FIELD = 0;
  byte P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH = 1;
  byte P4_CLIENT_VIEW_SOURCE_VALUE_CLIENT_NAME = 2;

  // VSS
  String VSS_BRANCH_NAME = "vss.branch.name";
  String VSS_CHANGE_WINDOW = "vss.change.window";
  String VSS_DATABASE_PATH = "vss.db.path";
  String VSS_PASSWORD = "vss.password";
  String VSS_EXE_PATH = "vss.path.to.client";
  String VSS_PROJECT_PATH = "vss.project.path";
  String VSS_READONLY_CHECKOUT = "vss.readonly.checkout";
  String VSS_USER = "vss.user";

  // SVN (Subversion)
  String SVN_PASSWORD = "svn.password";
  String SVN_USER = "svn.user";
  String SVN_DEPOT_PATH = "svn.depot.path";
  String SVN_PATH_TO_EXE = "svn.exe.path";
  String SVN_URL = "svn.url";
  /**
   * Native change list number user to start build request.
   */
  String SVN_CHANGE_LIST_NUMBER = "svn.change.list.number";
  /**
   * If checked, means that builds will start on changes detected for non-recursive path and under.
   */
  String SVN_WATCH_NON_RECURSIVE_PATHS = "svn.watch.nonrecursive.paths";
  /**
   * Adds option '"--trust-server-cert" to Subversion integration.
   */
  String SVN_ADD_OPTION_TRUST_SERVER_CERT = "svn.add.option.trust.server.cert";
  /**
   * Adds option '"--ignore-externals" to Subversion integration.
   */
  String SVN_IGNORE_EXTERNALS = "svn.ignore.externals";
  // possible SC properties for GIT
  String GIT_PASSWORD = "git.password";
  String GIT_USER = "git.user";
  String GIT_DEPOT_PATH = "git.depot.path";
  String GIT_PATH_TO_EXE = "git.exe.path";
  String GIT_REPOSITORY = "git.repository";
  String GIT_BRANCH = "git.branch";
  /**
   * Native change list number user to start build request.
   */
  String GIT_CHANGE_LIST_NUMBER = "git.change.list.number";

  // Surround SCM
  String SURROUND_PASSWORD = "surround.password";
  String SURROUND_USER = "surround.user";
  String SURROUND_BRANCH = "surround.branch";
  String SURROUND_PATH_TO_EXE = "surround.exe";
  String SURROUND_HOST = "surround.address";
  String SURROUND_REPOSITORY = "surround.repository";
  String SURROUND_PORT = "surround.port";

  // ClearCase
  String CLEARCASE_BRANCH = "cc.branch";
  String CLEARCASE_CHANGE_WINDOW = "cc.change.window";
  String CLEARCASE_PATH_TO_EXE = "cc.cleartool";
  String CLEARCASE_RELATIVE_BUILD_DIR = "cc.relative.build.dir";
  String CLEARCASE_VIEW_STORAGE_LOCATION = "cc.view.storage.location";
  String CLEARCASE_VIEW_CONFIG_SPEC = "cc.view.config.spec";
  String CLEARCASE_VIEW_NAME_TEMPLATE = "cc.view.name.template";
  String CLEARCASE_TEXT_MODE = "cc.tmode.auto";
  String CLEARCASE_VIEW_STORAGE_LOCATION_CODE = "cc.view.storage.location.code";
  String CLEARCASE_IGNORE_LINES = "cc.ignore.lines";
  String CLEARCASE_START_DATE = "cc.start.date";
  // CLEARCASE_TEXT_MODE values
  byte CLEARCASE_TEXT_MODE_NOT_SET = 0;
  byte CLEARCASE_TEXT_MODE_AUTO = 1;
  byte CLEARCASE_TEXT_MODE_MSDOS = 2;
  byte CLEARCASE_TEXT_MODE_UNIX = 3;
  byte CLEARCASE_TEXT_MODE_INSERT_CR = 4;
  byte CLEARCASE_TEXT_MODE_STRIP_CR = 5;
  byte CLEARCASE_TEXT_MODE_TRANSPARENT = 6;
  // storage locations
  byte CLEARCASE_STORAGE_CODE_AUTOMATIC = 0;
  byte CLEARCASE_STORAGE_CODE_STGLOC = 1;
  byte CLEARCASE_STORAGE_CODE_VWS = 2;

  // StarTeam
  String STARTEAM_PASSWORD = "starteam.password";
  String STARTEAM_USER = "starteam.user";
  String STARTEAM_HOST = "starteam.host";
  String STARTEAM_PORT = "starteam.port";
  String STARTEAM_PROJECT_PATH = "starteam.project.path";
  String STARTEAM_PATH_TO_EXE = "starteam.exe.path";
  String STARTEAM_ENCRIPTION = "starteam.encription";
  String STARTEAM_EOL_CONVERSION = "starteam.eol.conversion";
  byte STARTEAM_ENCRYPTION_NO_ENCRYPTION = 0;
  byte STARTEAM_ENCRYPTION_RSA_R4_STREAM_CIPHER = 1;
  byte STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_ECB = 2;
  byte STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_CBC = 3;
  byte STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_CF = 4;
  byte STARTEAM_EOL_OFF = 1;
  byte STARTEAM_EOL_ON = 2;
  byte STARTEAM_EOL_CR = 3;
  byte STARTEAM_EOL_LF = 4;
  byte STARTEAM_EOL_CRLF = 5;

  // SourceGear Vault
  String VAULT_EXE = "vault.exe";
  String VAULT_HOST = "vault.host";
  String VAULT_PASSWORD = "vault.password";
  String VAULT_PROXY_DOMAIN = "vault.proxy.domain";
  String VAULT_PROXY_PASSWORD = "vault.proxy.password";
  String VAULT_PROXY_PORT = "vault.proxy.port";
  String VAULT_PROXY_SERVER = "vault.proxy.server";
  String VAULT_PROXY_USER = "vault.proxy.user";
  String VAULT_REPOSITORY = "vault.repository";
  String VAULT_REPOSITORY_PATH = "vault.repository.path";
  String VAULT_USE_SSL = "vault.use.ssl";
  String VAULT_USER = "vault.user";

  // PVCS
  String PVCS_BRANCH_NAME = "pvcs.branch";
  String PVCS_LABEL = "pvcs.label";
  String PVCS_PROJECT = "pvcs.project";
  String PVCS_USER = "pvcs.user";
  String PVCS_PROMOTION_GROUP = "pvcs.promotion.group";
  String PVCS_REPOSITORY = "pvcs.repository";
  String PVCS_PASSWORD = "pvcs.password";
  String PVCS_EXE_PATH = "pvcs.pcli.path";
  String PVCS_CHANGE_WINDOW = "pvcs.change.window";

  // MKS
  String MKS_CO_DATE_FORMAT = "mks.co.date.format";
  String MKS_DEVELOPMENT_PATH = "mks.development.path";
  String MKS_HOST = "mks.host";
  String MKS_LINE_TERMINATOR = "mks.line.terminator";
  String MKS_PASSWORD = "mks.password";
  String MKS_PATH_TO_EXE = "mks.path.to.exe";
  String MKS_PORT = "mks.port";
  String MKS_PROJECT = "mks.project";
  String MKS_PROJECT_REVISION = "mks.project.revision";
  String MKS_RLOG_DATE_FORMAT = "mks.rlog.date.format";
  String MKS_USER = "mks.user";

  // MKS line terminator options
  int MKS_LINE_TERMINATOR_LF = 0;
  int MKS_LINE_TERMINATOR_CRLF = 1;
  int MKS_LINE_TERMINATOR_NATIVE = 2;

  // Accurev VCS
  String ACCUREV_DEPOT = "accurev.depot";
  String ACCUREV_EXE_PATH = "accurev.exe.path";
  String ACCUREV_HOST = "accurev.host";
  String ACCUREV_PORT = "accurev.port";
  String ACCUREV_STREAM = "accurev.stream";
  String ACCUREV_USER = "accurev.user";
  String ACCUREV_PASSWORD = "accurev.password";
  String ACCUREV_PATH = "accurev.path";
  String ACCUREV_EOL_TYPE = "accurev.line.terminator";
  String ACCUREV_WORKSPACE_LOCK = "accurev.workspace.kind";
  byte ACCUREV_EOL_PLATFORM = 1;
  byte ACCUREV_EOL_UNIX = 2;
  byte ACCUREV_EOL_WINDOWS = 3;
  byte ACCUREV_WORKSPACE_LOKING_NONE = 1;
  byte ACCUREV_WORKSPACE_LOKING_EXCLUSIVE = 2;
  byte ACCUREV_WORKSPACE_LOKING_ANCHOR = 3;

  // BAZAAR
  String BAZAAR_PASSWORD = "bzr.password";
  String BAZAAR_USER = "bzr.user";
  String BAZAAR_BRANCH_LOCATION = "bzr.branch.location";
  String BAZAAR_EXE_PATH = "bazaar.exe.path";
  String BAZAAR_REVISION_NUMBER = "bazaar.revision.number";

  // Mercurial
  String MERCURIAL_EXE_PATH = "hg.exe.path";
  String MERCURIAL_URL = "hg.url";
  String MERCURIAL_REVISION_NUMBER = "hg.revision.number";
  String MERCURIAL_BRANCH = "hg.branch";
  int MERCURIAL_PATH_TYPE_FILE = 0;
  int MERCURIAL_PATH_TYPE_URL = 1;
  int MERCURIAL_PATH_TYPE_SSH = 2;

  // "Command" VCS
  String COMMAND_VCS_LABEL_COMMAND = "command.vcs.label.command";
  String COMMAND_VCS_REMOVE_LABEL_COMMAND = "command.vcs.remove.label.command";
  String COMMAND_VCS_SYNC_TO_CHANGE_LIST_COMMAND = "command.vcs.sync.to.change.list.command";
  String COMMAND_VCS_COLUMN_DIVIDER = "command.vcs.column.divider";
  String COMMAND_VCS_END_OF_RECORD = "command.vcs.end.of.record";
  String COMMAND_VCS_CHANGE_DATE_FORMAT = "command.vcs.change.date.format";
  String COMMAND_VCS_CHANGE_WINDOW = "command.vcs.change.window";

  // "Generic" VCS
  String GENERIC_VCS_GET_CHANGES_COMMAND = "command.vcs.get.changes.command";

  // "File system" VCS
  String FILESYSTEM_VCS_PATH = "command.vcs.path";
  String FILESYSTEM_VCS_USER = "command.vcs.user";

  // CM Synergy VCS
  String SYNGERGY_HOST = "synergy.host";
  String SYNGERGY_PASSWORD = "synergy.password";
  String SYNGERGY_PATH_TO_EXE = "synergy.path.to.exe";
  String SYNGERGY_USER = "synergy.user";
  /**
   * URL for Cenqua FishEye integration.
   *
   * @see FishEyeSettingsPanel
   */
  String FISHEYE_URL = "fisheye.url";
  /**
   * Root for Cenqua FishEye integration.
   *
   * @see FishEyeSettingsPanel
   */
  String FISHEYE_ROOT = "fisheye.root";
  /**
   * URL for WebSVN integration.
   *
   * @see WebSVNSettingsPanel
   */
  String WEB_SVN_URL = "web.svn.url";
  /**
   * Repository name for Web SVN integration.
   *
   * @see WebSVNSettingsPanel
   */
  String WEB_SVN_REPNAME = "web.svn.repname";
  // dependant build ID
  String REFERENCE_BUILD_ID = "build.id.reference";
  // Exlusion field is shared by all version control systems.
  String VCS_EXCLUSION_PATHS = "vcs.exclusion.paths";
  // An optional custom checkout dir
  String VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE = "vcs.custom.checkout.dir.template";
  /**
   * Defines what repository browser type is used when
   * displaying changes. Possible values include {@link
   * #CODE_FISHEYE} and {@link #CODE_VIEWVC}.
   *
   * @see #CODE_VIEWVC
   * @see #CODE_FISHEYE
   */
  String REPOSITORY_BROWSER_TYPE = "repository.browser.type";
  /**
   * Code for a "not-selected" repository browser.
   */
  int CODE_NOT_SELECTED = 0;
  /**
   * Code for selection of ViewVC as a repository browser.
   */
  int CODE_VIEWVC = 1;
  /**
   * Code for selection of FishEye as a repository browser.
   */
  int CODE_FISHEYE = 2;
  /**
   * Code for selection of WebSVN as a repository browser.
   */
  int CODE_WEB_SVN = 3;
  /**
   * Code for selection of Github as a repository browser.
   */
  int CODE_GITHUB = 4;
  /**
   * Tells to have a no-checkout build.
   */
  String DO_NOT_CHECKOUT = "do.not.checkout";
}
