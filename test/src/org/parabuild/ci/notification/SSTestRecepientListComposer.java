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

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.merge.MergeNag;
import org.parabuild.ci.object.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;

/**
 * Tests EmailRecipientListComposer
 */
public class SSTestRecepientListComposer extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestRecepientListComposer.class); // NOPMD

  private EmailRecipientListComposer composer = null;
  private Map sourceControlMap = null;
  private Map configuredMap = null;
  private ConfigurationManager cm;
  private static final String TEST_DEFAULT_DOMAIN = "parabuildci.org";


  public SSTestRecepientListComposer(final String s) {
    super(s);
  }


  public void test_addSourceControlUser() throws Exception {
    composer.addBuildConfiguredVCSUserToEmailMap(configuredMap);
    composer.setVersionControlMap(sourceControlMap);

    composer.addSourceControlUser("test1");
    assertEquals(1, composer.getToRecipients().size());

    composer.addSourceControlUser("test2");
    assertEquals(2, composer.getToRecipients().size());

    composer.addSourceControlUser("test3");
    assertEquals(3, composer.getToRecipients().size());

    assertEquals(0, composer.getInvalidEmails().size());
    assertEquals(0, composer.getUnmappedUsers().size());
  }


  public void test_addSourceControlUserFindsUnmapped() throws Exception {
    // set domain to null so than it doesn't participate in mapping
    composer.setDefaultDomain(null);
    composer.addBuildConfiguredVCSUserToEmailMap(configuredMap);
    composer.setVersionControlMap(sourceControlMap);

    composer.addSourceControlUser("test_9999");
    assertEquals(0, composer.getToRecipients().size());
    assertEquals(1, composer.getUnmappedUsers().size());
  }


  public void test_addSourceControlUserMapsSourceControlFirst() throws Exception {
    sourceControlMap.put("test5", "test_scm@test");
    configuredMap.put("test5", makeVCSUserToEmailMap("test5", "test_conf@test"));

    composer.addBuildConfiguredVCSUserToEmailMap(configuredMap);
    composer.setVersionControlMap(sourceControlMap);

    composer.addSourceControlUser("test5");
    assertEquals(1, composer.getToRecipients().size());
    assertEquals("test_scm@test", ((InternetAddress) composer.getToRecipients().get(0)).getAddress());
  }


  public void test_addSourceControlUserMapsConfiguredIfSourceControlNotFound() throws Exception {
    configuredMap.put("test5", makeVCSUserToEmailMap("test5", "test_conf@test"));

    composer.setDefaultDomain(TEST_DEFAULT_DOMAIN);
    composer.addBuildConfiguredVCSUserToEmailMap(configuredMap);
    composer.setVersionControlMap(sourceControlMap);

    composer.addSourceControlUser("test5");
    assertEquals(1, composer.getToRecipients().size());
    assertEquals("test_conf@test", ((InternetAddress) composer.getToRecipients().get(0)).getAddress());
  }


  public void test_addSourceControlUserMapsConfiguredFindsInvalid() throws Exception {
    // put invalid address
    configuredMap.put("test5", makeVCSUserToEmailMap("test5", "test_conf@"));

    composer.addBuildConfiguredVCSUserToEmailMap(configuredMap);
    composer.setVersionControlMap(sourceControlMap);

    composer.addSourceControlUser("test5");
    assertEquals(0, composer.getToRecipients().size());
    assertEquals(1, composer.getInvalidEmails().size());
    assertEquals("test_conf@", composer.getInvalidEmails().get(0));
  }


  public void test_addWatchers() throws Exception {
    final String TEST_WATCHER_EMAIL = "test_watcher@test";

    composer.addBuildConfiguredVCSUserToEmailMap(configuredMap);
    composer.setVersionControlMap(sourceControlMap);

    final BuildWatcher bw = new BuildWatcher();
    bw.setEmail(TEST_WATCHER_EMAIL);
    bw.setLevel(BuildWatcher.LEVEL_BROKEN);
    final List list = new ArrayList();
    list.add(bw);
    composer.addWatchers(BuildWatcher.LEVEL_BROKEN, list);
    assertEquals(1, composer.getToRecipients().size());
  }


  public void test_makeRecepients() throws Exception {
    final BuildRun buildRun = cm.getBuildRun(3);
    final Map testMap = new HashMap(11);
    for (final Iterator iterator = cm.getVCSUserToEmailMap(buildRun.getBuildID()).values().iterator(); iterator.hasNext();) {
      final VCSUserToEmailMap userToEmailMap = (VCSUserToEmailMap) iterator.next();
      testMap.put(userToEmailMap.getUserName(), userToEmailMap.getUserEmail());
    }
    if (log.isDebugEnabled()) log.debug("testMap: " + testMap);
    final EmailRecipients emailRecipients = composer.makeRecipients(buildRun, testMap, false, true, false, true, BuildWatcher.LEVEL_BROKEN);
    final List allAddresses = emailRecipients.getAllAddresses();
    if (log.isDebugEnabled()) log.debug("allAddresses: " + allAddresses);
    assertEquals(2, allAddresses.size());
  }


  public void test_makeRecepientsWithFilteringEnabled() throws Exception {
    // NOTE: simeshev@parabuilci.org - currently just checks
    // if it does not blow up.

    // alter configuration to enable filtering on failures
    final BuildRun buildRun = cm.getBuildRun(3);
    final BuildConfigAttribute buildConfigAttribute = new BuildConfigAttribute(buildRun.getBuildID(), BuildConfigAttribute.SEND_FAILURE_ONCE, BuildConfigAttribute.OPTION_CHECKED);
    cm.saveObject(buildConfigAttribute);
    // run
    final Map vcsUserToEmailMap = cm.getVCSUserToEmailMap(buildRun.getBuildID());
    composer.makeRecipients(buildRun, vcsUserToEmailMap, false, true, false, true, BuildWatcher.LEVEL_BROKEN);
  }


  public void test_bug709_DoesNotIncludeFailureWatchersIfStepSuccessful() throws AddressException {
    // make test watcher list
    final String testWatcherEmail = "test@watcher";
    final List watchers = new ArrayList(1);
    watchers.add(new BuildWatcher(testWatcherEmail, BuildWatcher.LEVEL_BROKEN));
    composer.addWatchers(BuildWatcher.LEVEL_SUCCESS, watchers);
    // make recipients
    final EmailRecipients recipients = composer.makeRecipients(cm.getBuildRun(3), new HashMap(), false, true, false, true, BuildWatcher.LEVEL_BROKEN);
    final List allAddresses = recipients.getAllAddresses();
    for (Iterator i = allAddresses.iterator(); i.hasNext();) {
      final String address = ((InternetAddress) i.next()).getAddress();
      assertTrue(!address.equals(testWatcherEmail));
    }
  }


  /**
   * Tests adding watcher with level higher than message level.
   *
   * @throws AddressException
   */
  public void test_addWatcher() throws AddressException {
    // make test watcher list
    final String TEST_VALID_WATCHER_EMAIL = "test@watcher";
    final String TEST_VALID_WATCHER_USERNAME = "test_watcher_user_name";
    final List watchers = new ArrayList(1);
    watchers.add(new BuildWatcher(TEST_VALID_WATCHER_EMAIL, BuildWatcher.LEVEL_SYSTEM_ERROR));
    watchers.add(new BuildWatcher(TEST_VALID_WATCHER_USERNAME, BuildWatcher.LEVEL_SYSTEM_ERROR));
    composer.addWatchers(BuildWatcher.LEVEL_BROKEN, watchers);
    // make recipients
    final EmailRecipients recipients = composer.makeRecipients(cm.getBuildRun(3), new HashMap(), false, true, false, true, BuildWatcher.LEVEL_BROKEN);
    final List allAddresses = recipients.getAllAddresses();
    boolean emailFound = false;
    boolean emailFromUserNameFound = false;
    for (Iterator i = allAddresses.iterator(); i.hasNext();) {
      final String address = ((InternetAddress) i.next()).getAddress();
      emailFound = emailFound || address.equals(TEST_VALID_WATCHER_EMAIL);
      emailFromUserNameFound = emailFromUserNameFound || address.equals(TEST_VALID_WATCHER_USERNAME + '@' + TEST_DEFAULT_DOMAIN);
    }
    assertTrue(emailFound);
    assertTrue(emailFromUserNameFound);
  }


  /**
   * Tests add security groups as build watchers
   */
  public void test_cr948_addSecurityGroupWatcher() throws AddressException {
    // make test watcher list
    final String TEST_VALID_WATCHER_GROUP_NAME = "Test group 1";
    final List watchers = new ArrayList(1);
    watchers.add(new BuildWatcher(TEST_VALID_WATCHER_GROUP_NAME, BuildWatcher.LEVEL_SYSTEM_ERROR));
    composer.addWatchers(BuildWatcher.LEVEL_BROKEN, watchers);

    // make recipients
    boolean emailFromGroupUserFound = false;
    final EmailRecipients recipients = composer.makeRecipients(cm.getBuildRun(3), new HashMap(), false, true, false, true, BuildWatcher.LEVEL_BROKEN);
    for (final Iterator i = recipients.getAllAddresses().iterator(); i.hasNext();) {
      final String address = ((InternetAddress) i.next()).getAddress();
      emailFromGroupUserFound = emailFromGroupUserFound || address.equals("imeshev@yahoo.com");
    }
    assertTrue(emailFromGroupUserFound);
  }


  /**
   * #824 - Default domain takes precedence over e-mails
   * stored in CVS
   */
  public void test_bug824_addSourceControlUserMapsVSCMapFirst() throws AddressException {
//    composer.setConfiguredMap(configuredMap);
    composer.setVersionControlMap(sourceControlMap);
    composer.addSourceControlUser("test2");
    assertEquals(1, composer.getToRecipients().size());
    assertEquals("test2@test", ((InternetAddress) composer.getToRecipients().get(0)).getAddress());
  }


  /**
   */
  public void test_makeRecipients() throws AddressException {
    final ArrayList nagReport = new ArrayList();
    nagReport.add(new MergeNag() {
      public String getUserName() {
        return "test_user";
      }


      public List getPendingChangeLists() {
        return Collections.EMPTY_LIST;
      }
    });
    composer.makeRecipients(nagReport);
  }


  /**
   */
  public void test_cr834_buildStarterGetsIntoList() throws AddressException {
    final BuildRun buildRun = cm.getBuildRun(3);
    final BuildRunAttribute bra = new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_STARTED_USER_ID, "3");
    cm.saveObject(bra);
    final EmailRecipients emailRecipients = composer.makeRecipients(buildRun, Collections.EMPTY_MAP, false, true, false, true, BuildWatcher.LEVEL_BROKEN);
    assertEquals(3, emailRecipients.getAllAddresses().size());
    // NOTE: vimeshev - 2006-02-22 - this may break if returned list is ordered differently
    assertEquals("test@email", ((InternetAddress) emailRecipients.getAllAddresses().get(1)).getAddress());
  }


  /**
   */
  public void test_cr936_firstStepIsConsidered() throws AddressException {
    final BuildRun buildRun = cm.getBuildRun(3);
    // set to send only to the first start
    final BuildConfigAttribute bca = new BuildConfigAttribute(buildRun.getBuildID(),
            BuildConfigAttribute.SEND_START_NOTICE_FOR_FIRST_STEP_ONLY, BuildConfigAttribute.OPTION_CHECKED);
    cm.saveObject(bca);
    composer.addSourceControlUser("test2");

    // says filter is ignored
    EmailRecipients emailRecipients = composer.makeRecipients(buildRun, Collections.EMPTY_MAP, false, false, false, true, BuildWatcher.LEVEL_BROKEN);
    assertEquals(3, emailRecipients.getAllAddresses().size());

    // says filter is considered
    emailRecipients = composer.makeRecipients(buildRun, Collections.EMPTY_MAP, false, true, false, true, BuildWatcher.LEVEL_BROKEN);
    assertEquals(0, emailRecipients.getAllAddresses().size());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestRecepientListComposer.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();

    composer = new EmailRecipientListComposer();
    composer.setDefaultDomain(TEST_DEFAULT_DOMAIN);

    sourceControlMap = new HashMap(3);
    sourceControlMap.put("test1", "test1@test");
    sourceControlMap.put("test2", "test2@test");

    final VCSUserToEmailMap vcsUserToEmailMap1 = makeVCSUserToEmailMap("test3", "test3@test");
    final VCSUserToEmailMap vcsUserToEmailMap2 = makeVCSUserToEmailMap("test4", "test4@test");
    configuredMap = new HashMap(3);
    configuredMap.put(vcsUserToEmailMap1.getUserName(), vcsUserToEmailMap1);
    configuredMap.put(vcsUserToEmailMap2.getUserName(), vcsUserToEmailMap2);
  }


  private VCSUserToEmailMap makeVCSUserToEmailMap(final String user, final String email) {
    final VCSUserToEmailMap vcsUserToEmailMap = new VCSUserToEmailMap();
    vcsUserToEmailMap.setUserName(user);
    vcsUserToEmailMap.setUserEmail(email);
    return vcsUserToEmailMap;
  }
}
