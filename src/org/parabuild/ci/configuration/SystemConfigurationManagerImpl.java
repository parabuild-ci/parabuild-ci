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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.Group;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.ServiceManager;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 */
final class SystemConfigurationManagerImpl implements SystemConfigurationManager {

  private static final Log log = LogFactory.getLog(SystemConfigurationManagerImpl.class);


  /**
   * @return system property by name or default value if not
   *         found.
   */
  public String getSystemPropertyValue(final String propertyName, final String defaultValue) {
    try {
      final Cache cache = getSystemPropertyCache();
      final Element element = cache.get(propertyName);
      if (element == null) {
        final String value = getSystemPropertyValueFromDB(propertyName, defaultValue);
        cache.put(new Element(propertyName, value));
        return value;
      } else {
        return (String) element.getValue();
      }
    } catch (final CacheException ignored) {
      return getSystemPropertyValueFromDB(propertyName, defaultValue);
    }
  }


  private Cache getSystemPropertyCache() throws CacheException {
    return CacheManager.getInstance().getCache("system_configuration_cache");
  }


  /**
   * VCS command timeout.
   */
  public int getSystemVCSTimeout() {
    return getSystemPropertyValue(SystemProperty.DEFAULT_VCS_TIMEOUT, 120);
  }


  /**
   * @return system property by name or default value if not
   *         found.
   */
  public int getSystemPropertyValue(final String propertyName, final int defaultValue) {
    return Integer.parseInt(getSystemPropertyValue(propertyName, Integer.toString(defaultValue)));
  }


  public byte getSystemPropertyValue(final String propertyName, final byte defaultValue) {
    return Byte.parseByte(getSystemPropertyValue(propertyName, Byte.toString(defaultValue)));
  }


  /**
   * Returns date format used to display dates
   */
  public String getDateFormat() {
    return getSystemPropertyValue(SystemProperty.DATE_FORMAT, ConfigurationConstants.DEFAULT_DATE_FORMAT);
  }


  /**
   * Returns date and time format used to display dates and time.
   * This method doesnt throw any exceptions.
   */
  public String getDateTimeFormat() {
    try {
      return getSystemPropertyValue(SystemProperty.DATE_TIME_FORMAT, ConfigurationConstants.DEFAULT_DATE_TIME_FORMAT);
    } catch (final Exception e) {
      // ignore, if we fail for whatever reason, we will use default below
      log.error("Error getting date and time format, will use default", e);
      return ConfigurationConstants.DEFAULT_DATE_TIME_FORMAT;
    }
  }


  public String formatDateTime(final Date date) {
    return StringUtils.formatDate(date, getDateTimeFormat());
  }


  /**
   * @return system-wide build status refresh period. If not set,
   *         returns default value.
   * @see SystemProperty#DEFAULT_BUILD_STATUS_REFRESH_RATE
   */
  public int getBuildStatusRefreshSecs() {
    return getSystemPropertyValue(SystemProperty.BUILD_STATUS_REFRESH_SECS,
            SystemProperty.DEFAULT_BUILD_STATUS_REFRESH_RATE);
  }


  /**
   * Saves list of build properties
   *
   * @param properties
   */
  public void saveSystemProperties(final List properties) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (final Iterator iter = properties.iterator(); iter.hasNext(); ) {
          final SystemProperty systemProperty = (SystemProperty) iter.next();
          session.saveOrUpdateCopy(systemProperty);
          getSystemPropertyCache().remove(systemProperty.getPropertyName());
          if (systemProperty.getPropertyName().equals(SystemProperty.ENABLE_ANONYMOUS_BUILDS)) {
            SecurityManager.getInstance().invalidateRightSetCaches();
          }
        }
        return null;
      }
    });
  }


  /**
   * {@inheritDoc}
   */
  public int getSchemaVersion() {
    return getSystemPropertyValue(SystemProperty.SCHEMA_VERSION, -1);
  }


  /**
   * Saves system property.
   *
   * @param systemProperty
   */
  public SystemProperty saveSystemProperty(final SystemProperty systemProperty) {
    return (SystemProperty) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(systemProperty);
        getSystemPropertyCache().remove(systemProperty.getPropertyName());
        if (systemProperty.getPropertyName().equals(SystemProperty.ENABLE_ANONYMOUS_BUILDS)) {
          SecurityManager.getInstance().invalidateRightSetCaches();
        }
        return systemProperty;
      }
    });
  }


  /**
   * Deletes system property.
   *
   * @param systemProperty
   */
  public void deleteSystemProperty(final SystemProperty systemProperty) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete(systemProperty);
        getSystemPropertyCache().remove(systemProperty.getPropertyName());
        if (systemProperty.getPropertyName().equals(SystemProperty.ENABLE_ANONYMOUS_BUILDS)) {
          SecurityManager.getInstance().invalidateRightSetCaches();
        }
        return null;
      }
    });
  }


  /**
   * @return true if including build results into e-mail
   *         messages is allowed.
   */
  public boolean isIncludeResultsIntoMessages() {
    return getSystemPropertyValue(SystemProperty.INCLUDE_RESULTS_IN_MESSAGES,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * List system properties.
   */
  public List getSystemProperties() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from SystemProperty as sp");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * @return system property by name, <code>null</code>
   */
  public SystemProperty getSystemProperty(final String propertyName) {
    return (SystemProperty) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from SystemProperty as sp where sp.propertyName = ?");
        q.setString(0, propertyName);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * Returns true if system configuration complete.
   */
  public boolean isSystemConfigurationComplete() {
    boolean adminEmailSet = false;
    boolean adminNameSet = false;
    boolean smtpServerSet = false;
    final List props = getSystemProperties();
    for (final Iterator iter = props.iterator(); iter.hasNext(); ) {
      final SystemProperty prop = (SystemProperty) iter.next();
      if (isSystemPropertySet(prop, SystemProperty.BUILD_ADMIN_EMAIL)) {
        adminEmailSet = true;
      } else {
        if (isSystemPropertySet(prop, SystemProperty.BUILD_ADMIN_NAME)) {
          adminNameSet = true;
        } else {
          if (isSystemPropertySet(prop, SystemProperty.SMTP_SERVER_NAME)) {
            smtpServerSet = true;
          }
        }
      }
    }
    return adminEmailSet && adminNameSet && smtpServerSet;
  }


  /**
   * Helper method
   */
  public boolean isSystemPropertySet(final SystemProperty prop, final String propName) {
    return prop.getPropertyName().equals(propName) && !StringUtils.isBlank(prop.getPropertyValue());
  }


  /**
   * @return fully qualified build manager host address,
   *         including host name and port.
   */
  public String getBuildManagerHostAndPort() {
    String hostNameAndPort = "localhost";

    // get system property for fully qualified build manager host name
    final SystemProperty hostProperty = getSystemProperty(SystemProperty.BUILD_MANAGER_HOST_NAME);
    if (hostProperty == null || StringUtils.isBlank(hostProperty.getPropertyValue())) {
      // get a network host name
      try {
        hostNameAndPort = getBuildManagerHost() + ':' + ServiceManager.getInstance().getListenPort();
      } catch (final UnknownHostException e) {
        // report error, host name will be left "null"
        final org.parabuild.ci.error.Error error = new org.parabuild.ci.error.Error();
        error.setDescription("Can not determine build manager host name: " + StringUtils.toString(e));
        error.setDetails(e);
        error.setErrorLevel(org.parabuild.ci.error.Error.ERROR_LEVEL_ERROR);
        ErrorManagerFactory.getErrorManager().reportSystemError(error);
      }
    } else {
      hostNameAndPort = hostProperty.getPropertyValue();
    }

    return hostNameAndPort;
  }


  public String getBuildManagerProtocolHostAndPort() {
    return getSystemPropertyValue(SystemProperty.GENERATED_URL_PROTOCOL, "http://") + getBuildManagerHostAndPort();
  }


  /**
   * @return build manager host name
   * @throws UnknownHostException
   */
  public String getBuildManagerHost() throws UnknownHostException {
    return IoUtils.getLocalHostName();
  }


  /**
   * @return true if build schedule gap is enabled.
   */
  public boolean isScheduleGapEnabled() {
    return getSystemPropertyValue(SystemProperty.ENABLE_SCHEDULE_GAP,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if given time is in schedule gap
   */
  public boolean isTimeInScheduleGap(final long timeInMilliSeconds) {
    if (!isScheduleGapEnabled()) {
      return false;
    }
    // get params
    final int from = getSystemPropertyValue(SystemProperty.SCHEDULE_GAP_FROM, 0);
    final int to = getSystemPropertyValue(SystemProperty.SCHEDULE_GAP_TO, 0);
    // calculate if in the gap
    final Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timeInMilliSeconds);
    final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

    if (hourOfDay >= from && hourOfDay <= to) {
      return true;
    }

    if (from > to) {
      if (hourOfDay >= from && hourOfDay <= 23) {
        return true;
      }
      if (hourOfDay >= 0 && hourOfDay <= to) {
        return true;
      }
    }
    return false;
  }


  /**
   * @return true if given time is in schedule gap
   */
  public boolean isTimeInScheduleGap(final Date date) {
    return isTimeInScheduleGap(date.getTime());
  }


  /**
   * @return true if builds should be serialized.
   */
  public boolean isSerializedBuilds() {
    return getSystemPropertyValue(SystemProperty.SERIALIZE_BUILDS,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return system-wide max change list size.
   */
  public int getMaxChangeListSize() {
    return getSystemPropertyValue(SystemProperty.MAX_CHANGELIST_SIZE, SystemProperty.DEFAULT_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * @return true if global advanced config mode is enabled.
   */
  public boolean isAdvancedConfigurationMode() {
    return getSystemPropertyValue(SystemProperty.ENABLE_ADVANCED_BUILD_SETTING,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if displaying RSS is enabled.
   */
  public boolean isRSSDisplayEnabled() {
    return getSystemPropertyValue(SystemProperty.SHOW_RSS_LINKS,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if LDAP authentication enabled
   */
  public boolean isLDAPAuthenticationEnabled() {
    return getSystemPropertyValue(SystemProperty.LDAP_AUTHENTICATION_ENABLED, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return LDAP connection password or null or empty string if not set.
   */
  public String getLDAPConnectionPassword() {
    return getSystemPropertyValue(SystemProperty.LDAP_CONNECTION_PASSWORD, null);
  }


  /**
   * @return LDAP connection URL or null or empty string if not set.
   */
  public String getLDAPConnectionURL() {
    return getSystemPropertyValue(SystemProperty.LDAP_CONNECTION_URL, null);
  }


  /**
   * @return LDAP base element for user searches or null or empty string if not set.
   */
  public String getLDAPBaseElementForUserSearches() {
    return getSystemPropertyValue(SystemProperty.LDAP_BASE_ELEMENT_FOR_USER_SEARCHES, null);
  }


  /**
   * @return LDAP connection security level or null or empty string if not set.
   */
  public String getLDAPConnectionSecurityLevel() {
    final byte value = getSystemPropertyValue(SystemProperty.LDAP_CONNECTION_SECURITY_LEVEL, ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_DEFAULT);
    if (value == ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_DEFAULT) {
      return ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_DEFAULT_VALUE;
    }
    if (value == ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_NONE) {
      return ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_NONE_VALUE;
    }
    if (value == ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_SIMPLE) {
      return ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_SIMPLE_VALUE;
    }
    if (value == ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_STRONG) {
      return ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_STRONG_VALUE;
    }
    return ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_DEFAULT_VALUE;
  }


  /**
   * @return LDAP connection user name or null or empty string if not set.
   */
  public String getLDAPConnectionUserName() {
    return getSystemPropertyValue(SystemProperty.LDAP_CONNECTION_USER_NAME, null);
  }


  /**
   * @return LDAP credentials digest or null or empty string if not set.
   */
  public String getLDAPCredentialsDigest() {
    final byte value = getSystemPropertyValue(SystemProperty.LDAP_CREDENTIALS_DIGEST, ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_NOT_SELECTED);
    if (value == ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_NOT_SELECTED) {
      return null;
    }
    if (value == ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD2) {
      return ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD2_VALUE;
    }
    if (value == ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD5) {
      return ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD5_VALUE;
    }
    if (value == ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_SHA1) {
      return ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_SHA1_VALUE;
    }
    return null;
  }


  /**
   * @return true if the whole LDAP tree should be searched.
   */
  public boolean getLDAPSearchEntireSubtree() {
    return getSystemPropertyValue(SystemProperty.LDAP_SEARCH_ENTIRE_SUBTREE, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if credentials digests should be used
   * @see #getLDAPCredentialsDigest()
   */
  public boolean getLDAPUseCredentialsDigest() {
    return getSystemPropertyValue(SystemProperty.LDAP_USE_CREDENTIALS_DIGEST, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return LDAP user distinguished name template.
   */
  public String getLDAPUserDistinguishedNameTemplate() {
    return getSystemPropertyValue(SystemProperty.LDAP_USER_DISTINGUISHED_NAME_TEMPLATE, null);
  }


  /**
   * @return LDAP attribute name for user password.
   */
  public String getLDAPUserPasswordAttributeName() {
    return getSystemPropertyValue(SystemProperty.LDAP_USER_PASSWORD_ATTRIBUTE_NAME, null);
  }


  /**
   * @return LDAP user search template.
   */
  public String getLDAPUserSearchTemplate() {
    return getSystemPropertyValue(SystemProperty.LDAP_USER_SEARCH_TEMPLATE, null);
  }


  /**
   * @return An ID of a group that a first-time user should be
   *         added or {@link Group#UNSAVED_ID} if not defined.
   */
  public int getLDAPAddFirstTimeUserToGroupID() {
    return getSystemPropertyValue(SystemProperty.LDAP_ADD_FIRST_TIME_USER_TO_GROUP, Group.UNSAVED_ID);
  }


  /**
   * @return LDAP version or null if is not set or is set to
   *         default.
   */
  public String getLDAPVersion() {
    final byte code = getSystemPropertyValue(SystemProperty.LDAP_VERSION, ConfigurationConstants.LDAP_VERSION_DEFAULT);
    return new LDAPVersionCodeToValueConverter().convert(code);
  }


  /**
   * @return LDAP referral or null if is not set or is set to
   *         default.
   */
  public String getLDAPReferrals() {
    final byte code = getSystemPropertyValue(SystemProperty.LDAP_REFERRAL, ConfigurationConstants.LDAP_REFERRAL_DEFAULT);
    return new LDAPReferralCodeToValueConverter().convert(code);
  }


  /**
   * @return true if change list descriptions should be shown to
   *         anonymous users.
   */
  public boolean isHideChangeListDescriptionsFromAnonymousUsers() {
    return getSystemPropertyValue(SystemProperty.HIDE_CHANGE_DESCRIPTIONS_FROM_ANONYMOUS, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * Defines how we should lookup a user.
   * <p/>
   * Can be either {@link
   * ConfigurationConstants#LDAP_USER_LOOKUP_BY_DN_TEMPLATE} or
   * {@link ConfigurationConstants#LDAP_USER_LOOKUP_BY_SEARCH}
   */
  public byte getLDAPUserLookupMode() {
    final boolean lookupByDN = getSystemPropertyValue(SystemProperty.LDAP_USER_LOOKUP_MODE_DN, SystemProperty.RADIO_UNSELECTED).equals(SystemProperty.RADIO_SELECTED);
    if (lookupByDN) {
      return ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE;
    }

    final boolean lookupBySearch = getSystemPropertyValue(SystemProperty.LDAP_USER_LOOKUP_MODE_SEARCH, SystemProperty.RADIO_UNSELECTED).equals(SystemProperty.RADIO_SELECTED);
    if (lookupBySearch) {
      return ConfigurationConstants.LDAP_USER_LOOKUP_BY_SEARCH;
    }

    //default
    return ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE;
  }


  /**
   * Defines mandatory e-mail attribute name for LDAP authentication.
   *
   * @return e-mail attribute name for LDAP authentication.
   */
  public String getLDAPUserEmailAttributeName() {
    return getSystemPropertyValue(SystemProperty.LDAP_USER_EMAIL_ATTRIBUTE_NAME, null);
  }


  /**
   * @return initial number of change lists.
   */
  public int getInitialNumberOfChangeLists() {
    return getSystemPropertyValue(SystemProperty.INITIAL_NUMBER_OF_CHANGELISTS, SystemProperty.DEFAULT_INITIAL_NUMBER_OF_CHANGELISTS);
  }


  /**
   * @return maximum number of change lists.
   */
  public int getMaxNumberOfChangeLists() {
    return getSystemPropertyValue(SystemProperty.MAX_NUMBER_OF_CHANGE_LISTS, SystemProperty.DEFAULT_MAX_NUMBER_OF_CHANGE_LISTS);
  }


  /**
   * Creates a new sequence number for active build
   */
  public int incrementBuildSequenceNumber() {
    return ((Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        try {
          // Fix possible mismatch for active build sequence. See #1384 - "Cannot copy build" for more information.
          final Query query = session.createQuery("select max(ab.sequenceNumber) from ActiveBuild ab");
          final List list = query.list();
          if (!list.isEmpty()) {
            final Integer number = (Integer) list.get(0);
            if (number != null) {
              final int maxSequenceNumber = number.intValue();
              final SystemProperty systemProperty = getSystemProperty(SystemProperty.BUILD_SEQUENCE_NUMBER);
              final int currentSequenceNumber = systemProperty.getPropertyValueAsInt();
              if (maxSequenceNumber > currentSequenceNumber) {
                systemProperty.setPropertyValue(maxSequenceNumber);
                session.flush();
              }
            }
          }
          final BuildSequenceNumberIncrementer incrementer = new BuildSequenceNumberIncrementer();
          return new Integer(incrementer.incrementBuildSequenceNumber(session.connection()));
        } finally {
          // invalidate cache for system property because it
          // was changed by BuildSequenceNumberIncrementer
          getSystemPropertyCache().remove(SystemProperty.BUILD_SEQUENCE_NUMBER);
          CacheManager.getInstance().getCache("org.parabuild.ci.object.SystemProperty").remove(SystemProperty.BUILD_SEQUENCE_NUMBER);
        }
      }
    })).intValue();
  }


  /**
   * @return true if project link should be displayed on the
   *         top navigation menu.
   */
  public boolean isProjectDisplayEnabled() {
    return getSystemPropertyValue(SystemProperty.SHOW_PROJECTS_LINK,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if deleting projects is allowed.
   */
  public boolean isProjectDeletionEnabled() {
    return getSystemPropertyValue(SystemProperty.ALLOW_DELETING_PROJECTS,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if build promotion tab should be shown.
   */
  public boolean isBuildPromotionEnabled() {
    return getSystemPropertyValue(SystemProperty.ENABLE_BUILD_PROMOTION,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if anonymous access to protected feeds is
   *         enabled.
   */
  public boolean isAnonymousAccessToProtectedFeedsIsEnabled() {
    return getSystemPropertyValue(SystemProperty.ENABLE_ANONYMOUS_ACCESS_TO_PROTECTED_FEEDS,
            SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isHideChangeListFilesFromAnonymousUsers() {
    return getSystemPropertyValue(SystemProperty.HIDE_CHANGE_FILES_FROM_ANONYMOUS, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return number of lines to quote from the build log in case of a build error.
   */
  public int getErrorLogQuoteSize() {
    return getSystemPropertyValue(SystemProperty.ERROR_LOG_QUOTE_SIZE,
            ConfigurationConstants.DEFAULT_ERROR_LOG_QUOTE_SIZE);
  }


  /**
   * @return true if parallel builds should be shown in the build list.
   */
  public boolean showParallelInListView() {
    return getSystemPropertyValue(SystemProperty.SHOW_PARALLEL_BUILDS_IN_LIST_VIEW, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if merges link should be shown.
   */
  public boolean isShowingMergesEnabled() {
    return getSystemPropertyValue(SystemProperty.SHOW_MERGE_STATUSES, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * Number of builds in a dashboard row.
   */
  public int getDashboardRowSize() {
    return getSystemPropertyValue(SystemProperty.DASHBOARD_ROW_SIZE,
            ConfigurationConstants.DEFAULT_DASHBOARD_ROW_SIZE);
  }


  public int getTailWindowSize() {
    return Math.min(ConfigurationConstants.TAIL_BUFFER_SIZE, getSystemPropertyValue(SystemProperty.TAIL_WINDOW_SIZE,
            ConfigurationConstants.DEFAULT_TAIL_WINDOW_SIZE));
  }


  /**
   * @return true if change list numbers should be used as
   *         build numbers.
   */
  public boolean useChangeListNumberAsBuildnumber() {
    return getSystemPropertyValue(SystemProperty.USE_CHANGELIST_NUMBER_AS_BUILD_NUMBER, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if an XML log format should be used to poll Subversion changes.
   */
  public boolean useXMLLogFormatForSubversion() {
    return getSystemPropertyValue(SystemProperty.USE_XML_LOG_FORMAT_FOR_SUBVERSION, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isLoggingCleanupTimingEnabled() {
    return getSystemPropertyValue(SystemProperty.ENABLE_LOGGING_ARCHIVE_CLEANUP_TIMING, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if publishing commands are enabled.
   */
  public boolean isPublishingCommandsEnabled() {
    return getSystemPropertyValue(SystemProperty.ENABLE_PUBLISHING_COMMANDS, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * @return true if VCS names are case sensitive.
   */
  public boolean isCaseSensitiveVCSNames() {
    return getSystemPropertyValue(SystemProperty.CASE_SENSITIVE_VCS_USER_NAMES, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * Returns true if showing agents is enabled.
   *
   * @return true if showing agents is enabled.
   */
  public boolean isShowingAgentsEnabled() {
    return getSystemPropertyValue(SystemProperty.ENABLE_SHOWING_AGENTS, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * {@inheritDoc}
   */
  public int getAgentStatusColums() {
    return getSystemPropertyValue(SystemProperty.AGENT_STATUS_COLUMNS, 3);
  }


  /**
   * {@inheritDoc}
   */
  public int getAgentStatusImageHeightPixels() {
    return getSystemPropertyValue(SystemProperty.AGENT_STATUS_WIDTH_PIXELS, 250);
  }


  /**
   * {@inheritDoc}
   */
  public int getAgentStatusImageWidthPixels() {
    return getSystemPropertyValue(SystemProperty.AGENT_STATUS_HEIGHT_PIXELS, 400);
  }


  public boolean isShowBuildInstructions() {
    return getSystemPropertyValue(SystemProperty.SHOW_BUILD_INSTRUCTIONS, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isShowIPAddressOnBuildStatusList() {
    return getSystemPropertyValue(SystemProperty.SHOW_IP_ADDRESS_ON_BUILD_STATUS_LIST, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isShowNextBuildTimeOnBuildStatusList() {
    return getSystemPropertyValue(SystemProperty.SHOW_NEXT_BUILD_TIME_ON_BUILD_STATUS_LIST, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isQueueManualStartRequests() {
    return getSystemPropertyValue(SystemProperty.QUEUE_SERIALIZED_BUILDS, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  /**
   * Returns true if automatic agent upgrade is enabled.
   *
   * @return true if automatic agent upgrade is enabled.
   */
  public boolean isAutomaticAgentUpgradeEnabled() {
    return getSystemPropertyValue(SystemProperty.AUTOMATIC_AGENT_UPGRADE, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isCheckCustomCheckoutDirectoriesForDuplicates() {
    return getSystemPropertyValue(SystemProperty.CHECK_CUSTOM_CHECKOUT_DIRS_FOR_DUPLICATES, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isUseLDAPToLookUpVCSUserEmails() {
    return getSystemPropertyValue(SystemProperty.LDAP_USE_TO_LOOKUP_VCS_USER_EMAIL, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isShowNumberAndChangeListOnDashboard() {
    return getSystemPropertyValue(SystemProperty.SHOW_BUILD_AND_CHANGE_LIST_NUMBER_ON_DASHBOARD, SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isRoundRobinLoadBalancing() {
    return getSystemPropertyValue(SystemProperty.ROUND_ROBIN_LOAD_BALANCING, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isCustomBuildNameValidation() {
    return getSystemPropertyValue(SystemProperty.CUSTOM_BUILD_NAME_VALIDATION, SystemProperty.RADIO_UNSELECTED).equals(SystemProperty.RADIO_SELECTED);
  }


  public boolean isCustomVariableNameValidation() {
    return getSystemPropertyValue(SystemProperty.CUSTOM_VARIABLE_NAME_VALIDATION, SystemProperty.RADIO_UNSELECTED).equals(SystemProperty.RADIO_SELECTED);
  }


  public String getCustomBuildNameRegex() {
    return getSystemPropertyValue(SystemProperty.CUSTOM_BUILD_NAME_REGEX_TEMPLATE, StringUtils.REGEX_STRICT_NAME);
  }


  public String getCustomVariableNameRegex() {
    return getSystemPropertyValue(SystemProperty.CUSTOM_VARIABLE_NAME_REGEX_TEMPLATE, StringUtils.REGEX_STRICT_NAME);
  }


  public int getMaxParallelSystemUpgrades() {
    return getSystemPropertyValue(SystemProperty.MAX_PARALLEL_UPGRADES, 2);
  }


  public boolean isNoCheckoutBuildEnabled() {
    return getSystemPropertyValue(SystemProperty.ENABLE_NO_CHECKOUT_BUILDS, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public int getDefaultMaxRecentBuilds() {
    return getSystemPropertyValue(SystemProperty.DEFAULT_MAX_RECENT_BUILDS, ConfigurationConstants.DEFAULT_MAX_RECENT_BUILD);
  }


  public boolean isUseGitUserEmail() {
    return getSystemPropertyValue(SystemProperty.USE_GIT_USER_E_MAIL, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public void createSystemPropertyIfDoesNotExist(final String propertyName, final String propertyValue) {
    final SystemProperty systemProperty = getSystemProperty(propertyName);
    if (systemProperty == null) {
      createSystemProperty(propertyName, propertyValue);
    }
  }


  public void createSystemProperty(final String propertyName, final String propertyValue) {
    final SystemProperty systemProperty = new SystemProperty();
    systemProperty.setPropertyName(propertyName);
    systemProperty.setPropertyValue(propertyValue);
    saveSystemProperty(systemProperty);
  }


  public void deleteSystemProperty(final String name) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete("from SystemProperty sp where sp.propertyName = ?", name, Hibernate.STRING);
        getSystemPropertyCache().remove(name);
        return null;
      }
    });
  }


  public void createOrUpdateSystemProperty(final String propertyName, final int propertyValue) {
    createOrUpdateSystemProperty(propertyName, Integer.toString(propertyValue));
  }


  public void createOrUpdateSystemProperty(final String propertyName, final String propertyValue) {
    SystemProperty systemProperty = getSystemProperty(propertyName);
    if (systemProperty == null) {
      systemProperty = new SystemProperty();
      systemProperty.setPropertyName(propertyName);
    }
    systemProperty.setPropertyValue(propertyValue);
    saveSystemProperty(systemProperty);
  }


  /**
   * @return true if Parabuild will notify build administrators about system errors.
   */
  public boolean isNotifyUsersWithEditRightsAboutSystemErrors() {
    return getSystemPropertyValue(SystemProperty.NOTIFY_USERS_WITH_EDIT_RIGHTS_ABOUT_SYSTEM_ERRORS,
            SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public boolean isRespectIntermediateStepFailure() {
    return getSystemPropertyValue(SystemProperty.RESPECT_INTERMEDIATE_STEP_FAILURE,
            SystemProperty.OPTION_CHECKED).equals(SystemProperty.OPTION_CHECKED);
  }


  public int getLastEnteredAgentPort() {
    return getSystemPropertyValue(SystemProperty.LAST_ENTERED_AGENT_PORT, 8080);
  }


  /**
   * @return system property by name or default value if not
   *         found.
   */
  private String getSystemPropertyValueFromDB(final String propertyName, final String defaultValue) {
    return (String) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select sp.propertyValue from SystemProperty as sp " +
                " where sp.propertyName = ?");
        q.setString(0, propertyName);
        q.setCacheable(true);
        final String result = (String) q.uniqueResult();
        if (!StringUtils.isBlank(result)) {
          return result;
        }
        return defaultValue;
      }
    });
  }
}
