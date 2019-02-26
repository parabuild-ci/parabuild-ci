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

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.FatalConfigurationException;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.GroupMemberVO;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.Group;
import org.parabuild.ci.object.GroupBuildAccess;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.User;

import java.util.List;

/**
 *
 */
public class SSTestSecurityManager extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestSecurityManager.class);

  public static final String TEST_NOT_ADMIN_USER = "not_admin";
  public static final String TEST_NOT_ADMIN_PASSWD = "not_admin";
  public static final int TEST_BUILD_ID_1 = 1;
  public static final int TEST_BUILD_ID_6 = 6;
  private static final int TEST_USER_ID_3 = 3;
  private static final int TEST_USER_ID_2 = 2;
  private static final int TEST_USER_ID_1 = 1;
  private static final int TEST_USER_ID_0 = 0;
  private static final int TEST_RESULT_GROUP_ID_0 = 0;
  private static final int TEST_GROUP_ID_1 = 1;
  private static final int TEST_GROUP_ID_0 = 0;
  private static final String TEST_LDAP_CONNECTION_URL = "ldap://localhost:389";

  private static final String TEST_LDAP_USER_DN_TEMPLATE = "uid=${user.id},ou=Test,dc=viewtier,dc=com";
  private static final String TEST_LDAP_PASSWORD = "test_password";
  private static final String TEST_LDAP_USER_WITH_PLAIN_PASSWORD = "test_user_with_plain_password";

  private ConfigurationManager cm = null;
  private org.parabuild.ci.security.SecurityManager securityManager = null;


  private SystemConfigurationManager systemCM = null;


  public SSTestSecurityManager(final String s) {
    super(s);
  }


  public void test_isAnonymousAccessEnabled() {
    // enabled
    enableAnonymousBuilds();
    assertTrue(securityManager.isAnonymousAccessEnabled());

    // disabled
    disableAnonymousBuilds();
    assertTrue(!securityManager.isAnonymousAccessEnabled());

    // property not present
    systemCM.deleteSystemProperty(systemCM.getSystemProperty(SystemProperty.ENABLE_ANONYMOUS_BUILDS));
    assertTrue(!securityManager.isAnonymousAccessEnabled());
  }


  public void test_userCanViewBuild() {
    // ==============================================================
    // anon disabled
    // ==============================================================
    disableAnonymousBuilds();

    // non-set user
    assertTrue(!securityManager.userCanViewBuild(User.UNSAVED_ID, TEST_BUILD_ID_1));

    // non-existing user
    assertTrue(!securityManager.userCanViewBuild(999999, TEST_BUILD_ID_1));

    // existing user in a group with access to build
    assertTrue(securityManager.userCanViewBuild(TEST_USER_ID_2, TEST_BUILD_ID_1));

    // existing user not in a group with access to build
    assertTrue(!securityManager.userCanViewBuild(TEST_USER_ID_1, TEST_BUILD_ID_1));

    // ==============================================================
    // anon enabled
    // ==============================================================
    enableAnonymousBuilds();
    assertTrue(securityManager.userCanViewBuild(TEST_USER_ID_2, TEST_BUILD_ID_6));
  }


  public void test_getUserBuildRights() {
    // ==============================================================
    // anon disabled
    // ==============================================================
    disableAnonymousBuilds();

    // non-set user2
    assertEquals(BuildRights.NO_RIGHTS, securityManager.getUserBuildRights(null, TEST_BUILD_ID_1));

    // non-existing user2
    assertEquals(BuildRights.NO_RIGHTS, securityManager.getUserBuildRights(null, TEST_BUILD_ID_1));

    // existing user2 in a group with access to build
    final User user2 = SecurityManager.getInstance().getUser(TEST_USER_ID_2);
    BuildRights user2Rights = securityManager.getUserBuildRights(user2, TEST_BUILD_ID_1);
    if (log.isDebugEnabled()) log.debug("user2Rights: " + user2Rights);
    assertEquals("isAllowedToStartBuild", true, user2Rights.isAllowedToStartBuild());
    assertEquals("isAllowedToStopBuild", true, user2Rights.isAllowedToStopBuild());
    assertEquals("isAllowedToListCommands", true, user2Rights.isAllowedToListCommands());
    assertEquals("isAllowedToResumeBuild", true, user2Rights.isAllowedToResumeBuild());
    assertEquals("isAllowedToViewBuild", true, user2Rights.isAllowedToViewBuild());
    assertEquals("isAllowedToDeleteResults", false, user2Rights.isAllowedToDeleteResults());
    assertEquals("isAllowedToPublishResults", false, user2Rights.isAllowedToPublishResults());

    assertEquals(true, user2Rights.isAllowedToActivateBuild());
    assertEquals(false, user2Rights.isAllowedToCreateBuild());
    assertEquals(true, user2Rights.isAllowedToDeactivateBuild());
    assertEquals(false, user2Rights.isAllowedToDeleteBuild());
    assertEquals(false, user2Rights.isAllowedToUpdateBuild());

    // existing user2 not in a group with access to build
    final User user1 = SecurityManager.getInstance().getUser(TEST_USER_ID_1);
    assertEquals("isAllowedToViewBuild", false, securityManager.getUserBuildRights(user1, TEST_BUILD_ID_1).isAllowedToViewBuild());

    // build ID is not set
    assertEquals("isAllowedToViewBuild", false, securityManager.getUserBuildRights(user1, BuildConfig.UNSAVED_ID).isAllowedToViewBuild());

    // ==============================================================
    // anon enabled
    // ==============================================================
    enableAnonymousBuilds();
    user2Rights = securityManager.getUserBuildRights(user2, TEST_BUILD_ID_1);
    // stiil there?
    assertEquals("isAllowedToStartBuild", true, user2Rights.isAllowedToStartBuild());
    assertEquals("isAllowedToStopBuild", true, user2Rights.isAllowedToStopBuild());
    assertEquals("isAllowedToListCommands", true, user2Rights.isAllowedToListCommands());
    assertEquals("isAllowedToResumeBuild", true, user2Rights.isAllowedToResumeBuild());
    assertEquals("isAllowedToViewBuild", true, user2Rights.isAllowedToViewBuild());
    assertEquals("isAllowedToDeleteResults", false, user2Rights.isAllowedToDeleteResults());
    assertEquals("isAllowedToPublishResults", false, user2Rights.isAllowedToPublishResults());

    // anon
    if (log.isDebugEnabled()) log.debug("=================================================");
    assertEquals("isAllowedToViewBuild", true, securityManager.getUserBuildRights(user1, TEST_BUILD_ID_1).isAllowedToViewBuild());

    // build ID is not set
    assertEquals("isAllowedToViewBuild", false, securityManager.getUserBuildRights(user1, BuildConfig.UNSAVED_ID).isAllowedToViewBuild());
  }


  public void test_addBuildToAnonymousGroup() {

    // delete build from anon if it's there
    final Group anon = getAnonymousGroup();
    deleteBuildFromGroup(anon, TEST_BUILD_ID_1);

    // add build to anon and assert it's there
    securityManager.addBuildToAnonymousGroup(TEST_BUILD_ID_1);
    assertNotNull(SecurityManager.getInstance().getGroupBuildAccess(anon.getID(), TEST_BUILD_ID_1));
  }


  public void test_encrypt() {
    encryptEndPrint("test_password");
    encryptEndPrint("password");
    encryptEndPrint("test_bugs");
  }


  private String encryptEndPrint(final String original) {
    final String result = SecurityManager.encryptPassword(original);
    if (log.isDebugEnabled()) log.debug("original/encrypted = " + original + '/' + result);
    return result;
  }


  private Group getAnonymousGroup() {
    return SecurityManager.getInstance().getGroupByName(Group.SYSTEM_ANONYMOUS_GROUP);
  }


  public void test_getUserBuildStatuses() {
    assertEquals(5, securityManager.getUserBuildStatuses(TEST_USER_ID_2).size());
    assertEquals(5, securityManager.getUserBuildStatuses(TEST_USER_ID_3).size());

    // delete a build from anon
    deleteBuildFromGroup(getAnonymousGroup(), TEST_BUILD_ID_6);
    assertEquals(4, securityManager.getUserBuildStatuses(TEST_USER_ID_2).size());
    assertEquals(4, securityManager.getUserBuildStatuses(TEST_USER_ID_3).size());

    // setting build's access to public increases number of visi
    final BuildConfig buildConfiguration = cm.getBuildConfiguration(3);
    buildConfiguration.setAccess(BuildConfig.ACCESS_PUBLIC);
    cm.save(buildConfiguration);
    securityManager.invalidateRightSetCaches();
    assertEquals(5, securityManager.getUserBuildStatuses(TEST_USER_ID_2).size());
    assertEquals(5, securityManager.getUserBuildStatuses(TEST_USER_ID_3).size());
  }


  private void deleteBuildFromGroup(final Group anon, final int testBuildId1) {
    final GroupBuildAccess buildGroup = SecurityManager.getInstance().getGroupBuildAccess(anon.getID(), testBuildId1);
    securityManager.deleteGroupBuildAccess(buildGroup);
    assertNull(SecurityManager.getInstance().getGroupBuildAccess(anon.getID(), testBuildId1));
  }


  /**
   * Helper method
   */
  private void enableAnonymousBuilds() {
    setAnonymousBuildsPropertyValue(SystemProperty.OPTION_CHECKED);
  }


  /**
   * Helper method
   */
  private void disableAnonymousBuilds() {
    setAnonymousBuildsPropertyValue(SystemProperty.OPTION_UNCHECKED);
  }


  /**
   * Helper method.
   *
   * @param value
   */
  private void setAnonymousBuildsPropertyValue(final String value) {
    SystemProperty prop = systemCM.getSystemProperty(SystemProperty.ENABLE_ANONYMOUS_BUILDS);
    if (prop == null) {
      prop = new SystemProperty();
      prop.setPropertyName(SystemProperty.ENABLE_ANONYMOUS_BUILDS);
    }
    prop.setPropertyValue(value);
    systemCM.saveSystemProperty(prop);
  }


  public void test_getUserByName() throws Exception {
    final User u = org.parabuild.ci.security.SecurityManager.getInstance().getUserByName(User.DEFAULT_ADMIN_USER);
    assertNotNull(u);
    assertEquals(User.DEFAULT_ADMIN_USER, u.getName());
  }


  public void test_getUserByNameDontFindUnexisting() throws Exception {
    final User u = SecurityManager.getInstance().getUserByName(User.DEFAULT_ADMIN_USER + System.currentTimeMillis());
    assertNull(u);
  }


  /**
   * Test that out-of-the box configuration contains single
   * 'admin' user with 'admin' password.
   */
  public void test_administratorExists() throws Exception {
    assertTrue(SecurityManager.getInstance().administratorExists("admin", "admin"));
  }


  /**
   */
  public void test_loginAdministrator() throws Exception {
    final User admin = SecurityManager.getInstance().loginAdministrator("admin", "admin");
    assertNotNull(admin);
    assertEquals(admin.getName(), "admin");
    assertEquals(admin.getPassword(), ConfigurationManager.STR_DIGESTED_ADMIN);
    assertEquals(true, admin.isAdmin());
    assertTrue(admin.getUserID() >= 0);
    assertTrue(admin.getTimeStamp() > 0);
  }


  /**
   */
  public void test_loginAdministratorDoesntLoginNonAdmin() throws Exception {
    final User user = SecurityManager.getInstance().getUserByName(TEST_NOT_ADMIN_USER);
    assertNotNull(user);
    final User admin = SecurityManager.getInstance().loginAdministrator(TEST_NOT_ADMIN_USER, TEST_NOT_ADMIN_PASSWD);
    assertNull(admin);
  }


  public void test_getUserResultGroupsReturnEmptyListForAnonymousUser() {
    securityManager.getUserResultGroups(User.UNSAVED_ID);
  }


  public void test_createResultGroupRightsFromAnonGroup() {
    final Group group = new Group();
    group.setName(Group.SYSTEM_ANONYMOUS_GROUP);
    assertEquals(ResultGroupRights.VIEW_ONLY_RIGHTS, securityManager.createResultGroupRightsFromGroup(group));
  }


  public void test_createResultGroupRightsFromAdminGroup() {
    final Group group = new Group();
    group.setName(Group.SYSTEM_ADMIN_GROUP);
    assertEquals(ResultGroupRights.ALL_RIGHTS, securityManager.createResultGroupRightsFromGroup(group));
  }


  public void test_createResultGroupRightsFromGroup() {
    final Group group = new Group();
    group.setName("some_name");
    group.setAllowedToUpdateResultGroup(true);
    final ResultGroupRights resultGroupRightsFromgroup = securityManager.createResultGroupRightsFromGroup(group);
    assertEquals(true, resultGroupRightsFromgroup.isAllowedToUpdateResultGroup());
  }


  public void test_getUserResultGroups() {
    final List userResultGroups = securityManager.getUserResultGroups(TEST_USER_ID_2);
    assertEquals(1, userResultGroups.size());
  }


  public void test_userCanViewResultGroup() {
    assertTrue(!securityManager.userCanViewResultGroup(-1, TEST_RESULT_GROUP_ID_0)); // anonymous
    assertTrue(!securityManager.userCanViewResultGroup(TEST_USER_ID_1, TEST_RESULT_GROUP_ID_0)); // doesn't belong to a group
    assertTrue(securityManager.userCanViewResultGroup(TEST_USER_ID_0, TEST_RESULT_GROUP_ID_0)); // Admin
    assertTrue(securityManager.userCanViewResultGroup(TEST_USER_ID_2, TEST_RESULT_GROUP_ID_0));
  }


  public void test_getUserResultGroupRights() {
    final User user = securityManager.getUser(TEST_USER_ID_2);
    final ResultGroupRights userResultGroupRights = securityManager.getUserResultGroupRights(user, TEST_RESULT_GROUP_ID_0);
    assertTrue(!userResultGroupRights.isAllowedToCreateResultGroup());
    assertTrue(!userResultGroupRights.isAllowedToUpdateResultGroup());
    assertTrue(!userResultGroupRights.isAllowedToDeleteResultGroup());
    assertTrue(!userResultGroupRights.isAllowedToListCommands());
    assertTrue(userResultGroupRights.isAllowedToViewResultGroup());
  }


  public void test_getUserResultGroupHasNoRightsIfAnonymUser() {
    assertEquals(ResultGroupRights.NO_RIGHTS, securityManager.getUserResultGroupRights((User) null, TEST_RESULT_GROUP_ID_0));
  }


  public void test_getUserResultGroupHasAllRightsIfAdminUses() {
    final User adminUser = securityManager.getUser(TEST_USER_ID_0);
    assertEquals(ResultGroupRights.ALL_RIGHTS, securityManager.getUserResultGroupRights(adminUser, TEST_RESULT_GROUP_ID_0));
  }


  public void test_getSecurityGroupResultsReturnsNonMember() {
    final List securityGroupResults = securityManager.getSecurityGroupResults(TEST_GROUP_ID_0);
    assertEquals(1, securityGroupResults.size());
    assertEquals(false, ((GroupMemberVO) securityGroupResults.get(0)).isGroupMember());
  }


  public void test_getSecurityGroupResultsReturnsMember() {
    final List securityGroupResults = securityManager.getSecurityGroupResults(TEST_GROUP_ID_1);
    assertEquals(1, securityGroupResults.size());
    assertEquals(true, ((GroupMemberVO) securityGroupResults.get(0)).isGroupMember());
  }


  public void test_invalidateResultRightsCache() {
    // no exceptions thrown
    securityManager.invalidateResultRightsCache(TEST_RESULT_GROUP_ID_0);
  }


  public void test_invalidateRightSetCaches() {
    // no exceptions thrown
    securityManager.invalidateRightSetCaches();
  }


  public void test_bug1008_securityManagerDoesNotCreateAUserIfItIsInUpperCase() throws FatalConfigurationException {
    // prepare
    TestHelper.setSystemProperty(SystemProperty.LDAP_AUTHENTICATION_ENABLED, SystemProperty.OPTION_CHECKED);
    TestHelper.setSystemProperty(SystemProperty.LDAP_USER_LOOKUP_MODE_DN, SystemProperty.OPTION_CHECKED);
    TestHelper.setSystemProperty(SystemProperty.LDAP_CONNECTION_PASSWORD, "");
    TestHelper.setSystemProperty(SystemProperty.LDAP_CONNECTION_URL, TEST_LDAP_CONNECTION_URL);
    TestHelper.setSystemProperty(SystemProperty.LDAP_USER_EMAIL_ATTRIBUTE_NAME, "mail");
    TestHelper.setSystemProperty(SystemProperty.LDAP_CONNECTION_SECURITY_LEVEL, Integer.toString(ConfigurationConstants.LDAP_CONNECTION_SECURITY_LEVEL_SIMPLE));
    TestHelper.setSystemProperty(SystemProperty.LDAP_USER_DISTINGUISHED_NAME_TEMPLATE, TEST_LDAP_USER_DN_TEMPLATE);
    TestHelper.setSystemProperty(SystemProperty.LDAP_ADD_FIRST_TIME_USER_TO_GROUP, "1");

    final User user1 = securityManager.login(TEST_LDAP_USER_WITH_PLAIN_PASSWORD, TEST_LDAP_PASSWORD);
    assertNotNull(user1);
    final User user2 = securityManager.login(TEST_LDAP_USER_WITH_PLAIN_PASSWORD.toUpperCase(), TEST_LDAP_PASSWORD);
    assertNotNull(user1);
    assertEquals(user1.getUserID(), user2.getUserID());
  }


  public void testLoginAdmin() throws FatalConfigurationException {
    assertNotNull(securityManager.login("admin", "admin"));
  }


  public void test_getSecurityGroupBuilds() {
    final List securityGroupBuilds = securityManager.getSecurityGroupBuilds(3);
    assertTrue(!securityGroupBuilds.isEmpty());
  }


  public void test_getDisplayUserGroups() {
    final List displayUserGroups = securityManager.getDisplayUserGroups(TEST_USER_ID_2);
    assertEquals(3, displayUserGroups.size());
  }


  public void test_getUserName() {
    assertEquals("test_user", securityManager.getUserName(TEST_USER_ID_2, null));
    assertEquals(null, securityManager.getUserName(999999, null));
  }


  public void test_findUsersWithEditRights() {
    // Just make sure it doesn't blow up.
    securityManager.findUsersWithEditRights(TestHelper.TEST_CVS_VALID_BUILD_ID).size();
  }


  public void test_isAllowedToSeeErrors() {
    assertTrue(securityManager.isAllowedToSeeErrors(securityManager.getUser(TEST_USER_ID_2)));
  }


  public void testAssingBuildCreratorBug1440() throws Exception {
    securityManager.assignBuildCreator(1, 1);
    securityManager.assignBuildCreator(1, 1);
  }

  public void testFindUsersByEmail() {
    assertTrue(!securityManager.findUsersByEmail("test@email").isEmpty());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestSecurityManager.class, new String[]{
            "testFindUsersByEmail",
            "testAssingBuildCreratorBug1440",
            "test_isAllowedToSeeErrors",
            "test_findUsersWithEditRights",
            "test_getUserBuildRights",
            "test_bug1008_securityManagerDoesNotCreateAUserIfItIsInUpperCase",
            "test_getUserResultGroupsReturnEmptyListForAnonymousUser",
            "test_isAnonymousAccessEnabled"
    });
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
    cm = ConfigurationManager.getInstance();
    securityManager = SecurityManager.getInstance();
    systemCM = SystemConfigurationManagerFactory.getManager();
  }
}
