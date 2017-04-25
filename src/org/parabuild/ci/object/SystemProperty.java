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

import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Stored build configuration
 *
 * @hibernate.class table="SYSTEM_PROPERTY" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 * @noinspection StaticInheritance, HardcodedLineSeparator
 */
public final class SystemProperty implements Externalizable, ObjectConstants {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(SerializationUtils.class); // NOPMD
  private static final long serialVersionUID = 4471337135531309005L; // NOPMD

  public static final int DEFAULT_BUILD_STATUS_REFRESH_RATE = 10;
  public static final int DEFAULT_INITIAL_NUMBER_OF_CHANGELISTS = 1;
  public static final int DEFAULT_MAX_CHANGE_LIST_SIZE = 100;
  public static final int DEFAULT_MAX_NUMBER_OF_CHANGE_LISTS = 10;
  public static final int DEFAULT_MAX_COOLDOWN_TRIES = 5;
  public static final int DEFAULT_MINIMUM_RESULTS_RETENTION = 9999;
  public static final int UNLIMITED_MAX_NUMBER_OF_CHANGE_LISTS = Integer.MAX_VALUE;
  public static final int UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS = Integer.MAX_VALUE;
  public static final String DEFAULT_RETRY_VCS_COMMAND_INTERVAL = "10";
  public static final String DEFAULT_RETRY_VCS_COMMAND_TIMES = "0";
  public static final String DEFAULT_RETRY_VCS_COMMAND_PATTERNS = "chmod:.*p4tickets\\.txt: Access is denied\nsvn:.*Could not read status line";

  public static final byte MESSAGE_PRIORITY_NORMAL = (byte) 0;
  public static final byte MESSAGE_PRIORITY_HIGH = (byte) 1;

  public static final String DEFAULT_BUILD_STATUS_REFRESH_RATE_AS_STRING = Integer.toString(DEFAULT_BUILD_STATUS_REFRESH_RATE);

  public static final String ADVANCED_STOP_NOTIFICATION = "advanced.stop.notification";
  public static final String ALLOW_DELETING_PROJECTS = "allow.deleting.projects";
  public static final String AUTOMATIC_AGENT_UPGRADE = "automatic.agent.upgrade";
  public static final String BRANDING = "parabuild.branding";
  public static final String BUILD_ADMIN_EMAIL = "parabuild.admin.email";
  public static final String BUILD_ADMIN_NAME = "parabuild.admin.name";
  public static final String BUILD_MANAGER_HOST_NAME = "parabuild.build.manager.host.name";
  public static final String BUILD_SEQUENCE_NUMBER = "parabuild.build.sequence.number";
  public static final String BUILD_STATUS_REFRESH_SECS = "parabuild.build.status.refresh";
  public static final String CHANGE_LIST_DESCRIPTION_QUOTE_LENGTH = "parabuild.change.list.description.quote.length";
  public static final String CHECK_CUSTOM_CHECKOUT_DIRS_FOR_DUPLICATES = "check.custom.checkout.dirs.for.duplicates";
  public static final String DASHBOARD_ROW_SIZE = "dashboard.row.size";
  public static final String DATE_FORMAT = "parabuild.date.format";
  public static final String DATE_TIME_FORMAT = "parabuild.date.time.format";
  public static final String DEFAULT_BUILD_STEP_TIMEOUT = "parabuild.default.build.step.timeout";
  public static final String DEFAULT_EMAIL_DOMAIN = "parabuild.default.email.domain";
  public static final String DEFAULT_MAX_RECENT_BUILDS = "default.max.recent.builds";
  public static final String DEFAULT_VCS_TIMEOUT = "parabuild.default.vcs.timeout";
  public static final String ENABLE_ADVANCED_BUILD_SETTING = "parabuild.enable.advanced.build.settings";
  public static final String ENABLE_ANONYMOUS_ACCESS_TO_PROTECTED_FEEDS = "enabble.anonymous.access.to.protected.feeds";
  public static final String ENABLE_BUILD_PROMOTION = "enable.build.promotion";
  public static final String ENABLE_DEBUGGING = "parabuild.enable.debugging";
  public static final String ENABLE_NO_CHECKOUT_BUILDS = "enable.no.checkout.builds";
  public static final String ENABLE_LOGGING_ARCHIVE_CLEANUP_TIMING = "enable.logging.cleanup.timing";
  public static final String ENABLE_PUBLISHING_COMMANDS = "enable.publishing.commands";
  public static final String ENABLE_SCHEDULE_GAP = "parabuild.enable.schedule.gap";
  public static final String ENABLE_SHOWING_AGENTS = "enable.showing.agents";
  public static final String ENABLE_TEST_LOGGING = "parabuild.enable.test.logging";
  public static final String ERROR_LINE_QUOTE_LENGTH = "parabuild.error.line.quote.length";
  public static final String ERROR_LOG_QUOTE_SIZE = "parabuild.error.log.quote.size";
  public static final String GENERATED_URL_PROTOCOL = "generated.url.protocol";
  public static final String INCLUDE_RESULTS_IN_MESSAGES = "parabuild.includes.results.in.messages";
  public static final String INITIAL_NUMBER_OF_CHANGELISTS = "parabuild.initial.number.of.change.lists";
  public static final String KEEP_SCM_LOGS = "parabuild.keep.scm.logs";
  public static final String LAST_ENTERED_AGENT_PORT = "last.entered.agent.port";
  public static final String MAX_CHANGELIST_SIZE = "parabuild.max.change.list.size";
  public static final String MAX_COOLDOWN_TRIES = "parabuild.max.cooldown.tries";
  public static final String MAX_NUMBER_OF_CHANGE_LISTS = "parabuild.maximum.number.of.changelists";
  public static final String MAX_PARALLEL_UPGRADES = "max.parallel.upgrades";
  public static final String NOTIFICATION_PREFIX = "parabuild.notification.prefix";
  public static final String NOTIFY_USERS_WITH_EDIT_RIGHTS_ABOUT_SYSTEM_ERRORS = "notify.users.with.edit.rights.about.system.errors";
  public static final String OUTPUT_ENCODING = "parabuild.output.encoding";
  public static final String QUEUE_SERIALIZED_BUILDS = "queue.serialized.builds";
  public static final String RESPECT_INTERMEDIATE_STEP_FAILURE = "respect.intermediate.step.failure";
  public static final String RESULT_SEQUENCE_NUMBER = "result.sequence.number";
  public static final String ROUND_ROBIN_LOAD_BALANCING = "round.robin.load.balancing";
  public static final String SCHEDULE_GAP_FROM = "parabuild.schedule.gap.from";
  public static final String SCHEDULE_GAP_TO = "parabuild.schedule.gap.to";
  public static final String SCHEMA_VERSION = "parabuild.schema.version";
  public static final String SHOW_BUILD_AND_CHANGE_LIST_NUMBER_ON_DASHBOARD = "show.details.on.dashboard";
  public static final String SHOW_BUILD_INSTRUCTIONS = "show.build.instructions";
  public static final String SHOW_IP_ADDRESS_ON_BUILD_STATUS_LIST = "show.ip.on.build.status.list";
  public static final String SHOW_MERGE_STATUSES = "show.merge.statuses";
  public static final String SHOW_NEXT_BUILD_TIME_ON_BUILD_STATUS_LIST = "show.next.build.time.on.build.status.list";
  public static final String SHOW_PARALLEL_BUILDS_IN_LIST_VIEW = "show.parallel.in.list.view";
  public static final String SHOW_PROJECTS_LINK = "show.projects.link";
  public static final String SHOW_RSS_LINKS = "parabuild.show.rss.links";
  public static final String SMTP_SERVER_ENCRYPTED_CONNECTION = "parabuild.smtp.server.enrypted.connection";
  public static final String SMTP_SERVER_NAME = "parabuild.smtp.server.name";
  public static final String SMTP_SERVER_PASSWORD = "parabuild.smtp.server.password";
  public static final String SMTP_SERVER_PORT = "parabuild.smtp.server.port";
  public static final String SMTP_SERVER_USER = "parabuild.smtp.server.user";
  public static final String TAIL_WINDOW_SIZE = "parabuild.tail.window.size";
  public static final String TEXT_LOG_MARKERS = "text.log.markers";
  public static final String USE_CHANGELIST_NUMBER_AS_BUILD_NUMBER = "use.changelist.as.build.number";
  public static final String USE_XML_LOG_FORMAT_FOR_SUBVERSION = "use.xml.logs.for.subversion";
  public static final String USE_GIT_USER_E_MAIL = "use.git.user.email";

  // Security settings
  public static final String ENABLE_ANONYMOUS_BUILDS = "parabuild.enable.anon.builds";
  public static final String HIDE_CHANGE_DESCRIPTIONS_FROM_ANONYMOUS = "parabuild.hide.change.descriptions";
  public static final String HIDE_CHANGE_FILES_FROM_ANONYMOUS = "parabuild.hide.change.files";

  // Instant Messaging settings
  public static final String JABBER_SERVER_NAME = "parabuild.jabber.server.name";
  public static final String JABBER_SERVER_PORT = "parabuild.jabber.server.port";
  public static final String JABBER_LOGIN_PASSWORD = "parabuild.jabber.login.password";
  public static final String JABBER_LOGIN_NAME = "parabuild.jabber.login.name";
  public static final String JABBER_DISABLED = "parabuild.jabber.disabled";
  public static final String JABBER_SEND_NO_PRESENCE = "parabuild.jabber.send.if.no.presence";

  // stability settings
  public static final String SERIALIZE_BUILDS = "parabuild.serialize.builds";
  public static final String MINIMUM_RESULTS_RETENTION = "parabuild.minimum.results.retention";

  // notification subject
  public static final String STEP_STARTED_SUBJECT = "step.started.subject.template";
  public static final String STEP_FINISHED_SUBJECT = "step.finished.subject.template";

  // message priorities
  public static final String MESSAGE_PRIORITY_FAILED_BUILD = "message.priority.failed.build";
  public static final String MESSAGE_PRIORITY_SYSTEM_ERROR = "message.priority.system.error";

  // LDAP properties
  public static final String LDAP_ADD_FIRST_TIME_USER_TO_GROUP = "ldap.add.to.group";
  public static final String LDAP_AUTHENTICATION_ENABLED = "ldap.enabled";
  public static final String LDAP_BASE_ELEMENT_FOR_USER_SEARCHES = "ldap.base.element.for.user.searches";
  public static final String LDAP_CONNECTION_PASSWORD = "ldap.conn.passwd";
  public static final String LDAP_CONNECTION_SECURITY_LEVEL = "ldap.security.level";
  public static final String LDAP_CONNECTION_URL = "ldap.conn.url";
  public static final String LDAP_CONNECTION_USER_NAME = "ldap.conn.user.name";
  public static final String LDAP_CREDENTIALS_DIGEST = "ldap.credentials.digest";
  public static final String LDAP_REFERRAL = "ldap.referral";
  public static final String LDAP_SEARCH_ENTIRE_SUBTREE = "ldap.search.entire.subtree";
  public static final String LDAP_USE_CREDENTIALS_DIGEST = "ldap.use.credentials.digest";
  public static final String LDAP_USE_TO_LOOKUP_VCS_USER_EMAIL = "ldap.use.to.lookup.vcs.user.email";
  public static final String LDAP_USER_DISTINGUISHED_NAME_TEMPLATE = "ldap.user.dn.template";
  public static final String LDAP_USER_EMAIL_ATTRIBUTE_NAME = "ldap.user.email.attr.name";
  public static final String LDAP_USER_LOOKUP_MODE_DN = "ldap.user.lookup.dn";
  public static final String LDAP_USER_LOOKUP_MODE_SEARCH = "ldap.user.lookup.search";
  public static final String LDAP_USER_PASSWORD_ATTRIBUTE_NAME = "ldap.user.passwd.attr.name";
  public static final String LDAP_USER_SEARCH_TEMPLATE = "ldap.user.search.template";
  public static final String LDAP_VERSION = "ldap.version";

  // Notification
  public static final String CASE_SENSITIVE_VCS_USER_NAMES = "case.sensitive.vcs.user.names";

  // Agent status page
  public static final String AGENT_STATUS_COLUMNS = "agent.status.colums";
  public static final String AGENT_STATUS_WIDTH_PIXELS = "agent.status.width.px";
  public static final String AGENT_STATUS_HEIGHT_PIXELS = "agent.status.height.px";

  // Retrying VCS commands 
  public static final String RETRY_VCS_COMMAND_TIMES = "retry.vcs.command.times";
  public static final String RETRY_VCS_COMMAND_INTERVAL = "retry.vcs.command.interval";
  public static final String RETRY_VCS_COMMAND_PATTERNS = "retry.vcs.command.patterns";

  // Build name validation
  public static final String CUSTOM_BUILD_NAME_VALIDATION = "custom.build.name.validation";
  public static final String CUSTOM_BUILD_NAME_REGEX_TEMPLATE = "custom.build.name.regex.template";
  public static final String DEFAULT_BUILD_NAME_VALIDATION = "default.build.name.validation";

  // Variable name validation
  public static final String CUSTOM_VARIABLE_NAME_REGEX_TEMPLATE = "custom.variable.name.regex.template";
  public static final String CUSTOM_VARIABLE_NAME_VALIDATION = "custom.variable.name.validation";
  public static final String DEFAULT_VARIABLE_NAME_VALIDATION = "default.variable.name.validation";

  private int propertyID = UNSAVED_ID;
  private String propertyName = null;
  private String propertyValue = null;
  private long propertyTimeStamp = 1L;


  /**
   * Default constructor.
   */
  public SystemProperty() {
  }


  public SystemProperty(final String propertyName, final String propertyValue) {
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }


  public SystemProperty(final String propertyName, final int propertyValue) {
    this(propertyName, Integer.toString(propertyValue));
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
   * Returns build name
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


  public int getPropertyValueAsInt() {
    return Integer.parseInt(propertyValue);
  }


  public void setPropertyValue(final int value) {
    propertyValue = Integer.toString(value);
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


  /**
   * {@inheritDoc}
   */
  public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
    propertyID = in.readInt();
    propertyTimeStamp = in.readLong();
    propertyName = SerializationUtils.readNullableUTF(in);
    propertyValue = SerializationUtils.readNullableUTF(in);
  }


  /**
   * {@inheritDoc}
   */
  public void writeExternal(final ObjectOutput out) throws IOException {
    out.writeInt(propertyID);
    out.writeLong(propertyTimeStamp);
    SerializationUtils.writeNullableUTF(propertyName, out);
    SerializationUtils.writeNullableUTF(propertyValue, out);
  }


  public String toString() {
    return "SystemProperty{" +
            "propertyID=" + propertyID +
            ", propertyName='" + propertyName + '\'' +
            ", propertyValue='" + propertyValue + '\'' +
            ", propertyTimeStamp=" + propertyTimeStamp +
            '}';
  }
}
