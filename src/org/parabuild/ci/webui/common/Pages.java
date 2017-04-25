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
package org.parabuild.ci.webui.common;

import viewtier.ui.Color;
import viewtier.ui.Font;

public final class Pages {

  public static final String ADMIN_ABOUT = makeTierletURL("about");
  public static final String ADMIN_ACTIVATE_BUILD = makeTierletURL("admin/build/activate");
  public static final String ADMIN_BUILD_COMMANDS_LIST = makeTierletURL("admin/build/commands");
  public static final String ADMIN_BUILDS = makeTierletURL("admin/builds");
  public static final String ADMIN_CLEANUP_LOGS = makeTierletURL("admin/build/cleanuplogs");
  public static final String ADMIN_CLEANUP_RESULTS = makeTierletURL("admin/build/cleanupresults");
  public static final String ADMIN_CLEAR_ERROR = makeTierletURL("admin/errors/clearerror");
  public static final String ADMIN_CLONE_BUILD = makeTierletURL("admin/build/clone");
  public static final String ADMIN_DEACTIVATE_BUILD = makeTierletURL("admin/build/deactivate");
  public static final String ADMIN_DELETE_DISPLAY_GROUP = makeTierletURL("admin/displaygroup/delete");
  public static final String ADMIN_DELETE_GROUP = makeTierletURL("admin/group/delete");
  public static final String ADMIN_DELETE_USER = makeTierletURL("admin/user/delete");
  public static final String ADMIN_DELTE_BUILD = makeTierletURL("admin/build/delete");
  public static final String ADMIN_DISPLAY_GROUPS = makeTierletURL("admin/displaygroups");
  public static final String ADMIN_DOCS = makeTierletURL("admin/docs");
  public static final String ADMIN_EDIT_BUILD = makeTierletURL("admin/build/edit");
  public static final String ADMIN_CHANGE_BUILD_TYPE = makeTierletURL("admin/build/change/schedule/type");
  public static final String ADMIN_EDIT_DISPLAY_GROUP = makeTierletURL("admin/displaygroup/edit");
  public static final String ADMIN_EDIT_GROUP = makeTierletURL("admin/group/edit");
  public static final String ADMIN_EDIT_USER = makeTierletURL("admin/user/edit");
  public static final String ADMIN_ERROR_DETAILS = makeTierletURL("admin/errors/showerror");
  public static final String ADMIN_ERROR_LIST = makeTierletURL("admin/errors");
  public static final String ADMIN_GROUPS = makeTierletURL("admin/groups");
  public static final String ADMIN_INSTANT_MESSAGING_CONFIG = makeTierletURL("admin/system/imconfig");
  public static final String ADMIN_NEW_BUILD = makeTierletURL("admin/build/edit");
  public static final String ADMIN_PROCESS_LIST = makeTierletURL("admin/proclist");
  public static final String ADMIN_REQUEST_CLEAN_CHECKOUT = makeTierletURL("admin/build/request/clean/checkout");
  public static final String ADMIN_RERUN_BUILD = makeTierletURL("admin/build/rerun");
  public static final String ADMIN_RESET_BUILD_STATS_CACHES = makeTierletURL("admin/build/rstatcaches");
  public static final String ADMIN_RESET_PASSWORD = makeTierletURL("admin/rpass");
  public static final String ADMIN_RESUME_BUILD = makeTierletURL("admin/build/resume");
  public static final String ADMIN_RESUME_GROUP = makeTierletURL("admin/build/resume/group");
  public static final String ADMIN_START_BUILD = makeTierletURL("admin/build/start");
  public static final String ADMIN_STATUS = makeTierletURL("admin/status");
  public static final String ADMIN_STOP_BUILD = makeTierletURL("admin/build/stop");
  public static final String ADMIN_STOP_GROUP = makeTierletURL("admin/build/stop/group");
  public static final String ADMIN_SYSTEM_CONFIG_LINKS = makeTierletURL("admin/system/list");
  public static final String ADMIN_USERS = makeTierletURL("admin/users");
  public static final String ADMIN_BUILDERS = makeTierletURL("admin/build/farms");
  public static final String ADMIN_EDIT_BUILDER = makeTierletURL("admin/build/farm/edit");
  public static final String ADMIN_DELETE_BUILDER = makeTierletURL("admin/build/farm/delete");
  public static final String ADMIN_LDAP_CONFIG = makeTierletURL("admin/system/ldapconfig");
  public static final String ADMIN_SECURITY_CONFIGURATION = makeTierletURL("admin/system/security");
  public static final String ADMIN_EMAIL_CONFIGURATION = makeTierletURL("admin/system/email");
  public static final String ADMIN_EMAIL_GLOBAL_VCS_USER_MAP = makeTierletURL("admin/system/email/global/vcs/map");
  public static final String ADMIN_EMAIL_GLOBAL_VCS_USER_MAP_EDIT = makeTierletURL("admin/system/email/global/vcs/map/edit");
  public static final String ADMIN_EMAIL_GLOBAL_VCS_USER_MAP_DELETE = makeTierletURL("admin/system/email/global/vcs/map/delete");
  public static final String ADMIN_APPEARANCE_CONFIGURATION = makeTierletURL("admin/system/appearance");
  public static final String ADMIN_STABILITY_CONFIGURATION = makeTierletURL("admin/system/stability");
  /**
   * Page with undocumented commands.
   */
  public static final String PAGE_UNDOCUMENTED_COMMANDS = makeTierletURL("undocumented");


  public static final String RESULT_GROUPS = makeTierletURL("result/groups");
  public static final String RESULT_GROUP_EDIT = makeTierletURL("result/group/edit");
  public static final String RESULT_GROUP_DELETE = makeTierletURL("result/group/delete");
  public static final String RESULT_GROUP_CONTENT = makeTierletURL("result/group/content");
  public static final String RESULT_GROUP_UNPUBLISH_RESULT = makeTierletURL("result/group/content/unpublish");

  public static final String BUILD_CHANGES = makeTierletURL("build/changes");
  public static final String BUILD_RESULTS = makeTierletURL("build/results");
  public static final String BUILD_COFNIG_REPORT = makeTierletURL("build/configreport");
  public static final String BUILD_STATISTICS = makeTierletURL("build/statistics");
  public static final String BUILD_HISTORY = makeTierletURL("build/history");
  public static final String BUILD_LOG = makeTierletURL("build/log");
  public static final String BUILD_DIFF_TWO = makeTierletURL("build/difftwo");
  public static final String BUILD_DIFF = makeTierletURL("build/diff");
  public static final String PUBLIC_BUILDS = makeTierletURL("index");
  public static final String PAGE_AGENTS = makeTierletURL("agents");
  public static final String PAGE_AGENT_ENVIRONMENT = makeTierletURL("agent/environment");
  public static final String PUBLIC_LOGIN = makeTierletURL("login");
  public static final String PUBLIC_SEARCH = makeTierletURL("search");
  public static final String PUBLIC_LOGOUT = makeTierletURL("logout");
  public static final String PUBLIC_SUPPORT = makeTierletURL("support");
  public static final String PUBLIC_PREFERENCES = makeTierletURL("preferences");
  public static final String RELEASE_NOTES = makeTierletURL("build/relnotes");
  public static final String RELEASE_NOTE_DETAILS = makeTierletURL("build/relnote/details");
  public static final String PUBLIC_WATCH_LOG = makeTierletURL("build/watch/log");

  // merge pages
  public static final String PAGE_MERGE_LIST = makeTierletURL("merge/status/list");
  public static final String PAGE_MERGE_EDIT = makeTierletURL("merge/edit");
  public static final String PAGE_MERGE_DELETE = makeTierletURL("merge/delete");
  public static final String PAGE_MERGE_STOP = makeTierletURL("merge/stop");
  public static final String PAGE_MERGE_RESUME = makeTierletURL("merge/resume");
  public static final String PAGE_MERGE_COMMANDS = makeTierletURL("merge/commands");
  public static final String PAGE_MERGE_START = makeTierletURL("merge/start");
  public static final String PAGE_MERGE_DETAILED_STATUS = makeTierletURL("merge/status");
  public static final String PAGE_MERGE_REPORT = makeTierletURL("merge/report");
  public static final String PAGE_MERGE_QUEUE_REPORT = makeTierletURL("merge/queue");
  public static final String PAGE_MERGE_RESET_ALL = makeTierletURL("merge/resetall");
  public static final String PAGE_MERGE_CHANGE_LIST = makeTierletURL("merge/changelist");

  // Builder
  public static final String PAGE_BUILDER_DETAILS = makeTierletURL("admin/build/farm/details");
  public static final String PAGE_ADD_BUILDER_AGENT = makeTierletURL("admin/build/farm/add/agent");
  public static final String PAGE_DETACH_BUILDER_AGENT = makeTierletURL("admin/build/farm/detach/agent");


  // styles - colors
  public static final Color COLOR_PANEL_HEADER_BG = new Color(0xe0e0e0);
  public static final Color COLOR_PANEL_BORDER = new Color(0xc8c8c8);
  public static final Color COLOR_COMMON_LINK_FG = Color.Navy;
  public static final Color COLOR_ERROR_FG = Color.DarkRed;
  public static final Color COLOR_BUILD_SUCCESSFUL = Color.DarkGreen;
  public static final Color COLOR_BUILD_FAILED = Color.DarkRed;
  public static final Color COLOR_HEADER_FOREGROUND = Color.White;
  public static final Color COLOR_HEADER_MENU_BACKGROUND = new Color(0x3D589A);
  public static final Color COLOR_HEADER_BACKGROUND = Color.Navy;

  public static final Color TABLE_COLOR_BORDER = new Color(0xf3f3f3);
  public static final Color TABLE_COLOR_HEADER_BG = new Color(0xeaeaea);
  //  public static final Color TABLE_COLOR_HEADER_BG = new Color(0xf3f3f3);
  public static final Color TABLE_COLOR_HEADER_FG = Color.Black;
  public static final Color TABLE_GRID_COLOR = new Color(0xc0c0c0);
  public static final Color TABLE_COLOR_ODD_ROW_BACKGROUND = new Color(0xF1F1F1);
  public static final Color TABLE_COLOR_EVEN_ROW_BACKGROUND = Color.White;

  public static final Color COLOR_LIGHT_LIGHT_YELLOW = new Color(0xfffff0);

  // styles - fonts
  public static final int COMMMON_FONT_SIZE = 12;
  public static final Font.Family COMMON_FONT_FAMILY = Font.SansSerif;
  public static final Font FONT_BUILD_RESULT = new Font(COMMON_FONT_FAMILY, Font.Bold | Font.None, COMMMON_FONT_SIZE);
  public static final Font FONT_COMMON = new Font(COMMON_FONT_FAMILY, Font.Plain, COMMMON_FONT_SIZE);
  public static final Font FONT_COMMON_LABEL = new Font(COMMON_FONT_FAMILY, Font.Bold, COMMMON_FONT_SIZE);
  public static final Font FONT_COMMON_BOLD_LABEL = new Font(COMMON_FONT_FAMILY, Font.Bold, COMMMON_FONT_SIZE);
  public static final Font FONT_COMMON_LINK = new Font(COMMON_FONT_FAMILY, Font.Plain, COMMMON_FONT_SIZE);
  public static final Font FONT_COMMON_BOLD_LINK = new Font(COMMON_FONT_FAMILY, Font.Bold, COMMMON_FONT_SIZE);
  public static final Font FONT_COMMON_MENU = new Font(COMMON_FONT_FAMILY, Font.Bold, COMMMON_FONT_SIZE - 1);
  public static final Font FONT_COMMON_SMALL = new Font(COMMON_FONT_FAMILY, Font.Plain, COMMMON_FONT_SIZE - 1);
  public static final Font FONT_DEFAULT_TABLE_HEADER = new Font(COMMON_FONT_FAMILY, Font.Bold, 12);
  public static final Font FONT_HEADER_LABEL = new Font(Font.SansSerif, Font.Bold, 18);
  public static final Font FONT_HEADER_LINK = new Font(Font.SansSerif, Font.Bold | Font.None, 12);
  public static final Font FONT_LOG = new Font(COMMON_FONT_FAMILY, Font.Plain, 10);
  public static final Font FONT_TABLE_HEADER = new Font(COMMON_FONT_FAMILY, Font.Bold, COMMMON_FONT_SIZE);


  // Style constants
  private static final int PAGE_HEADER_FONT_SIZE = COMMMON_FONT_SIZE + 8;
  public static final Color COLOR_PAGE_HEADER_BACKGROUND = Color.White;
  public static final Color COLOR_PAGE_HEADER_FOREGROUND = COLOR_HEADER_MENU_BACKGROUND;
  public static final Font FONT_PAGE_HEADER = new Font(COMMON_FONT_FAMILY, Font.Bold, PAGE_HEADER_FONT_SIZE);
  public static final Font FONT_PAGE_HEADER_LINK = new Font(COMMON_FONT_FAMILY, Font.Bold | Font.None, PAGE_HEADER_FONT_SIZE);

  private static final int PAGE_SECTION_FONT_SIZE = COMMMON_FONT_SIZE + 6;
  public static final Color COLOR_PAGE_SECTION_BACKGROUND = Color.White;
  public static final Color COLOR_PAGE_SECTION_FOREGROUND = COLOR_HEADER_MENU_BACKGROUND;
  public static final Font FONT_PAGE_SECTION = new Font(COMMON_FONT_FAMILY, Font.Bold, PAGE_SECTION_FONT_SIZE);


  public static final int PAGE_WIDTH = 760;
  public static final int PANEL_DIVIDER = 10;
  public static final int HEADER_HEIGHT = 30;

  // tierlet param names
  public static final String ATTRIBUTE_RETURN_TIERLET = "parabuild.attrib.return.tierlet";
  public static final String PARAM_BUILD_ID = "buildid";
  public static final String PARAM_BUILD_RUN_CONFIG_ID = "buildruncfgid";
  public static final String PARAM_BUILD_RUN_ID = "buildrunid";
  public static final String PARAM_DAYS = "days";
  public static final String PARAM_STATUS_VIEW = "view";
  public static final String PARAM_DISPLAY_GROUP_ID = "displaygroupid";
  public static final String PARAM_ERROR_ID = "errorid";
  public static final String PARAM_FILE_ID = "fid";
  public static final String PARAM_FILE_NAME = "fname";
  public static final String PARAM_GROUP_ID = "groupid";
  public static final String PARAM_LOG_ID = "logid";
  public static final String PARAM_QUERY = "query";
  public static final String PARAM_RELEASE_NOTE_ID = "rnid";
  public static final String PARAM_SHOW_ADVANCED = "showadvanced";
  public static final String PARAM_SHOW_CACHE_STATS = "cachestats";
  public static final String PARAM_SHOW_ENV_DETAILS = "showdetails";
  public static final String PARAM_SHOW_FILES = "showfiles";
  public static final String PARAM_VIEW_CHANGES_MODE = "showchangesmode";
  public static final String PARAM_STATS_CODE = "statscode";
  public static final String PARAM_USER_ID = "userid";
  public static final String PARAM_BUILD_START_NUMBER = "buildnumstart";
  public static final String PARAM_BUILD_END_NUMBER = "buildnumend";
  public static final String PARAM_BUILDER_ID = "bldrd";
  public static final String PARAM_ENABLE_REFRESH = "bsre";
  public static final String PARAM_EDIT = "edit";
  public static final String PARAM_RESULT_GROUP_ID = "resultgroupid";
  public static final String PARAM_PUBLISHED_RESULT_ID = "publishedresultid";
  public static final String PARAM_PROJECT_ID = "projectid";

  public static final String PARAM_VIEW_CHANGES_MODE_VALUE_BY_FILE = "byfile";

  public static final String PARAM_VIEW_CHANGES_MODE_VALUE_BY_CHANGE = "bychange";

  // predefined param values
  public static final String PARAM_VALUE_ALL_ERRORS = "all";

  /**
   * List of projects.
   */
  public static final String PAGE_PROJECTS = makeTierletURL("projects");

  /**
   * Cleans up all inactive workspaces.
   */
  public static final String PAGE_CLEANUP_ALL_INACTIVE_WORKSPACES = makeTierletURL("admin/builds/cleanup/inactive");


  /**
   * Page to edit a project.
   */
  public static final String PAGE_EDIT_PROJECT = makeTierletURL("admin/project/edit");

  /**
   * Page to delete a project.
   */
  public static final String PAGE_DELETE_PROJECT = makeTierletURL("admin/project/delete");

  /**
   * List of builder configration.
   */
  public static final String PAGE_AGENT_LIST = makeTierletURL("admin/agent/list");

  /**
   * Page to edit a builder configuration.
   */
  public static final String PAGE_EDIT_AGENT = makeTierletURL("admin/agent/edit");

  /**
   * Page to delete a builder configuration.
   */
  public static final String PAGE_DELETE_AGENT = makeTierletURL("admin/agent/delete");

  /**
   * List of Promotion policies.
   */
  public static final String PAGE_PROMOTION_POLICY_LIST = makeTierletURL("admin/promotion/policies");


  /**
   * Edit promotion policy
   */
  public static final String PAGE_EDIT_PROMOTION_POLICY = makeTierletURL("admin/promotion/policy/edit");


  /**
   * Edit promotion policy step.
   */
  public static final String PAGE_EDIT_PROMOTION_POLICY_STEP = makeTierletURL("admin/promotion/policy/step/edit");


  /**
   * Edit promotion policy step.
   */
  public static final String PAGE_DELETE_PROMOTION_POLICY_STEP = makeTierletURL("admin/promotion/policy/step/delete");


  /**
   * Shows read-only information of the policy.
   */
  public static final String PAGE_VIEW_PROMOTION_POLICY_DETAILS = makeTierletURL("admin/promotion/policy/view/details");


  /**
   * Shows read-only information of the policy step.
   */
  public static final String PAGE_VIEW_PROMOTION_POLICY_STEP_DETAILS = makeTierletURL("admin/promotion/policy/step/view/details");


  /**
   * Shows interval changes for a file.
   */
  public static final String PAGE_VIEW_BUILD_DIFF_FILE_CHANGES = makeTierletURL("build/diff/file/changes");

  /**
   * Delete promotion policy
   */
  public static final String PAGE_DELETE_PROMOTION_POLICY = makeTierletURL("admin/promotion/policy/delete");


  /**
   * Changes promotion policy order.
   */
  public static final String PAGE_CHANGE_PROMOTION_STEP_ORDER = makeTierletURL("admin/promotion/step/change/order");

  /**
   * Page that staticly points to the last clean build.
   */
  public static final String PAGE_LATEST_SUCCESSFUL_BUILD = makeTierletURL("latest/successful/build");


  /**
   * Page to display build run tests.
   */
  public static final String PAGE_BUILD_RUN_TESTS = makeTierletURL("build/run/tests");

  public static final String PAGE_VARIABLE_DELETE = makeTierletURL("admin/variable/delete");
  public static final String PAGE_VARIABLE_LIST = makeTierletURL("admin/variable/list");
  public static final String PAGE_VARIABLE_EDIT = makeTierletURL("admin/variable/edit");

  /**
   * Value to display list view for build statuses.
   */
  public static final String STATUS_VIEW_LIST = "list";

  /**
   * Value to display a detailed view for a build status.
   */
  public static final String STATUS_VIEW_DETAILED = "detailed";

  /**
   * Value to display dashboard view for build statuses.
   */
  public static final String STATUS_VIEW_DASHBOARD = "dashboard";

  /**
   * Value to display recent builds
   */
  public static final String STATUS_VIEW_RECENT = "recent";

  /**
   * Merge ID
   */
  public static final String PARAM_MERGE_ID = "mergeid";

  /**
   * Show tail.
   */
  public static final String PARAM_SHOW_LOG_TAIL = "tail";

  /**
   * Page number.
   */
  public static final String PARAM_PAGE_NUM = "pagenum";

  /**
   * Branch change list.
   */
  public static final String PARAM_BRANCH_CHANGE_LIST_ID = "bchlid";

  /**
   * Merge queue change list.
   */
  public static final String PARAM_MERGE_QUEUE_CHANGE_LIST_ID = "mqchlid";

  /**
   * Change list ID.
   */
  public static final String PARAM_CHANGE_LIST_ID = "chlid";

  public static final String PARAM_FILTER = "filter";

  /**
   * Promotion policy ID parameter.
   */
  public static final String PARAM_PROMOTION_POLICY_ID = "prpolid";

  /**
   * Access parameter.
   */
  public static String PARAM_ACCESS = "access";


  /**
   * Builder ID.
   */
  public static final String PARAM_AGENT_ID = "gntd";

  /**
   * ID of a global VCS user to email mapping ID.
   */
  public static final String PARAM_VCS_MAPPING_ID = "gvcsmapid";

  /**
   * Filters to show tests
   */
  public static final String FILTER_ALL_FAILED_TESTS = "all-failed-tests";
  public static final String FILTER_NEW_FAILED_TESTS = "new-failed-tests";
  public static final String FILTER_ALL_TESTS = "all-tests";
  public static final String FILTER_NEW_TESTS = "new-tests";
  public static final String FILTER_SUCCESSFUL_TESTS = "successful-tests";
  public static final String FILTER_NEW_SUCCESSFUL_TESTS = "new-successful-tests";


  /**
   * Paramter that tells a form to validate on load.
   */
  public static final String PARAM_VALIDATE_ON_LOAD = "vldtnld";

  /**
   * Promotion policy step ID
   */
  public static final String PARAM_PROMOTION_POLICY_STEP_ID = "prmtnplcstpd";

  public static final String PARAM_BUILDER_AGENT_ID = "bldrgntd";

  public static final Color SECTION_HEADER_COLOR = new Color(0x006699);

  public static final String PARAM_PROMOTION_STEP_OPERATION_CODE = "prmtnstpprtn";
  public static final String PARAM_PROMOTION_STEP_OPERATION_UP = "up";
  public static final String PARAM_PROMOTION_STEP_OPERATION_DOWN = "down";
  public static final String PARAM_AGENT_STATUS_VIEW = "gntsttsvw";
  public static final String AGENT_STATUS_VIEW_LIST = "list";
  public static final String AGENT_STATUS_VIEW_LOAD = "load";


  public static final String PARAM_STOP_GROUP_SOURCE = "sgsrc";

  /**
   * A {@link #PARAM_BUILD_ID} is expected to accompany this value for {@link #PARAM_STOP_GROUP_SOURCE}.
   */
  public static final String STOP_GROUP_SOURCE_BUILD_COMMANDS = "stpbcmds";

  /**
   * A {@link #PARAM_AGENT_ID} is expected to accompany this value for {@link #PARAM_STOP_GROUP_SOURCE}.
   */
  public static final String STOP_GROUP_SOURCE_AGENT_STATUS = "stpgntstts";

  /**
   * Parameter for the source.
   */
  public static final String PARAM_RESUME_GROUP_SOURCE = "rsmgrpsrc";
  public static final String RESUME_GROUP_SOURCE_BUILD_COMMANDS = "rsmbcmds";
  public static final String RESUME_GROUP_SOURCE_AGENT_STATUS = "rsmgntstts";

  //
  public static final String PARAM_VARIABLE_ID = "vrbld";
  public static final String PARAM_VARIABLE_TYPE = "vrbltp";
  public static final String PARAM_VARIABLE_OWNER = "vrblwnr";

  //
  // Cookies
  //
  static final String COOKIE_SHOW_CHANGES_MODE = "parabuild_show_changes_mode";

  //
  public static final String PARAM_FROM_BUILD_NUMBER = "frombuild";

  //
  public static final String PARAM_TO_BUILD_NUMBER = "tobuild";


  /**
   * This factory mathod composes a BT trierlet URL from the
   * given URLs by prepending local URL with BT base URL.
   *
   * @param localTierletURL to be prepended with
   *                        "tierlet:bt"
   * @return fully qualified BT tierlet URL
   */
  public static final String makeTierletURL(final String localTierletURL) {
    return "tierlet:parabuild/" + localTierletURL;
  }
}
