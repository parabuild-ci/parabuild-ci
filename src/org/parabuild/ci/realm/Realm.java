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
package org.parabuild.ci.realm;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.ConfigurationFile;
import org.parabuild.ci.configuration.HSQLDBUtils;
import org.parabuild.ci.configuration.SystemConstants;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 */
public final class Realm extends RealmBase {


  /**
   * Descriptive information about this Realm implementation.
   */
  private static final String realmInfo = "org.parabuild.ci.realm.Realm/2.0";


  /**
   * Descriptive information about this Realm implementation.
   */
  private static final String name = "Realm";


  /**
   * The set of valid Principals for this Realm, keyed by user
   * name.
   */
  private Map principals = new HashMap(111);

  // ------------------------------------------------------------- Properties


  /**
   * Return descriptive information about this Realm
   * implementation and the corresponding version number, in the
   * format <code>&lt;description&gt;/&lt;version&gt;</code>.
   */
  public String getInfo() {
    return realmInfo;
  }

  // --------------------------------------------------------- Public Methods


  /**
   * Return the Principal associated with the specified username
   * and credentials, if there is one; otherwise return
   * <code>null</code>.
   *
   * @param username    Username of the Principal to look up
   * @param credentials Password or other credentials to use in
   *                    authenticating this username
   */
  public Principal authenticate(final String username, final String credentials) {
//    System.out.println("DEBUG: username: " + username);
//    System.out.println("DEBUG: credentials: " + credentials);

    if (username == null || credentials == null) return null;

    // check if we have to reload
    if ("true".equals(System.getProperty(SystemConstants.SYSTEM_PROPERTY_RELOAD_PRINCIPAL, "true"))) {
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_RELOAD_PRINCIPAL, "false");
      reloadPrincipals();
    }

    // get from map
    GenericPrincipal principal = null;
    synchronized (this) {
      principal = (GenericPrincipal) principals.get(username.trim());
//      System.out.println("DEBUG: principal 1: " + principal);
      if (principal == null) return null;
    }

    // validate
    boolean validated = false;
    if (principal != null) { // NOPMD
      String digestedPrincipal = null;
      try {
        digestedPrincipal = StringUtils.digest(credentials);
      } catch (final NoSuchAlgorithmException e) {
        System.out.println("Error: " + toString(e)); // NOPMD
      }
      if (digestedPrincipal == null) return null;
//      System.out.println("DEBUG: digestedPrincipal: " + digestedPrincipal);
      // Hex hashes should be compared case-insensitive
      final String principalPassword = principal.getPassword();
      validated = digestedPrincipal.equalsIgnoreCase(principalPassword);
//      System.out.println("DEBUG: validated: " + validated);
    }

    if (validated) {
      return principal;
    } else {
      return null;
    }
  }

  // ------------------------------------------------------ Protected Methods


  /**
   * Return a short name for this Realm implementation.
   */
  protected String getName() {
    return name;
  }


  /**
   * Return the password associated with the given principal's
   * user name.
   */
  protected String getPassword(final String username) {
    final GenericPrincipal principal =
            (GenericPrincipal) principals.get(username);
    if (principal != null) {
      return principal.getPassword();
    } else {
      return null;
    }

  }


  /**
   * Return the Principal associated with the given user name.
   */
  protected Principal getPrincipal(final String username) {
    return (Principal) principals.get(username);
  }

  // ------------------------------------------------------ Lifecycle Methods


    /**
   * Prepare for active use of the public methods of this
   * Component.
   *
   * @throws LifecycleException if this
   *                            component detects a fatal error that prevents it from being
   *                            started
   */
    protected void startInternal() throws LifecycleException {

    // NOTE: simeshev@parabuilci.org - 07/25/2004 - we just create a
    // default user with default password. It does not provide
    // security per say as it's relatively easy to decompile and
    // figure out that it's just an parabuild/parabuild configuration.
    // We just keep the realm in place for possible future use.
    reloadPrincipals();
    System.setProperty(SystemConstants.SYSTEM_PROPERTY_RELOAD_PRINCIPAL, "false");

    // Perform normal superclass initialization
    super.startInternal();
  }


  private synchronized void reloadPrincipals() {
    this.principals = new HashMap(111); // clean up
    Connection conn = null; // NOPMD
    PreparedStatement pstmt = null;  // NOPMD
    ResultSet rs = null;  // NOPMD
    try {
      // add default agent password
      final String string = ConfigurationFile.getInstance().getBuildManagerAddress();
      if (!(string == null || string.trim().isEmpty())) { // NOPMD
        addUser(RealmConstants.DEFAULT_BUILDER_USER, StringUtils.digest(RealmConstants.DEFAULT_BUILDER_PASSWORD), RealmConstants.PARABUILD_MANAGER_ROLE);
        addUser(RealmConstants.DEFAULT_AGENT_MANAGER_USER, StringUtils.digest(RealmConstants.DEFAULT_AGENT_MANAGER_PASSWORD), RealmConstants.PARABUILD_MANAGER_ROLE);
      }

      conn = HSQLDBUtils.createHSQLConnection(ConfigurationConstants.DATABASE_HOME);
      pstmt = conn.prepareStatement("select NAME, PASSWORD from USERS where ENABLED='Y'");
      rs = pstmt.executeQuery();
      while (rs.next()) {
        final String userName = rs.getString(1);
        final String password = rs.getString(2);
//        System.out.println("DEBUG: password: " + password);
//        System.out.println("DEBUG: userName: " + userName);
        addUser(userName.trim(), password.trim(), RealmConstants.PARABUILD_USER_ROLE);
      }
    } catch (final Exception e) {
      e.printStackTrace(); // NOPMD - nowhere to report but to stdout
    } finally {
      closeHard(rs);
      closeHard(pstmt);
      closeHard(conn);
    }
  }


  /**
   * Add a new user to the map
   *
   * @param username User's username
   * @param password User's password (clear text)
   * @param roles    Comma-delimited set of roles associated with
   *                 this user
   */
  private void addUser(final String username, final String password, final String roles) {

    // Accumulate the list of roles for this user
    String rolesToProcess = roles + ',';
    final ArrayList list = new ArrayList(1);
    while (true) {
      final int comma = rolesToProcess.indexOf(',');
      if (comma < 0) break;
      final String role = rolesToProcess.substring(0, comma).trim();
      list.add(role);
      rolesToProcess = rolesToProcess.substring(comma + 1);
    }

    // Construct and cache the Principal for this user
    final GenericPrincipal principal = new GenericPrincipal(username, password, list);
    principals.put(username, principal);
  }

  /**
   * @return Agent password configured through Java system property
   *         defined in SystemConstants.SYSTEM_PROPERTY_BUILDER_PASSWORD.
   *         <p/>
   *         NOTE: simeshev@parabuilci.org - currently (07/25/2004) is
   *         not used and is left for future use.
   */
//  private String getConfiguredBuilderPassword() {
//    // init random
//    Random random = new Random(System.currentTimeMillis());
//    random.nextLong();
//
//    // get agent passord or set it to random value
//    String password = System.getProperty(SystemConstants.SYSTEM_PROPERTY_BUILDER_PASSWORD, Long.toString(random.nextLong()));
//    return password;
//  }


  private static void closeHard(final Connection conn1) {
    if (conn1 != null) {
      try {
        conn1.close();
      } catch (final SQLException ignore) { // NOPMD EmptyCatchBlock
      }
    }
  }


  private static String toString(final Exception e) {
    final String result;
    final String message = e.toString();
    final int i = message.indexOf("ion: ");
    if (i >= 0) {
      result = message.substring(i + 5);
    } else {
      result = message;
    }
    return result;
  }


  private static void closeHard(final PreparedStatement pstmt) {
    if (pstmt != null) {
      try {
        pstmt.close();
      } catch (final SQLException ignore) { // NOPMD EmptyCatchBlock
      }
    }
  }


  private static void closeHard(final ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (final SQLException ignore) { // NOPMD EmptyCatchBlock
      }
    }
  }
}
