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

import org.parabuild.ci.object.Group;
import org.parabuild.ci.object.SystemProperty;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

/**
 * Responsible for system-wide configuration.
 */
public interface SystemConfigurationManager {

  /**
   * @return system property by name or default value if not
   *         found.
   */
  String getSystemPropertyValue(String propertyName, String defaultValue);


  /**
   * VCS command timeout.
   *
   * @return VCS command timeout.
   */
  int getSystemVCSTimeout();


  /**
   * @return system property by name or default value if not
   *         found.
   */
  int getSystemPropertyValue(String propertyName, int defaultValue);


  /**
   * @return system property by name or default value if not
   *         found.
   */
  byte getSystemPropertyValue(String propertyName, byte defaultValue);


  /**
   * Returns date format used to display dates
   */
  String getDateFormat();


  /**
   * Returns date and time format used to display dates and time.
   * This method doesnt throw any exceptions.
   */
  String getDateTimeFormat();


  /**
   * Formats date using system-wide date-time format.
   *
   * @param date to format
   * @return String with fromatted date.
   */
  String formatDateTime(Date date);


  /**
   * @return system-wide build status refresh period. If not set,
   *         returns default value.
   * @see SystemProperty#DEFAULT_BUILD_STATUS_REFRESH_RATE
   */
  int getBuildStatusRefreshSecs();


  /**
   * Saves list of build propeties
   *
   * @param properties
   */
  void saveSystemProperties(List properties);


  /**
   * Returns database schema version.
   *
   * @return database schema version.
   */
  int getSchemaVersion();

  /**
   * List system properties.
   *
   * @return system properties.
   */
  List getSystemProperties();


  /**
   * @return system property by name, <code>null</code>
   */
  SystemProperty getSystemProperty(String propertyName);


  /**
   * Returns true if system configuration complete.
   */
  boolean isSystemConfigurationComplete();


  /**
   * Helper method
   */
  boolean isSystemPropertySet(SystemProperty prop, String propName);


  /**
   * @return fully qualified build manager host address,
   *         including host name and port.
   */
  String getBuildManagerHostAndPort();


  /**
   * @return build manager host name
   * @throws UnknownHostException
   */
  String getBuildManagerHost() throws UnknownHostException;


  /**
   * Saves system property.
   *
   * @param systemProperty
   * @return saved SystemProperty
   */
  SystemProperty saveSystemProperty(SystemProperty systemProperty);


  /**
   * Deletes system property.
   *
   * @param systemProperty
   */
  void deleteSystemProperty(SystemProperty systemProperty);


  /**
   * @return true if including build results into e-mail
   *         messages is allowed.
   */
  boolean isIncludeResultsIntoMessages();


  /**
   * @return true if global advanced config mode is enabled.
   */
  boolean isAdvancedConfigurationMode();


  /**
   * @return true if displaying RSS is enabled.
   */
  boolean isRSSDisplayEnabled();


  /**
   * @return true if build schedule gap is enabled.
   */
  boolean isScheduleGapEnabled();


  /**
   * @return true if given time is in schedule gap
   */
  boolean isTimeInScheduleGap(long timeInMilliSeconds);


  /**
   * @return true if given time is in schedule gap
   */
  boolean isTimeInScheduleGap(Date date);


  /**
   * @return true if builds shold be serialized.
   */
  boolean isSerializedBuilds();


  int getMaxChangeListSize();


  /**
   * @return true if LDAP authentication enebled
   */
  boolean isLDAPAuthenticationEnabled();


  /**
   * @return LDAP connection password or null or empty string if not set.
   */
  String getLDAPConnectionPassword();


  /**
   * @return LDAP connection URL or null or empty string if not set.
   */
  String getLDAPConnectionURL();


  /**
   * @return LDAP base element for user searches or null or empty string if not set.
   */
  String getLDAPBaseElementForUserSearches();


  /**
   * @return LDAP connection security level or null or empty string if not set.
   */
  String getLDAPConnectionSecurityLevel();


  /**
   * @return LDAP connection user name or null or empty string if not set.
   */
  String getLDAPConnectionUserName();


  /**
   * @return LDAP credentials digest or null or empty string if not set.
   */
  String getLDAPCredentialsDigest();


  /**
   * @return true if the whole LDAP tree should be searched.
   */
  boolean getLDAPSearchEntireSubtree();


  /**
   * @return true if crdentials digests should be used
   * @see #getLDAPCredentialsDigest()
   */
  boolean getLDAPUseCredentialsDigest();


  /**
   * @return LDAP user distinguished name template.
   */
  String getLDAPUserDistinguishedNameTemplate();


  /**
   * @return LDAP attribute name for user password.
   */
  String getLDAPUserPasswordAttributeName();


  /**
   * @return LDAP user search template.
   */
  String getLDAPUserSearchTemplate();


  /**
   * @return An ID of a group that a first-time user should be
   *         added or {@link Group#UNSAVED_ID} if not defined.
   */
  int getLDAPAddFirstTimeUserToGroupID();


  /**
   * @return true if change list descriptions should be shown to
   *         anonymous users.
   */
  boolean isHideChangeListDescriptionsFromAnonymousUsers();


  /**
   * Defines how we should lookup a user.
   * <p/>
   * Can be either {@link
   * ConfigurationConstants#LDAP_USER_LOOKUP_BY_DN_TEMPLATE} or
   * {@link ConfigurationConstants#LDAP_USER_LOOKUP_BY_SEARCH}
   */
  byte getLDAPUserLookupMode();


  /**
   * Defines mandatory e-mail attribuite name for LDAP authentication.
   *
   * @return e-mail attribuite name for LDAP authentication.
   */
  String getLDAPUserEmailAttributeName();


  /**
   * @return fully qualified build manager's protocol, host address,
   *         including host name and port.
   */
  String getBuildManagerProtocolHostAndPort();


  /**
   * @return initial number of change lists.
   */
  int getInitialNumberOfChangeLists();


  /**
   * @return maximum number of change lists.
   */
  int getMaxNumberOfChangeLists();


  /**
   * Creates a new sequence number for active build
   */
  int incrementBuildSequenceNumber();


  /**
   * @return true if project link should be displayed on the
   *         top navigation menu.
   */
  boolean isProjectDisplayEnabled();


  /**
   * @return true if deleting projects is allowed.
   */
  boolean isProjectDeletionEnabled();


  /**
   * @return LDAP version or null if is not set or is set to
   *         default.
   */
  String getLDAPVersion();


  /**
   * @return LDAP referral or null if is not set or is set to
   *         default.
   */
  String getLDAPReferrals();


  /**
   * @return true if build promotion tab should be shown.
   */
  boolean isBuildPromotionEnabled();


  /**
   * @return true if anonymous access to protected feeds is
   *         enabled.
   */
  boolean isAnonymousAccessToProtectedFeedsIsEnabled();


  /**
   * @return if change list files should be hidden from
   *         anonymous users.
   */
  boolean isHideChangeListFilesFromAnonymousUsers();


  /**
   * @return number of lines to quote from the build log in case of a build error.
   */
  int getErrorLogQuoteSize();


  /**
   * @return true if parallel builds should be shown in the build list.
   */
  boolean showParallelInListView();


  /**
   * @return true if merges link should be shown.
   */
  boolean isShowingMergesEnabled();


  int getDashboardRowSize();


  int getTailWindowSize();


  /**
   * @return true if change list numbers should be used as
   *         build numbers.
   */
  boolean useChangeListNumberAsBuildnumber();


  /**
   * @return true if publishing commands are enabled.
   */
  boolean isPublishingCommandsEnabled();

  /**
   * @return true if VCS names are case sensitive.
   */
  boolean isCaseSensitiveVCSNames();

  /**
   * Returns true if showing agents is enabled.
   *
   * @return true if showing agents is enabled.
   */
  boolean isShowingAgentsEnabled();

  /**
   * Returns number of columns on the agent status page.
   *
   * @return number of columns on the agent status page.
   */
  int getAgentStatusColums();

  /**
   * Returns agent status image height.
   *
   * @return agent status image height.
   */
  int getAgentStatusImageHeightPixels();

  /**
   * Returns agent status image width.
   *
   * @return agent status image width.
   */
  int getAgentStatusImageWidthPixels();


  /**
   * True if build start description should be present.
   *
   * @return True if build start description should be present.
   */
  boolean isShowBuildInstructions();

  boolean isShowIPAddressOnBuildStatusList();

  boolean isShowNextBuildTimeOnBuildStatusList();

  boolean isQueueManualStartRequests();

  void createSystemPropertyIfDoesNotExist(String propertyName, String propertyValue);

  void createOrUpdateSystemProperty(String propertyName, int propertyValue);

  void createOrUpdateSystemProperty(String propertyName, String propertyValue);

  public void createSystemProperty(String propertyName, String propertyValue);

  void deleteSystemProperty(String name);

  /**
   * Returns true if automatic agent upgrade is enabled.
   *
   * @return true if automatic agent upgrade is enabled.
   */
  boolean isAutomaticAgentUpgradeEnabled();

  /**
   * Returns true if checking for duplicates for custom checkout directories is enabled.
   *
   * @return true if checking for duplicates for custom checkout directories is enabled.
   */
  boolean isCheckCustomCheckoutDirectoriesForDuplicates();

  /**
   * Returns true if using LDAP to look up VCS user e-mails is enabled.
   *
   * @return true if using LDAP to look up VCS user e-mails is enabled.
   * @see #isLDAPAuthenticationEnabled()
   */
  boolean isUseLDAPToLookUpVCSUserEmails();

  boolean isShowNumberAndChangeListOnDashboard();

  boolean isRoundRobinLoadBalancing();

  boolean isCustomBuildNameValidation();

  String getCustomBuildNameRegex();

  /**
   * @return true if Parabuild will notify build administrators about system errors.
   */
  boolean isNotifyUsersWithEditRightsAboutSystemErrors();

  boolean isRespectIntermediateStepFailure();

  int getLastEnteredAgentPort();

  boolean isCustomVariableNameValidation();

  String getCustomVariableNameRegex();

  int getMaxParallelSystemUpgrades();

  boolean isNoCheckoutBuildEnabled();

  boolean useXMLLogFormatForSubversion();

  boolean isLoggingCleanupTimingEnabled();

  int getDefaultMaxRecentBuilds();

  boolean isUseGitUserEmail();
}
