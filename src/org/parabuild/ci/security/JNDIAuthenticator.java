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
package org.parabuild.ci.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationConstants;

import javax.naming.AuthenticationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * <p>Authenticator that works with a directory server accessed
 * via the Java Naming and Directory Interface (JNDI) APIs. The
 * following constraints are imposed on the data structure in the
 * underlying directory server:</p>
 * <ul>
 * <p/>
 * <li>Each user that can be authenticated is represented by an individual
 * element in the top level <code>DirContext</code> that is accessed
 * via the <code>connectionURL</code> property.</li>
 * <p/>
 * <li>If a socket connection can not be made to the <code>connectURL</code>
 * an attempt will be made to use the <code>alternateURL</code> if it
 * exists.</li>
 * <p/>
 * <li>Each user element has a distinguished name that can be formed by
 * substituting the presented username into a pattern configured by the
 * <code>userPattern</code> property.</li>
 * <p/>
 * <li>Alternatively, if the <code>userPattern</code> property is not
 * specified, a unique element can be located by searching the directory
 * context. In this case:
 * <ul>
 * <li>The <code>userSearch</code> pattern specifies the search filter
 * after substitution of the username.</li>
 * <li>The <code>userBase</code> property can be set to the element that
 * is the base of the subtree containing users.  If not specified,
 * the search base is the top-level context.</li>
 * <li>The <code>userSubtree</code> property can be set to
 * <code>true</code> if you wish to search the entire subtree of the
 * directory context.  The default value of <code>false</code>
 * requests a search of only the current level.</li>
 * </ul>
 * </li>
 * <p/>
 * <li>The user may be authenticated by binding to the directory with the
 * username and password presented. This method is used when the
 * <code>userPassword</code> property is not specified.</li>
 * <p/>
 * <li>The user may be authenticated by retrieving the value of an attribute
 * from the directory and comparing it explicitly with the value presented
 * by the user. This method is used when the <code>userPassword</code>
 * property is specified, in which case:
 * <ul>
 * <li>The element for this user must contain an attribute named by the
 * <code>userPassword</code> property.
 * <li>The value of the user password attribute is either a cleartext
 * String, or the result of passing a cleartext String through the
 * <code>RealmBase.digest()</code> method (using the standard digest
 * support included in <code>RealmBase</code>).
 * <li>The user is considered to be authenticated if the presented
 * credentials (after being passed through
 * <code>RealmBase.digest()</code>) are equal to the retrieved value
 * for the user password attribute.</li>
 * </ul></li>
 * <p/>
 * <li>Each group of users that has been assigned a particular role may be
 * represented by an individual element in the top level
 * <code>DirContext</code> that is accessed via the
 * <code>connectionURL</code> property.  This element has the following
 * characteristics:
 * <ul>
 * <li>The set of all possible groups of interest can be selected by a
 * search pattern configured by the <code>roleSearch</code>
 * property.</li>
 * <li>The <code>roleSearch</code> pattern optionally includes pattern
 * replacements "{0}" for the distinguished name, and/or "{1}" for
 * the username, of the authenticated user for which roles will be
 * retrieved.</li>
 * <li>The <code>roleBase</code> property can be set to the element that
 * is the base of the search for matching roles.  If not specified,
 * the entire context will be searched.</li>
 * <li>The <code>roleSubtree</code> property can be set to
 * <code>true</code> if you wish to search the entire subtree of the
 * directory context.  The default value of <code>false</code>
 * requests a search of only the current level.</li>
 * <li>The element includes an attribute (whose name is configured by
 * the <code>roleName</code> property) containing the name of the
 * role represented by this element.</li>
 * </ul></li>
 * <p/>
 * <li>In addition, roles may be represented by the values of an attribute
 * in the user's element whose name is configured by the
 * <code>userRoleName</code> property.</li>
 * <p/>
 * <li>Note that the standard <code>&lt;security-role-ref&gt;</code> element in
 * the web application deployment descriptor allows applications to refer
 * to roles programmatically by names other than those used in the
 * directory server itself.</li>
 * </ul>
 */
public class JNDIAuthenticator {


  private static final Log log = LogFactory.getLog(JNDIAuthenticator.class);


  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  /**
   * Password digest agorithm for cased when a password is stored
   * in a digested form rather than clear text.
   */
  private String digestAlgorithm = null;

  /**
   * The type of authentication to use
   */
  private String connectionSecurityLevel = null;

  /**
   * The connection username for the server we will contact.
   */
  private String connectionPrincipal = null;


  /**
   * The connection password for the server we will contact.
   */
  private String connectionCredentials = null;


  /**
   * The connection URL for the server we will contact.
   */
  private String connectionURL = null;

//  /**
//   * The directory context linking us to our directory server.
//   */
//  private DirContext context = null;
  //
  //
  /**
   * The JNDI context factory used to acquire our InitialContext.  By
   * default, assumes use of an LDAP server using the standard JNDI LDAP
   * provider.
   */
  private String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";


  /**
   * How aliases should be dereferenced during search operations.
   */
  private String derefAliases = null;

  /**
   * Constant that holds the name of the environment property for specifying
   * the manner in which aliases should be dereferenced.
   */
  static final String JAVA_NAMING_DEREF_ALIASES = "java.naming.ldap.derefAliases";

  /**
   * Constatn to hold optional LDAP version.
   */
  private static final String JAVA_NAMING_LDAP_VERSION = "java.naming.ldap.version";

  /**
   * The protocol that will be used in the communication with the
   * directory server.
   */
  private String protocol = null;


  /**
   * How should we handle referrals?  Microsoft Active Directory can't handle
   * the default case, so an application authenticating against AD must
   * set referrals to "follow".
   */
  private String referrals = null;


  /**
   * The base element for user searches.
   */
  private String userBase = "";


  /**
   * The message format used to search for a user, with "{0}" marking
   * the spot where the username goes.
   */
  private String userSearchTemplate = null;

//  /**
//   * The MessageFormat object associated with the current
//   * <code>userSearch</code>.
//   */
//  private MessageFormat userSearchFormat = null;
  //
  //
  /**
   * Should we search the entire subtree for matching users?
   */
  private boolean searchEntireSubtree = false;


  /**
   * The attribute name used to retrieve the user password.
   */
  private String userPasswordAttributeName = null;


  /**
   * The attribute name used to retrieve the user email. This is a mandatory attribute.
   */
  private String userEmailAttributeName = null;


  /**
   * The message format used to form the distinguished name of a
   * user, with "{0}" marking the spot where the specified username
   * goes.
   */
  private String userDistinguishedNameTemplate = null;


  /**
   * The base element for role searches.
   */
  private String roleBase = "";


  /**
   * The MessageFormat object associated with the current
   * <code>roleSearch</code>.
   */
  private MessageFormat roleFormat = null;


  /**
   * The name of an attribute in the user's entry containing
   * roles for that user
   */
  private String userRoleName = null;


  /**
   * The name of the attribute containing roles held elsewhere
   */
  private String roleName = null;


  /**
   * The message format used to select roles for a user, with "{0}" marking
   * the spot where the distinguished name of the user goes.
   */
  private String roleSearch = null;


  /**
   * Should we search the entire subtree for matching memberships?
   */
  private boolean roleSubtree = false;

//  /**
//   * The current user pattern to be used for lookup and binding of a user.
//   */
//  private int i = 0;
  //
  //
  /**
   * Defines how we should lookup a user.
   *
   * @see ConfigurationConstants#LDAP_USER_LOOKUP_BY_DN_TEMPLATE
   * @see ConfigurationConstants#LDAP_USER_LOOKUP_BY_SEARCH
   */
  private byte userLookupMode = ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE;

  /**
   * If set to true authentication will throw an exception if e-mail attribute
   * does not exist or not set.
   */
  private boolean emailRequired = true;

  /**
   * LDAP version.
   */
  private String ldapVersion = null;


  /**
   * Constructor.
   *
   * @param userLookupMode defines how we should lookup a user.
   *                       Can be either {@link
   *                       ConfigurationConstants#LDAP_USER_LOOKUP_BY_DN_TEMPLATE} or
   *                       {@link ConfigurationConstants#LDAP_USER_LOOKUP_BY_SEARCH}
   * @param emailRequired
   */
  public JNDIAuthenticator(final byte userLookupMode, final boolean emailRequired) {
    this.userLookupMode = userLookupMode;
    this.emailRequired = emailRequired;
  }


  /**
   * @return digest algorithm
   */
  public String getDigestAlgorithm() {
    return digestAlgorithm;
  }


  /**
   * Set digest algorithm or null if plain or none.
   *
   * @see ConfigurationConstants#LDAP_CREDENTIAL_DIGEST_MD2_VALUE
   * @see ConfigurationConstants#LDAP_CREDENTIAL_DIGEST_MD5_VALUE
   * @see ConfigurationConstants#LDAP_CREDENTIAL_DIGEST_SHA1_VALUE
   */
  public void setDigestAlgorithm(final String digestAlgorithm) {
    this.digestAlgorithm = digestAlgorithm;
  }


  /**
   * Return the type of authentication to use.
   */
  public String getConnectionSecurityLevel() {
    return connectionSecurityLevel;
  }


  /**
   * Set the type of authentication to use.
   *
   * @param connectionSecurityLevel The authentication
   */
  public void setConnectionSecurityLevel(final String connectionSecurityLevel) {
    this.connectionSecurityLevel = connectionSecurityLevel;
  }


  /**
   * Return the connection username for this Realm.
   */
  public String getConnectionPrincipal() {
    return this.connectionPrincipal;
  }


  /**
   * Set the connection username for this Realm.
   *
   * @param connectionPrincipal The new connection username
   */
  public void setConnectionPrincipal(final String connectionPrincipal) {
    this.connectionPrincipal = connectionPrincipal;
  }


  /**
   * Return the connection password for this Realm.
   */
  public String getConnectionCredentials() {
    return this.connectionCredentials;
  }


  /**
   * Set the connection password for this Realm.
   *
   * @param connectionCredentials The new connection password
   */
  public void setConnectionCredentials(final String connectionCredentials) {
    this.connectionCredentials = connectionCredentials;
  }


  /**
   * Return the connection URL for this Realm.
   */
  public String getConnectionURL() {
    return this.connectionURL;
  }


  /**
   * Set the connection URL for this Realm.
   *
   * @param connectionURL The new connection URL
   */
  public void setConnectionURL(final String connectionURL) {
    this.connectionURL = connectionURL;
  }


  /**
   * Return the JNDI context factory for this Realm.
   */
  public String getContextFactory() {
    return this.contextFactory;
  }


  /**
   * Set the JNDI context factory for this Realm.
   *
   * @param contextFactory The new context factory
   */
  public void setContextFactory(final String contextFactory) {
    this.contextFactory = contextFactory;
  }


  /**
   * Return the derefAliases setting to be used.
   */
  public String getDerefAliases() {
    return derefAliases;
  }


  /**
   * Set the value for derefAliases to be used when searching the directory.
   *
   * @param derefAliases New value of property derefAliases.
   */
  public void setDerefAliases(final String derefAliases) {
    this.derefAliases = derefAliases;
  }


  /**
   * Return the protocol to be used.
   */
  public String getProtocol() {
    return protocol;
  }


  /**
   * Set the protocol for this Realm.
   *
   * @param protocol The new protocol.
   */
  public void setProtocol(final String protocol) {
    this.protocol = protocol;
  }


  /**
   * Returns the current settings for handling JNDI referrals.
   */
  public String getReferrals() {
    return referrals;
  }


  /**
   * How do we handle JNDI referrals? ignore, follow, or throw
   * (see javax.naming.Context.REFERRAL for more information).
   */
  public void setReferrals(final String referrals) {
    this.referrals = referrals;
  }


  /**
   * Return the base element for user searches.
   */
  public String getUserBase() {
    return this.userBase;
  }


  /**
   * Set the base element for user searches.
   *
   * @param userBase The new base element
   */
  public void setUserBase(final String userBase) {
    this.userBase = userBase;
  }


  /**
   * Return the message format pattern for selecting users in this Realm.
   */
  public String getUserSearchTemplate() {
    return this.userSearchTemplate;
  }


  /**
   * Set the message format pattern for selecting users in this Realm.
   *
   * @param userSearchTemplate The new user search pattern
   */
  public void setUserSearchTemplate(final String userSearchTemplate) {
    this.userSearchTemplate = userSearchTemplate;
  }


  /**
   * Return the "search subtree for users" flag.
   */
  public boolean getSearchEntireSubtree() {
    return this.searchEntireSubtree;
  }


  /**
   * Set the "search subtree for users" flag.
   *
   * @param searchEntireSubtree The new search flag
   */
  public void setSearchEntireSubtree(final boolean searchEntireSubtree) {
    this.searchEntireSubtree = searchEntireSubtree;
  }


  /**
   * Return the user role name attribute name for this Realm.
   */
  public String getUserRoleName() {
    return userRoleName;
  }


  /**
   * Set the user role name attribute name for this Realm.
   *
   * @param userRoleName The new userRole name attribute name
   */
  public void setUserRoleName(final String userRoleName) {
    this.userRoleName = userRoleName;
  }


  /**
   * Return the base element for role searches.
   */
  public String getRoleBase() {
    return this.roleBase;
  }


  /**
   * Set the base element for role searches.
   *
   * @param roleBase The new base element
   */
  public void setRoleBase(final String roleBase) {
    this.roleBase = roleBase;
  }


  /**
   * Return the role name attribute name for this Realm.
   */
  public String getRoleName() {
    return this.roleName;
  }


  /**
   * Set the role name attribute name for this Realm.
   *
   * @param roleName The new role name attribute name
   */
  public void setRoleName(final String roleName) {
    this.roleName = roleName;
  }


  /**
   * Return the message format pattern for selecting roles in this Realm.
   */
  public String getRoleSearch() {
    return this.roleSearch;
  }


  /**
   * Set the message format pattern for selecting roles in this Realm.
   *
   * @param roleSearch The new role search pattern
   */
  public void setRoleSearch(final String roleSearch) {
    this.roleSearch = roleSearch;
    if (roleSearch == null) {
      roleFormat = null;
    } else {
      roleFormat = new MessageFormat(roleSearch);
    }
  }


  /**
   * Return the "search subtree for roles" flag.
   */
  public boolean getRoleSubtree() {
    return this.roleSubtree;
  }


  /**
   * Set the "search subtree for roles" flag.
   *
   * @param roleSubtree The new search flag
   */
  public void setRoleSubtree(final boolean roleSubtree) {
    this.roleSubtree = roleSubtree;
  }


  /**
   * Return the password attribute used to retrieve the user password.
   */
  public String getUserPasswordAttributeName() {
    return this.userPasswordAttributeName;
  }


  /**
   * Set the password attribute used to retrieve the user password.
   *
   * @param userPasswordAttributeName The new password attribute
   */
  public void setUserPasswordAttributeName(final String userPasswordAttributeName) {
    this.userPasswordAttributeName = userPasswordAttributeName;
  }


  /**
   * Return the e-mail attribute used to retrieve the user e-mail.
   */
  public String getUserEmailAttributeName() {
    return userEmailAttributeName;
  }


  /**
   * Set the e-mail attribute used to retrieve the user e-mail.
   */
  public void setUserEmailAttributeName(final String userEmailAttributeName) {
    this.userEmailAttributeName = userEmailAttributeName;
  }


  /**
   * Return the message format pattern for selecting users in this Realm.
   */
  public String getUserDistinguishedNameTemplate() {
    return this.userDistinguishedNameTemplate;
  }


  /**
   * Set the message format pattern for selecting users in this Realm.
   * This may be one simple pattern, or multiple patterns to be tried,
   * separated by parentheses. (for example, either "cn={0}", or
   * "(cn={0})(cn={0},o=myorg)" Full LDAP search strings are also supported,
   * but only the "OR", "|" syntax, so "(|(cn={0})(cn={0},o=myorg))" is
   * also valid. Complex search strings with &, etc are NOT supported.
   *
   * @param userDistinguishedNameTemplate The new user pattern
   */
  public void setUserDistinguishedNameTemplate(final String userDistinguishedNameTemplate) {
    if (userLookupMode == ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE) {
      this.userDistinguishedNameTemplate = ArgumentValidator.validateArgumentNotBlank(userDistinguishedNameTemplate, "user DN template");
    } else {
      this.userDistinguishedNameTemplate = userDistinguishedNameTemplate;
    }
  }


  /**
   * Return the Principal associated with the specified username and
   * credentials, if there is one; otherwise return <code>null</code>.
   * <p/>
   * If there are any errors with the JDBC connection, executing
   * the query or anything we return null (don't authenticate). This
   * event is also logged, and the connection will be closed so that
   * a subsequent request will automatically re-open it.
   *
   * @param username    Username of the Principal to look up
   * @param credentials Password or other credentials to use in
   */
  public JNDIUser authenticate(final String username, final String credentials) throws NamingException {

    DirContext context = null;
    try {

      // Ensure that we have a directory context available
      context = openContext();

      // Return the authenticated Principal (if any)
      if (log.isDebugEnabled()) {
        log.debug("authenticate the username: " + username);
      }
      if (StringUtils.isBlank(username) || StringUtils.isBlank(credentials)) {
        return null;
      }

      // Retrieve user information
      final JNDIUser user = getUser(context, username);
      if (log.isDebugEnabled()) {
        log.debug("user is null");
      }
      if (user == null) {
        return null;
      }

      // Check the user's credentials
      if (!checkCredentials(context, user, credentials)) {
        if (log.isDebugEnabled()) {
          log.debug("credential check failed");
        }
        return null;
      }

      // Search for additional roles
      final List roles = getRoles(context, user);

      // Create and return a suitable Principal for this user
      return new JNDIUser(username, user.getDn(), credentials, user.getEmail(), roles);
    } finally {
      closeHard(context);
    }
  }


  /**
   * Return a User object containing information about the user
   * with the specified username, if found in the directory;
   * otherwise return <code>null</code>.
   * <p/>
   * If the <code>userPassword</code> configuration attribute is
   * specified, the value of that attribute is retrieved from the
   * user's directory entry. If the <code>userRoleName</code>
   * configuration attribute is specified, all values of that
   * attribute are retrieved from the directory entry.
   *
   * @param username Username to be looked up
   * @throws NamingException if a directory server error occurs
   */
  public JNDIUser getUser(final String username) throws NamingException {
    return getUser(openContext(), username);
  }


  /**
   * Return a User object containing information about the user
   * with the specified username, if found in the directory;
   * otherwise return <code>null</code>.
   * <p/>
   * If the <code>userPassword</code> configuration attribute is
   * specified, the value of that attribute is retrieved from the
   * user's directory entry. If the <code>userRoleName</code>
   * configuration attribute is specified, all values of that
   * attribute are retrieved from the directory entry.
   *
   * @param context  The directory context
   * @param username Username to be looked up
   * @throws NamingException if a directory server error occurs
   */
  private JNDIUser getUser(final DirContext context, final String username) throws NamingException {

    final JNDIUser user;

// Use pattern or search for user entry
    if (userLookupMode == ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE) {
      user = getUserByPattern(context, username, attributesToRetrieveFromUserEntry());
    } else if (userLookupMode == ConfigurationConstants.LDAP_USER_LOOKUP_BY_SEARCH) {
      user = getUserBySearch(context, username, attributesToRetrieveFromUserEntry());
    } else {
      throw new NamingException("Unknown user lookup mode: " + userLookupMode);
    }
    return user;
  }


  private String[] attributesToRetrieveFromUserEntry() {
    final Collection list = new ArrayList(1);
    if (userPasswordAttributeName != null) {
      list.add(userPasswordAttributeName);
    }
    if (userEmailAttributeName != null) {
      list.add(userEmailAttributeName);
    }
    if (userRoleName != null) {
      list.add(userRoleName);
    }
    final String[] attrIds = new String[list.size()];
    list.toArray(attrIds);
    return attrIds;
  }


  /**
   * Use the <code>UserPattern</code> configuration attribute to
   * locate the directory entry for the user with the specified
   * username and return a User object; otherwise return
   * <code>null</code>.
   *
   * @param context  The directory context
   * @param username The username
   * @param attrIds  String[]containing names of attributes to
   *                 retrieve.
   * @throws NamingException if a directory server error occurs
   */
  private JNDIUser getUserByPattern(final DirContext context, final String username,
                                    final String[] attrIds) throws NamingException {
    if (log.isDebugEnabled()) {
      log.debug("getting user by pattern");
    }

    if (username == null || userDistinguishedNameTemplate == null) {
      return null;
    }

    try {

      // parse template
      final String[] userDistinguishedNameTemplateArray = parseUserPatternString(userDistinguishedNameTemplate);
      for (int i = 0; i < userDistinguishedNameTemplateArray.length; i++) {
        final String template = userDistinguishedNameTemplateArray[i];
        final JNDIUserLookupStringGenerator jndiUserLookupStringGenerator = new JNDIUserLookupStringGenerator();
        final String userDN = jndiUserLookupStringGenerator.makeUserLookupString(template, username).toString();

        // Get required attributes from user entry
        Attributes attrs = null;
        try {

          attrs = context.getAttributes(userDN, attrIds);
        } catch (final NameNotFoundException e) {
          if (log.isDebugEnabled()) {
            log.debug("e: " + e, e);
          }
          continue; // to the next template
        }

        return makeUser(username, userDN, attrs);
      }
      return null;
    } catch (final ValidationException e) {
      throw toNamingException(e);
    }
  }


  private JNDIUser makeUser(final String username, final String userDN, final Attributes attrs) throws NamingException {
    if (attrs == null) {
      return null;
    }

    // Retrieve value of userPassword
    final String password = getAttributeValue(attrs, userPasswordAttributeName);
    if (!StringUtils.isBlank(userPasswordAttributeName) && password == null) {
      throw new NamingException("Password attribute \"" + userPasswordAttributeName + "\" not found.");
    }

    final String email = getAttributeValue(attrs, userEmailAttributeName);
    if (emailRequired) {
      if (email == null) {
        throw new NamingException("E-mail attribute \"" + userEmailAttributeName + "\" not found.");
      }
      if (StringUtils.isBlank(email)) {
        throw new NamingException("E-mail attribute \"" + userEmailAttributeName + "\" not set.");
      }
    }

    // Retrieve values of userRoleName attribute
    List roles = null;
    if (userRoleName != null) {
      roles = addAttributeValues(userRoleName, attrs, roles);
    }

    return new JNDIUser(username, userDN, password, email, roles);
  }


  /**
   * Search the directory to return a User object containing
   * information about the user with the specified username, if
   * found in the directory; otherwise return <code>null</code>.
   *
   * @param context  The directory context
   * @param username The username
   * @param attrIds  String[]containing names of attributes to retrieve.
   * @throws NamingException if a directory server error occurs
   */
  private JNDIUser getUserBySearch(final DirContext context,
                                   final String username, String[] attrIds) throws NamingException {

    if (log.isDebugEnabled()) {
      log.debug("geting user by search, parameters:");
    }
    if (log.isDebugEnabled()) {
      log.debug("   context: " + context);
    }
    if (log.isDebugEnabled()) {
      log.debug("   username: " + username);
    }
    if (log.isDebugEnabled()) {
      log.debug("   attrIds: " + Arrays.asList(attrIds));
    }
    try {
      if (StringUtils.isBlank(username) || StringUtils.isBlank(userSearchTemplate)) {
        return null;
      }

      // Form the search searchFilter
      final JNDIUserLookupStringGenerator generator = new JNDIUserLookupStringGenerator();
      final String searchFilter = generator.makeUserLookupString(userSearchTemplate, username).toString();

      // Set up the search controls
      final SearchControls constraints = new SearchControls();

      if (log.isDebugEnabled()) {
        log.debug("searchEntireSubtree: " + searchEntireSubtree);
      }
      if (searchEntireSubtree) {
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
      } else {
        constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
      }

      // Specify the attributes to be retrieved
      if (attrIds == null) {
        attrIds = EMPTY_STRING_ARRAY;
      }
      constraints.setReturningAttributes(attrIds);

      if (log.isDebugEnabled()) {
        log.debug("userBase: " + userBase);
      }
      if (log.isDebugEnabled()) {
        log.debug("searchFilter: " + searchFilter);
      }
      if (log.isDebugEnabled()) {
        log.debug("constraints: " + constraints);
      }
      final NamingEnumeration results = context.search(userBase, searchFilter, constraints);

      // Fail if no entries found
      if (results == null || !results.hasMore()) {
        if (log.isDebugEnabled()) {
          log.debug("results: " + results);
        }
        return null;
      }

      // Get result for the first entry found
      final SearchResult result = (SearchResult) results.next();

      // Check no further entries were found
      if (results.hasMore()) {
        throw new NamingException("User with name " + username + " has multiple entries");
      }

      // Get the entry's distinguished name
      final NameParser parser = context.getNameParser("");
      final Name contextName = parser.parse(context.getNameInNamespace());
      final Name baseName = parser.parse(userBase);

      final Name entryName = parser.parse(new CompositeName(result.getName()).get(0));

      Name name = contextName.addAll(baseName);
      name = name.addAll(entryName);
      final String dn = name.toString();

      if (log.isDebugEnabled()) {
        log.debug("Entry found for " + username + " with dn " + dn);
      }

      // Get the entry's attributes
      final Attributes attrs = result.getAttributes();
      return makeUser(username, dn, attrs);
    } catch (final ValidationException e) {
      throw toNamingException(e);
    }
  }


  /**
   * Check whether the given User can be authenticated with the
   * given credentials. If the <code>userPassword</code>
   * configuration attribute is specified, the credentials
   * previously retrieved from the directory are compared explicitly
   * with those presented by the user. Otherwise the presented
   * credentials are checked by binding to the directory as the
   * user.
   *
   * @param context     The directory context
   * @param user        The User to be authenticated
   * @param credentials The credentials presented by the user
   * @throws NamingException if a directory server error occurs
   */
  private boolean checkCredentials(final DirContext context, final JNDIUser user,
                                   final String credentials) throws NamingException {

    boolean validated = false;
    if (StringUtils.isBlank(userPasswordAttributeName)) {
      validated = bindAsUser(context, user, credentials);
    } else {
      validated = compareCredentials(user, credentials);
    }

    if (log.isDebugEnabled()) {
      if (validated) {
        log.debug("authentication successful for: " + user.getUsername());
      } else {
        log.debug("authentication failed for: " + user.getUsername());
      }
    }
    return validated;
  }


  /**
   * Check whether the credentials presented by the user match those
   * retrieved from the directory.
   *
   * @param info        The User to be authenticated
   * @param credentials Authentication credentials
   */
  private boolean compareCredentials(final JNDIUser info, final String credentials) throws ConfigurationException {
    try {
      if (info == null || credentials == null) {
        return false;
      }
      final String password = info.getPassword();
      if (log.isDebugEnabled()) {
        log.debug("user password: " + password);
      }
      if (log.isDebugEnabled()) {
        log.debug("digestAlgorithm: " + digestAlgorithm);
      }
      if (password == null) {
        return false;
      }
      if (log.isDebugEnabled()) {
        log.debug("Validate the credentials specified by the user");
      }
      boolean validated = false;
      if (StringUtils.isBlank(digestAlgorithm)) {
        validated = digest(credentials).equals(password);
      } else {
        if (password.toUpperCase().startsWith("{SHA}")) {
          validated = digestCredentialsAndValidate(credentials, password.substring(5));
        } else if (password.toUpperCase().startsWith("{MD5}")) {
          validated = digestCredentialsAndValidate(credentials, password.substring(5));
        } else {
          // Hex hashes should be compared case-insensitive
          validated = digest(credentials).equalsIgnoreCase(password);
        }
      }
      return validated;
    } catch (final NoSuchAlgorithmException e) {
      final ConfigurationException ce = new ConfigurationException(StringUtils.toString(e));
      ce.initCause(e);
      throw ce;
    }
  }


  private boolean digestCredentialsAndValidate(final String credentials, final String password) throws NoSuchAlgorithmException {
    final MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
    md.reset();
    md.update(credentials.getBytes());
    return password.equals(new String(StringUtils.encode(md.digest())));
  }


  /**
   * Digest the password using the specified algorithm and
   * convert the result to a corresponding hexadecimal string.
   * If exception, the plain credentials string is returned.
   * <p/>
   * <strong>IMPLEMENTATION NOTE</strong> - This implementation is
   * synchronized because it reuses the MessageDigest instance.
   * This should be faster than cloning the instance on every request.
   *
   * @param credentials Password or other credentials to use in
   *                    authenticating this username
   */
  private String digest(final String credentials) throws NoSuchAlgorithmException {
    if (!!StringUtils.isBlank(digestAlgorithm)) {
      return credentials;
    }
    final MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
    md.reset();
    md.update(credentials.getBytes());
    return StringUtils.encodeToHex(md.digest());
  }


  /**
   * Check credentials by binding to the directory as the user
   *
   * @param context     The directory context
   * @param user        The User to be authenticated
   * @param credentials Authentication credentials
   * @throws NamingException if a directory server error occurs
   */
  private boolean bindAsUser(final DirContext context, final JNDIUser user, final String credentials)
          throws NamingException {

    if (credentials == null || user == null) {
      return false;
    }

    // Validate the credentials specified by the user
    final String dn = user.getDn();
    if (log.isDebugEnabled()) {
      log.debug("validating credentials by binding as: " + dn);
    }
    if (dn == null) {
      return false;
    }

    // Set up security environment to bind as the user
    context.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
    context.addToEnvironment(Context.SECURITY_CREDENTIALS, credentials);

    // Elicit an LDAP bind operation
    boolean validated = false;
    try {
      //noinspection UNUSED_SYMBOL,UnusedDeclaration
      context.getAttributes("", null);
      validated = true;
    } catch (final AuthenticationException e) {
      if (log.isDebugEnabled()) {
        log.debug("bind attempt failed: " + e, e);
      }
    }

    // Restore the original security environment
    if (connectionPrincipal != null) {
      context.addToEnvironment(Context.SECURITY_PRINCIPAL, connectionPrincipal);
    } else {
      context.removeFromEnvironment(Context.SECURITY_PRINCIPAL);
    }

    if (connectionCredentials != null) {
      context.addToEnvironment(Context.SECURITY_CREDENTIALS, connectionCredentials);
    } else {
      context.removeFromEnvironment(Context.SECURITY_CREDENTIALS);
    }

    return validated;
  }


  /**
   * Return a List of roles associated with the given User.  Any
   * roles present in the user's directory entry are supplemented by
   * a directory search. If no roles are associated with this user,
   * a zero-length List is returned.
   *
   * @param context The directory context we are searching
   * @param user    The User to be checked
   * @throws NamingException if a directory server error occurs
   */
  private List getRoles(final DirContext context, final JNDIUser user)
          throws NamingException {

    if (user == null) {
      return null;
    }

    final String dn = user.getDn();
    final String username = user.getUsername();

    if (dn == null || username == null) {
      return null;
    }

    if (log.isDebugEnabled()) {
      log.debug("getRoles(" + dn + ')');
    }

    // Start with roles retrieved from the user entry
    List list = user.getRoles();
    if (list == null) {
      list = new ArrayList(1);
    }

    // Are we configured to do role searches?
    if (roleFormat == null || roleName == null) {
      return list;
    }

    // Set up parameters for an appropriate search
    final String filter = roleFormat.format(new String[]{doRFC2254Encoding(dn), username});
    final SearchControls controls = new SearchControls();
    if (roleSubtree) {
      controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    } else {
      controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
    }
    controls.setReturningAttributes(new String[]{roleName});

    // Perform the configured search and process the results
    final NamingEnumeration results = context.search(roleBase, filter, controls);
    if (results == null) {
      return list;  // Should never happen, but just in case ...
    }
    while (results.hasMore()) {
      final SearchResult result = (SearchResult) results.next();
      final Attributes attrs = result.getAttributes();
      if (attrs == null) {
        continue;
      }
      list = addAttributeValues(roleName, attrs, list);
    }

    if (log.isDebugEnabled()) {
      if (list != null) {
        log.debug("Returning " + list.size() + " roles");
        for (int i = 0; i < list.size(); i++) {
          log.debug("Found role " + list.get(i));
        }
      } else {
        log.debug("getRoles about to return null ");
      }
    }

    return list;
  }


  /**
   * Return a String representing the value of the specified attribute.
   *
   * @param attrs  Attributes containing the required value
   * @param attrId Attribute name
   * @throws NamingException if a directory server error occurs
   */
  private String getAttributeValue(final Attributes attrs, final String attrId)
          throws NamingException {

    if (StringUtils.isBlank(attrId)) {
      return null;
    }

    if (attrId == null || attrs == null) {
      return null;
    }

    final Attribute attr = attrs.get(attrId);
    if (attr == null) {
      return null;
    }
    final Object value = attr.get();
    if (value == null) {
      return null;
    }
    String valueString = null;
    if (value instanceof byte[]) {
      valueString = new String((byte[]) value);
    } else {
      valueString = value.toString();
    }

    return valueString;
  }


  /**
   * Add values of a specified attribute to a list
   *
   * @param attrId Attribute name
   * @param attrs  Attributes containing the new values
   * @param values ArrayList containing values found so far
   * @throws NamingException if a directory server error occurs
   */
  private List addAttributeValues(final String attrId, final Attributes attrs, List values)
          throws NamingException {

    if (attrId == null || attrs == null) {
      return values;
    }
    if (values == null) {
      values = new ArrayList(1);
    }
    final Attribute attr = attrs.get(attrId);
    if (attr == null) {
      return values;
    }
    final NamingEnumeration e = attr.getAll();
    while (e.hasMore()) {
      final String value = (String) e.next();
      values.add(value);
    }
    return values;
  }


  /**
   * Close any open connection to the directory server for this Realm.
   *
   * @param context The directory context to be closed
   */
  private void closeHard(final DirContext context) {

    // Do nothing if there is no opened connection
    if (context == null) {
      return;
    }

    // Close our opened connection
    try {
      context.close();
    } catch (final NamingException e) {
      log.error("Error while closing context", e);
    }
  }


  /**
   * Open (if necessary) and return a connection to the configured
   * directory server for this Realm.
   *
   * @throws NamingException if a directory server error occurs
   */
  private DirContext openContext() throws NamingException {
    if (log.isDebugEnabled()) {
      log.debug("opening context...");
    }
    final Hashtable environment = makeDirectoryContextEnvironment();
    if (log.isDebugEnabled()) {
      log.debug("environment: " + environment);
    }

    final InitialDirContext initialDirContext = new InitialDirContext(environment);
    if (log.isDebugEnabled()) {
      log.debug("initialDirContext: " + initialDirContext);
    }
    return initialDirContext;
  }


  /**
   * Create our directory context configuration.
   *
   * @return java.util.Hashtable the configuration for the directory context.
   */
  private Hashtable makeDirectoryContextEnvironment() {
    final Hashtable result = new Hashtable(11);
    putIfNotBlank(result, Context.INITIAL_CONTEXT_FACTORY, contextFactory);
    putIfNotBlank(result, Context.SECURITY_PRINCIPAL, connectionPrincipal);
    putIfNotBlank(result, Context.SECURITY_CREDENTIALS, connectionCredentials);
    putIfNotBlank(result, Context.PROVIDER_URL, connectionURL);
    putIfNotBlank(result, Context.SECURITY_AUTHENTICATION, connectionSecurityLevel);
    putIfNotBlank(result, Context.SECURITY_PROTOCOL, protocol);
    putIfNotBlank(result, Context.REFERRAL, referrals);
    putIfNotBlank(result, JAVA_NAMING_DEREF_ALIASES, derefAliases);
    putIfNotBlank(result, JAVA_NAMING_LDAP_VERSION, ldapVersion);
    return result;
  }


  /**
   * Given a string containing LDAP patterns for user locations (separated by
   * parentheses in a pseudo-LDAP search string format -
   * "(location1)(location2)", returns an array of those paths.  Real LDAP
   * search strings are supported as well (though only the "|" "OR" type).
   *
   * @param userPatternString - a string LDAP search paths surrounded by
   *                          parentheses
   */
  private String[] parseUserPatternString(final String userPatternString) {

    if (userPatternString != null) {
      final Collection pathList = new ArrayList(5);
      int startParenLoc = userPatternString.indexOf('(');
      if (startParenLoc == -1) {
        // no parens here; return whole thing
        return new String[]{userPatternString};
      }
      int startingPoint = 0;
      while (startParenLoc > -1) {
        // weed out escaped open parens and parens enclosing the
        // whole statement (in the case of valid LDAP search
        // strings: (|(something)(somethingelse))
        while (userPatternString.charAt(startParenLoc + 1) == '|' ||
                startParenLoc != 0 && userPatternString.charAt(startParenLoc - 1) == '\\') {
          startParenLoc = userPatternString.indexOf('(', startParenLoc + 1);
        }
        int endParenLoc = userPatternString.indexOf(')', startParenLoc + 1);
        // weed out escaped end-parens
        while (userPatternString.charAt(endParenLoc - 1) == '\\') {
          endParenLoc = userPatternString.indexOf(')', endParenLoc + 1);
        }
        final String nextPathPart = userPatternString.substring
                (startParenLoc + 1, endParenLoc);
        pathList.add(nextPathPart);
        startingPoint = endParenLoc + 1;
        startParenLoc = userPatternString.indexOf('(', startingPoint);
      }
      return (String[]) pathList.toArray(new String[pathList.size()]);
    }
    return null;
  }


  /**
   * Given an LDAP search string, returns the string with certain characters
   * escaped according to RFC 2254 guidelines.
   * The character mapping is as follows:
   * char ->  Replacement
   * ---------------------------
   * *  -> \2a
   * (  -> \28
   * )  -> \29
   * \  -> \5c
   * \0 -> \00
   *
   * @param inString string to escape according to RFC 2254 guidelines
   * @return String the escaped/encoded result
   */
  private String doRFC2254Encoding(final String inString) {
    final StringBuilder buf = new StringBuilder(inString.length());
    for (int i = 0; i < inString.length(); i++) {
      final char c = inString.charAt(i);
      switch (c) {
        case '\\':
          buf.append("\\5c");
          break;
        case '*':
          buf.append("\\2a");
          break;
        case '(':
          buf.append("\\28");
          break;
        case ')':
          buf.append("\\29");
          break;
        case '\0':
          buf.append("\\00");
          break;
        default:
          buf.append(c);
          break;
      }
    }
    return buf.toString();
  }


  private void putIfNotBlank(final Map map, final String key, final String value) {
    if (!StringUtils.isBlank(value)) {
      map.put(key, value);
    }
  }


  /**
   * Helper method.
   */
  private NamingException toNamingException(final ValidationException e) {
    final NamingException ne = new NamingException(StringUtils.toString(e));
    ne.initCause(e);
    return ne;
  }


  public String toString() {
    return "JNDIAuthenticator{}";
  }


  public void setLDAPVersion(final String ldapVersion) {
    this.ldapVersion = ldapVersion;
  }
}
/*

Example of weblogic LDAP settings:


# Properties for Microsoft Site Server
# -------------------------------------------------------------------
#weblogic.security.ldaprealm.url=ldap://ldapserver:389
#weblogic.security.ldaprealm.principal=cn=ldapadmin
#weblogic.security.ldaprealm.credential=*REPLACE-ME*
#weblogic.security.ldaprealm.ssl=false
#weblogic.security.ldaprealm.authentication=simple
#weblogic.security.ldaprealm.userAuthentication=local
#
# User Schema
#weblogic.security.ldaprealm.userDN=o=Microsoft, ou=Members
#weblogic.security.ldaprealm.userNameAttribute=cn
#weblogic.security.ldaprealm.userPasswordAttribute=userpassword
#weblogic.security.ldaprealm.userCommonNameAttribute=cn
#
# Group Schema
#weblogic.security.ldaprealm.groupDN=o=Microsoft, ou=Groups
#weblogic.security.ldaprealm.groupNameAttribute=cn
#weblogic.security.ldaprealm.groupIsContext=true
#weblogic.security.ldaprealm.groupUsernameAttribute=memberObject
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #



# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# Properties for Netscape Directory Server
# -------------------------------------------------------------------
# Directory Server Properties
#weblogic.security.ldaprealm.url=ldap://ldapserver:389
#weblogic.security.ldaprealm.principal=uid=admin, ou=Administrators, ou=TopologyManagement, o=NetscapeRoot
#weblogic.security.ldaprealm.credential=*REPLACE-ME*
#weblogic.security.ldaprealm.ssl=false
#weblogic.security.ldaprealm.authentication=simple
#weblogic.security.ldaprealm.userAuthentication=local
#
# User Schema
#weblogic.security.ldaprealm.userDN=o=airius.com,ou=People
#weblogic.security.ldaprealm.userNameAttribute=uid
#weblogic.security.ldaprealm.userPasswordAttribute=userpassword

# Group Schema
#weblogic.security.ldaprealm.groupDN=o=airius.com,ou=Groups
#weblogic.security.ldaprealm.groupNameAttribute=cn
#weblogic.security.ldaprealm.groupUsernameAttribute=uniquemember
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #



# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# Properties For Novell Directory Services
# -------------------------------------------------------------------
# Directory Server Properties
#weblogic.security.ldaprealm.url=ldap://ldapserver:636
#weblogic.security.ldaprealm.principal=cn=Admin,o=airius.com
#weblogic.security.ldaprealm.credential=*REPLACE-ME*
#weblogic.security.ldaprealm.ssl=true
#weblogic.security.ldaprealm.authentication=simple
#weblogic.security.ldaprealm.userAuthentication=bind
#
# User Schema
#weblogic.security.ldaprealm.userDN=o=airius.com,ou=People
#weblogic.security.ldaprealm.userNameAttribute=cn

# Group Schema
#weblogic.security.ldaprealm.groupDN=o=airius.com,ou=Groups
#weblogic.security.ldaprealm.groupNameAttribute=cn
#weblogic.security.ldaprealm.groupUsernameAttribute=member
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

*/