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
package org.parabuild.ci.notification;

import java.util.*;
import org.apache.commons.logging.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;

/**
 * This notification manager responsible for providing
 * notification services via Jabber IM protocol.
 */
public final class JabberNotificationManager extends AbstractInstantMessagingNotificationManager {

  private static final Log log = LogFactory.getLog(JabberNotificationManager.class);
  private final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();


  /**
   * @return server type this manager serves.
   *
   * @see User#IM_TYPE_NONE - server is not set
   * @see User#IM_TYPE_JABBER - Jabber XMPP server.
   */
  protected int serverType() {
    return User.IM_TYPE_JABBER;
  }


  /**
   * Sends an IM message to a String collection of IM addresses.
   *
   * @param addresses a String collection of IM addresses.
   * @param body
   */
  protected void send(final Collection addresses, final StringBuffer body) {
    if (addresses.isEmpty()) return;
    if (log.isDebugEnabled()) log.debug("sending to jabber list");
    if (log.isDebugEnabled()) log.debug("addresses: " + addresses);
    XMPPConnection conn = null;
    try {
      Chat.setFilteredOnThreadID(false);
      conn = connectToJabber();
      if (log.isDebugEnabled()) log.debug("conn: " + conn);
      if (log.isDebugEnabled()) log.debug("addresses: " + addresses);
      for (final Iterator i = addresses.iterator(); i.hasNext();) {
        sendToAddress(conn, getRoster(conn), (String)i.next(), body);
      }
    } catch (final XMPPException e) {
      // TODELETE: when debugging is done
      if (log.isDebugEnabled()) log.debug(e);
      reportSendError(e);
    } finally {
      closeHard(conn);
    }
  }


  /**
   * Sends a message to a single user.
   *
   * @param address
   * @param body
   */
  protected void send(final String address, final StringBuffer body) {
    if (log.isDebugEnabled()) log.debug("sending to jabber");
    if (log.isDebugEnabled()) log.debug("address: " + address);
    XMPPConnection conn = null;
    try {
      Chat.setFilteredOnThreadID(false);
      conn = connectToJabber();
      if (log.isDebugEnabled()) log.debug("conn: " + conn);
      sendToAddress(conn, getRoster(conn), address, body);
    } catch (final XMPPException e) {
      // TODELETE: when debugging is done
      if (log.isDebugEnabled()) log.debug(e);
      reportSendError(e);
    } finally {
      closeHard(conn);
    }
  }


  /**
   * Obtains roster from the XMPP connection and sets
   * subscription mode to SUBSCRIPTION_ACCEPT_ALL.
   */
  private static Roster getRoster(final XMPPConnection conn) {
    final Roster roster = conn.getRoster();
    if (roster != null) {
      // set subscription mode to acccept all requests so
      // that a build admin should not do it manually.
      if (!(roster.getSubscriptionMode() == Roster.SUBSCRIPTION_ACCEPT_ALL)) {
        roster.setSubscriptionMode(Roster.SUBSCRIPTION_ACCEPT_ALL);
      }
    }
    return roster;
  }


  /**
   * @return true if server of the serverType() is configured and
   *         enabled.
   */
  public final boolean isServerEnabled() {
    final String serverName = serverName();
    final String serverDisabled = systemCM.getSystemPropertyValue(SystemProperty.JABBER_DISABLED, SystemProperty.OPTION_UNCHECKED);
    return !serverDisabled.equals(SystemProperty.OPTION_CHECKED)
      && !StringUtils.isBlank(serverName);
  }


  /**
   * Connects to the configured jabber server.
   *
   * @return XMPPConnection
   *
   * @throws XMPPException
   * @see #serverName()
   * @see #serverPort()
   * @see #serverLoginName()
   * @see #serverLoginName()
   */
  private XMPPConnection connectToJabber() throws XMPPException {
    final XMPPConnection con = new XMPPConnection(serverName(), serverPort());
    if (log.isDebugEnabled()) log.debug("con: " + con);
    final String loginName = serverLoginName();
    final String loginPassword = serverLoginPassword();
    con.login(loginName, loginPassword);
    if (log.isDebugEnabled()) log.debug("con: " + con);
    return con;
  }


  /**
   * Sends a message to a user.
   *
   * @param con connection
   * @param roster roster
   * @param address user's address
   * @param body message body.
   */
  private void sendToAddress(final XMPPConnection con, final Roster roster, final String address, final StringBuffer body) throws XMPPException {
    if (log.isDebugEnabled()) log.debug("sending to: " + address);
    if (log.isDebugEnabled()) log.debug("roster: " + roster);
    // check if we should send
    if (!userPresent(roster, address) && !allowedToSendIfNoPresense()) return;
    // create chat
    final Chat chat = con.createChat(address);
    if (log.isDebugEnabled()) log.debug("chat: " + chat);
    // cover-ass validation
    if (chat == null) return;
    chat.sendMessage(body.toString());
  }


  /**
   * Helper method to check if a user is present.
   *
   * @return true if user is present.
   */
  private static boolean userPresent(final Roster roster, final String address) {
    final Presence presence = roster.getPresence(address);
    if (log.isDebugEnabled()) log.debug("presense: " + presence);
    if (presence == null) return false;
    final Presence.Mode mode = presence.getMode();
    if (log.isDebugEnabled()) log.debug("mode: " + mode);
    if (mode == null) return false;
    return mode.equals(Presence.Mode.AVAILABLE) || mode.equals(Presence.Mode.CHAT);
  }


  /**
   * Helper method.
   *
   * @return Jabber server name or null if not defined.
   */
  private String serverName() {
    return systemCM.getSystemPropertyValue(SystemProperty.JABBER_SERVER_NAME, null);
  }


  private int serverPort() {
    return systemCM.getSystemPropertyValue(SystemProperty.JABBER_SERVER_PORT,
      ConfigurationConstants.DEFAULT_JABBER_PORT);
  }


  /**
   * @return true if server configuration permits sending a
   *         meesage even if users's presence cannot be
   *         identified.
   */
  private boolean allowedToSendIfNoPresense() {
    return systemCM.getSystemPropertyValue(SystemProperty.JABBER_SEND_NO_PRESENCE, SystemProperty.OPTION_CHECKED)
      .equals(SystemProperty.OPTION_CHECKED);
  }


  private String serverLoginName() {
    return systemCM.getSystemPropertyValue(SystemProperty.JABBER_LOGIN_NAME, "");
  }


  private String serverLoginPassword() {
    final String encryptedPassword = systemCM.getSystemPropertyValue(SystemProperty.JABBER_LOGIN_PASSWORD, null);
    if (encryptedPassword == null) return "";
    return SecurityManager.decryptPassword(encryptedPassword);
  }


  /**
   * Closes XMPPConnection ignoring exceptions.
   */
  private static void closeHard(final XMPPConnection con) {
    if (con != null) {
      try {
        con.close();
      } catch (final Exception e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  /**
   * Helper method
   *
   * @param e Exception to report to administrator.
   */
  private static void reportSendError(final Exception e) {
    final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
    final Error error = new Error("Error sending Jabber message: " + StringUtils.toString(e));
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_NOTIFICATION);
    error.setSendEmail(false);
    error.setDetails(e);
    errorManager.reportSystemError(error);
  }


  public String toString() {
    return "JabberNotificationManager{" +
      "systemCM=" + systemCM +
      '}';
  }
}
