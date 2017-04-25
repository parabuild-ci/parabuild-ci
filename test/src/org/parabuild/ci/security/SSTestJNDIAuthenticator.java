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

import javax.naming.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;

/**
 *
 */
public class SSTestJNDIAuthenticator extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestJNDIAuthenticator.class);
  private static final String TEST_DN_TEMPLATE = "uid=${user.id},ou=Test,dc=viewtier,dc=com";
  private static final String TEST_USER_WITH_PLAIN_PASSWORD = "test_user_with_plain_password";
  private static final String TEST_USER_EMAIL_WITH_PLAIN_PASSWORD = "test_user_with_plain_password@parabuildci.org";
  private static final String TEST_PASSWORD = "test_password";
  private static final String TEST_USER_WITH_MD5_PASSWORD = "test_user_with_md5_password";
  private static final String TEST_USER_WITH_SHA_PASSWORD = "test_user_with_sha_password";
  private static final String USER_PASSWORD_ATTR_NAME = "userPassword";
  private static final String TEST_CONNECTION_PRINCIPAL = "uid=test_authenticator,ou=Test,dc=viewtier,dc=com";
  private static final String TEST_CONNECTION_PASSWORD = "test_auth_password";
  private static final String TEST_USER_SEARCH_TEMPLATE = "(mail=${user.id})";


  /**
   * Search auth
   */
  public void test_authenticateUserWithPlainPassswordUsingSearchBase() throws NamingException {
    final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_SEARCH);
    auth.setDigestAlgorithm(null);
    auth.setUserSearchTemplate(TEST_USER_SEARCH_TEMPLATE);
    auth.setUserBase("ou=Test,dc=viewtier,dc=com");
    auth.setSearchEntireSubtree(false);
    asserUserIsOK(auth.authenticate(TEST_USER_EMAIL_WITH_PLAIN_PASSWORD, TEST_PASSWORD));
  }


  /**
   * Search auth
   */
  public void test_authenticateUserWithPlainPassswordUsingSearchSubtree() throws NamingException {
    final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_SEARCH);
    auth.setDigestAlgorithm(null);
    auth.setUserSearchTemplate(TEST_USER_SEARCH_TEMPLATE);
    auth.setUserBase("dc=viewtier,dc=com");
    auth.setSearchEntireSubtree(true);
    asserUserIsOK(auth.authenticate(TEST_USER_EMAIL_WITH_PLAIN_PASSWORD, TEST_PASSWORD));
  }


  /**
   * NOTE: Bind is used because we don't provide password attribute name.
   */
  public void test_authenticateUserWithPlainPassswordUsingBind() throws NamingException {
    final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE);
    auth.setDigestAlgorithm(null);
    auth.setUserDistinguishedNameTemplate(TEST_DN_TEMPLATE);
    final JNDIUser user = auth.authenticate(TEST_USER_WITH_PLAIN_PASSWORD, TEST_PASSWORD);
    asserUserIsOK(user);
  }


  /**
   * NOTE: Bind is used because we don't provide password attribute name.
   */
  public void test_authenticateUserWithMD5PassswordUsingBind() throws NamingException {
    final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE);
    auth.setDigestAlgorithm(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD5_VALUE);
    auth.setUserDistinguishedNameTemplate(TEST_DN_TEMPLATE);
    final JNDIUser user = auth.authenticate(TEST_USER_WITH_MD5_PASSWORD, TEST_PASSWORD);
    asserUserIsOK(user);
  }


  /**
   * NOTE: Bind is used because we don't provide password attribute name.
   */
  public void test_authenticateUserWithSHAPassswordUsingBind() throws NamingException {
    final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE);
    auth.setDigestAlgorithm(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_SHA1_VALUE);
    auth.setUserDistinguishedNameTemplate(TEST_DN_TEMPLATE);
    final JNDIUser user = auth.authenticate(TEST_USER_WITH_SHA_PASSWORD, TEST_PASSWORD);
    asserUserIsOK(user);
  }


  /**
   * NOTE: Credential comparison is used because we provide password attribute name.
   */
  public void test_authenticateUserWithPlainPassswordUsingCredentialComparison() throws NamingException {
    final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE);
    auth.setDigestAlgorithm(null);
    auth.setUserDistinguishedNameTemplate(TEST_DN_TEMPLATE);
    auth.setUserPasswordAttributeName(USER_PASSWORD_ATTR_NAME);
    auth.setConnectionPrincipal(TEST_CONNECTION_PRINCIPAL);
    auth.setConnectionCredentials(TEST_CONNECTION_PASSWORD);
    final JNDIUser user = auth.authenticate(TEST_USER_WITH_PLAIN_PASSWORD, TEST_PASSWORD);
    asserUserIsOK(user);
  }


  /**
   * NOTE: Bind is used because we don't provide password attribute name.
   */
  public void test_authenticateUserWithMD5PassswordUsingCredentialComparison() throws NamingException {
    final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE);
    auth.setDigestAlgorithm(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD5_VALUE);
    auth.setUserDistinguishedNameTemplate(TEST_DN_TEMPLATE);
    auth.setUserPasswordAttributeName(USER_PASSWORD_ATTR_NAME);
    auth.setConnectionPrincipal(TEST_CONNECTION_PRINCIPAL);
    auth.setConnectionCredentials(TEST_CONNECTION_PASSWORD);
    final JNDIUser user = auth.authenticate(TEST_USER_WITH_MD5_PASSWORD, TEST_PASSWORD);
    asserUserIsOK(user);
  }


  /**
   * NOTE: Bind is used because we don't provide password attribute name.
   */
  public void test_authenticateUserWithSHAPassswordUsingCredentialComparison() throws NamingException {
    final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE);
    auth.setDigestAlgorithm(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_SHA1_VALUE);
    auth.setUserDistinguishedNameTemplate(TEST_DN_TEMPLATE);
    auth.setUserPasswordAttributeName(USER_PASSWORD_ATTR_NAME);
    auth.setConnectionPrincipal(TEST_CONNECTION_PRINCIPAL);
    auth.setConnectionCredentials(TEST_CONNECTION_PASSWORD);
    final JNDIUser user = auth.authenticate(TEST_USER_WITH_SHA_PASSWORD, TEST_PASSWORD);
    asserUserIsOK(user);
  }


  public void test_authenticateFailsOnNonExistingHost() throws NamingException {
    try {
      final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE);
      auth.setConnectionURL("ldap://test_host_never_existed" + System.currentTimeMillis());
      auth.authenticate("test_user_name", TEST_PASSWORD);
      TestHelper.failNoExceptionThrown();
    } catch (NamingException e) {
    }
  }


  public void test_authenticateFailsOnNonExistingEmailAttribute() throws NamingException {
    try {
      final JNDIAuthenticator auth = makeJNDIAuthenticator(ConfigurationConstants.LDAP_USER_LOOKUP_BY_SEARCH);
      auth.setDigestAlgorithm(null);
      auth.setUserSearchTemplate(TEST_USER_SEARCH_TEMPLATE);
      auth.setUserBase("ou=Test,dc=viewtier,dc=com");
      auth.setSearchEntireSubtree(false);
      auth.setUserEmailAttributeName("mail_attribute_never_existed");
      auth.authenticate(TEST_USER_EMAIL_WITH_PLAIN_PASSWORD, TEST_PASSWORD);
      TestHelper.failNoExceptionThrown();
    } catch (NamingException e) {
    }
  }


  /**
   * Helper method to check general validity of the authenticated
   * user.
   */
  private void asserUserIsOK(final JNDIUser user) {
    assertNotNull("user should not be null", user);
    assertTrue("user e-mail should not be null", !StringUtils.isBlank(user.getEmail()));
  }


  /**
   * Creates authenticator with common parameters set.
   */
  private JNDIAuthenticator makeJNDIAuthenticator(final byte lookupMode) {
    final JNDIAuthenticator auth = new JNDIAuthenticator(lookupMode, true);
    auth.setConnectionURL("ldap://localhost:389");
    auth.setConnectionSecurityLevel(ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_SIMPLE_VALUE);

    // this causes breakage of search
    auth.setUserEmailAttributeName("mail");
    return auth;
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestJNDIAuthenticator.class, new String[]{
      "test_authenticateUserWithPlainPassswordUsingSearchSubtree",
      "test_authenticateUserWithPlainPassswordUsingSearchBase",
      "test_authenticateUserWithSHAPassswordUsingCredentialComparison",
      "test_authenticateUserWithPlainPassswordUsingCredentialComparison",
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SSTestJNDIAuthenticator(final String s) {
    super(s);
  }
}
