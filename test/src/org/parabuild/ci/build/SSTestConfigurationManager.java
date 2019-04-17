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
package org.parabuild.ci.build;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.BuildConfigCloner;
import org.parabuild.ci.configuration.BuildRunParticipantVO;
import org.parabuild.ci.configuration.ChangeListIssueBinding;
import org.parabuild.ci.configuration.ChangeListsAndIssues;
import org.parabuild.ci.configuration.ChangeListsAndIssuesImpl;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.ReleaseNoteChangeList;
import org.parabuild.ci.configuration.ReleaseNoteReport;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.BuildChangeList;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.BuildRunParticipant;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.object.IssueAttribute;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.IssueTrackerProperty;
import org.parabuild.ci.object.LabelProperty;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.object.ScheduleItem;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.object.VCSUserToEmailMap;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.ServiceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class SSTestConfigurationManager extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestConfigurationManager.class);

  private ConfigurationManager configManager = null;

  private static final int TEST_BUILD_ID_1 = 1;
  private static final int TEST_BUILD_ID_3 = 3;
  private static final int TEST_BUILD_ID_4 = 4;
  private static final int TEST_LOG_CONFIG_ID = 1;
  private static final String TEST_BUILD_ID_1_NAME = "cvs_test_build";
  private static final String LONG_BUILD_NAME = "0123456789012345678901234567890123456789012345678901234567890123456789";


  public SSTestConfigurationManager(final String s) {
    super(s);
  }


  public void test_STARTED_UNDER_ROOT_USER() {
    assertEquals(false, StringUtils.isBlank(ConfigurationManager.STARTUP_USER));
    assertEquals(false, ConfigurationManager.BLOCK_ROOT_USER || ConfigurationManager.BLOCK_ADMIN_USER);
  }


  /**
   * Makes test that configuration configuations are not null
   */
  public void test_getExistingBuildConfigs() throws Exception {
    final List configurations = configManager.getExistingBuildConfigs();
    assertNotNull(configurations);
    assertTrue(!configurations.isEmpty()); // this relays on test data populated by DBUnit
    final BuildConfig config = (BuildConfig) configurations.get(0);
    assertTrue(config.getBuildID() != BuildConfig.UNSAVED_ID);
    assertNotNull(config.getBuildName());
  }


  /**
   *
   */
  public void test_getAllBuildConfigurations() throws Exception {
    final List configurations = configManager.getAllBuildConfigurations();
    assertNotNull(configurations);
    assertTrue(!configurations.isEmpty());
    final BuildConfig config = (BuildConfig) configurations.get(0);
    assertTrue(config.getBuildID() != BuildConfig.UNSAVED_ID);
    assertNotNull(config.getBuildName());
  }


  /**
   * Makes test that configuration sequence is retrievable
   */
  public void test_getAllBuildSequences() throws Exception {
    final List sequences = configManager.getAllBuildSequences(TEST_BUILD_ID_1, BuildStepType.BUILD);
    assertNotNull(sequences);
    assertTrue(!sequences.isEmpty()); // this relays on test data populated by DBUnit
    final BuildSequence sequence = (BuildSequence) sequences.get(0);
    assertTrue(sequence.getBuildID() != BuildConfig.UNSAVED_ID);
    assertNotNull(sequence.getStepName());
  }


  /**
   * Makes test that configuration sequence is retrievable
   */
  public void test_getBuildSequence() throws Exception {
    final List sequences = configManager.getEnabledBuildSequences(TEST_BUILD_ID_1, BuildStepType.BUILD);
    assertNotNull(sequences);
    assertTrue(!sequences.isEmpty()); // this relays on test data populated by DBUnit
    final BuildSequence sequence = (BuildSequence) sequences.get(0);
    assertTrue(sequence.getBuildID() != BuildConfig.UNSAVED_ID);
    assertNotNull(sequence.getStepName());
  }


  /**
   * Make sure all system properties can be requested
   */
  public void test_getSystemProperties() throws Exception {
    final List configurations = SystemConfigurationManagerFactory.getManager().getSystemProperties();
    assertNotNull(configurations);
    assertTrue(!configurations.isEmpty());
  }


  /**
   * Tests saving modified property
   */
  public void test_SaveNewSystemProperty() throws Exception {
    final SystemProperty systemProperty = new SystemProperty();
    systemProperty.setPropertyName("test.property.name");
    systemProperty.setPropertyValue("test.property.value");
    final Session session = configManager.openSession();
    final Transaction transacton = session.beginTransaction();
    session.saveOrUpdate(systemProperty);
    transacton.commit();

    session.close();
  }


  /**
   * Checks if single configuration config can be retrieved
   */
  public void test_getBuildConfiguration() throws Exception {
    final BuildConfig config = configManager.getBuildConfiguration(TEST_BUILD_ID_1);
    assertTrue(config.getBuildID() != BuildConfig.UNSAVED_ID);
    assertNotNull(config.getBuildName());
  }


  /**
   * Checks if config can be retrieved via StepLog
   */
  public void test_getBuildConfigViaStepLog() throws Exception {
    final StepLog stepLog = configManager.getStepLog(3);
    assertNotNull(stepLog);

    final BuildConfig config = configManager.getBuildRunConfig(stepLog);
    assertTrue(config.getBuildID() != BuildConfig.UNSAVED_ID);
    assertEquals(9, config.getBuildID());
    assertNotNull(config.getBuildName());
  }


  /**
   * Checks if a single build config can be stored
   */
  public void test_saveBuildConfiguration() throws Exception {
    BuildConfig config = configManager.getBuildConfiguration(TEST_BUILD_ID_1);
    config.setSourceControlEmail(false);
    configManager.save(config);
    config = configManager.getBuildConfiguration(TEST_BUILD_ID_1);
    assertTrue(!config.getSourceControlEmail());

    // check if scheme could fit long build name
    config.setBuildName(LONG_BUILD_NAME);
    configManager.save(config);
    config = configManager.getBuildConfiguration(TEST_BUILD_ID_1);
    assertEquals(LONG_BUILD_NAME.length(), config.getBuildName().length());
  }


  /**
   * Checks if single configuration config return null for not
   * found ID
   */
  public void test_getBuildConfigurationDontFindUnexisting() throws Exception {
    final BuildConfig config = configManager.getBuildConfiguration(99999999);
    assertTrue(config == null);
  }


  /**
   * Checks if search for build config by name is cases
   * insensitive
   */
  public void test_findActiveBuildConfigByName() {
    BuildConfig conf = configManager.findActiveBuildConfigByName(TEST_BUILD_ID_1_NAME);
    assertNotNull(conf);
    conf = configManager.findActiveBuildConfigByName(TEST_BUILD_ID_1_NAME.toUpperCase());
    assertNotNull(conf);
    conf = configManager.findActiveBuildConfigByName(TEST_BUILD_ID_1_NAME.toLowerCase());
    assertNotNull(conf);
  }


  /**
   * Checks if build sequence can be deleted
   */
  public void test_deleteBuildSequence() throws Exception {
    List seqs = configManager.getAllBuildSequences(TEST_BUILD_ID_1, BuildStepType.BUILD);
    final int preDeleteSize = seqs.size();
    assertTrue(preDeleteSize > 0);

    final BuildSequence sequence = (BuildSequence) seqs.get(0);
    configManager.delete(sequence);
    seqs = configManager.getAllBuildSequences(TEST_BUILD_ID_1, BuildStepType.BUILD);
    assertTrue(seqs.size() == (preDeleteSize - 1));
  }


  /**
   * Make sure user to email maps associated with the given
   * configuration can be retrieved
   */
  public void test_getVCSUserToEmailMaps() throws Exception {
    final List userToEmailMaps = configManager.getVCSUserToEmailMaps(TEST_BUILD_ID_1);
    assertNotNull(userToEmailMaps);
    assertTrue(!userToEmailMaps.isEmpty());
  }


  /**
   * Checks if VCSUserToEmailMap can be deleted
   */
  public void test_deleteVCSUserToEmailMap() {
    List maps = configManager.getVCSUserToEmailMaps(TEST_BUILD_ID_1);
    final int preDeleteSize = maps.size();
    assertTrue(preDeleteSize > 0);

    final VCSUserToEmailMap map = (VCSUserToEmailMap) maps.get(0);
    configManager.delete(map);
    maps = configManager.getVCSUserToEmailMaps(TEST_BUILD_ID_1);
    assertTrue(maps.size() == (preDeleteSize - 1));
  }


  /**
   * Make sure user to email maps associated with the given
   * configuration can be retrieved
   */
  public void test_getScheduleProperties() throws Exception {
    final List userToEmailMaps = configManager.getScheduleSettings(TEST_BUILD_ID_1);
    assertNotNull(userToEmailMaps);
    assertTrue(!userToEmailMaps.isEmpty());
  }


  public void test_getWorkDirectoryName() {
    final String dirName = ConfigurationManager.getSystemWorkDirectoryName();
    assertNotNull(dirName);
  }


  /**
   * Test saving build run
   */
  public void test_saveBuildRunResult() throws Exception {
    final BuildRun buildRun = makeAndSaveBuildRun();
    assertTrue(buildRun.getBuildRunID() >= 0);
  }


  /**
   * Test saving build run
   */
  public void test_saveStepRunResult() throws Exception {
    final BuildRun buildRun = makeAndSaveBuildRun();
    final StepRun stepRun = new StepRun();
    stepRun.setBuildRunID(buildRun.getBuildRunID());
    stepRun.setName("BUILD");
    stepRun.setStartedAt(new Date());
    stepRun.setFinishedAt(new Date());
    configManager.save(stepRun);
    assertTrue(stepRun.getID() >= 0);
  }


  private BuildRun makeAndSaveBuildRun() {
    final BuildRun buildRun = new BuildRun();
    assertEquals(-1, buildRun.getBuildRunID());
    final BuildConfigCloner cloner = new BuildConfigCloner();
    final BuildConfig buildRunConfig = cloner.createBuildRunConfig(TEST_BUILD_ID_1, null);
    buildRun.setBuildID(buildRunConfig.getBuildID());
    buildRun.setActiveBuildID(TEST_BUILD_ID_1);
    buildRun.setStartedAt(new Date());
    buildRun.setBuildRunNumber(configManager.getNewBuildNumber(TEST_BUILD_ID_1));
    buildRun.setChangeListNumber("111");
    buildRun.setLastStepRunName("BUILD");
    configManager.save(buildRun);
    return buildRun;
  }


  /**
   * Test that change list can be saved with string attrbiutes
   * exeeding allowed lengths.
   */
  public void test_bug526() throws Exception {
    final ChangeList changeList = new ChangeList();
    final String branch = makeLongString(2048);
    changeList.setCreatedAt(new Date());
    changeList.setBranch(branch);
    changeList.setClient(makeLongString(2048));
    changeList.setDescription(makeLongString(2048));
    changeList.setEmail(makeLongString(2048));
    changeList.setNumber(makeLongString(2048));
    changeList.setUser(makeLongString(2048));
    final Integer id1 = (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdate(changeList);
        return new Integer(changeList.getChangeListID());
      }
    });
    final int id = id1.intValue();
    assertTrue(id >= 0);
    final ChangeList read = (ChangeList) configManager.getObject(ChangeList.class, id);
    assertTrue(!read.getBranch().equals(branch));
    assertTrue(read.getBranch().length() < branch.length());
  }


  private String makeLongString(final int length) {
    final StringBuffer sb = new StringBuffer(length);
    for (int i = 0; i < length; i++) {
      sb.append('*');
    }
    return sb.toString();
  }


  /**
   * Test saving CVS change
   */
  public void test_saveChange() throws Exception {
    final ChangeList changeList = new ChangeList();
    changeList.setCreatedAt(new Date());
    changeList.setUser("test_user");
    changeList.setDescription("test description");
    final int changeListID = ((ChangeList) configManager.saveObject(changeList)).getChangeListID();

    final Change change = new Change();
    change.setChangeListID(changeListID);
    change.setChangeType(Change.TYPE_ADDED);
    change.setFilePath("test/file/path");
    change.setRevision("1.0");
    assertTrue(configManager.save(change) >= 0);
  }


  /**
   * Test saving build run participant
   */
  public void test_saveBuildRunParticipant() throws Exception {

    final BuildRun buildRun = makeAndSaveBuildRun();
    final int runID = buildRun.getBuildRunID();

    final ChangeList changeList = new ChangeList();
    changeList.setCreatedAt(new Date());
    changeList.setUser("test_user");
    changeList.setDescription("test description");
    final int changeListID = ((ChangeList) configManager.saveObject(changeList)).getChangeListID();

    final BuildRunParticipant participant = new BuildRunParticipant();
    participant.setBuildRunID(runID);
    participant.setChangeListID(changeListID);
    participant.setFirstBuildRunID(runID);
    participant.setFirstBuildRunNumber(buildRun.getBuildRunNumber());
    assertTrue(configManager.save(participant) >= 0);
  }


  public void test_getChangeListByBuildAndChangeList() throws Exception {
    final ChangeList buildChangeList = configManager.getChangeList(TestHelper.TEST_P4_VALID_BUILD_ID, 8);
    assertNotNull(buildChangeList);
    assertEquals(8, buildChangeList.getChangeListID());
  }


  public void test_getChangeListByBuildRunAndChangeListNumber() throws Exception {
    assertNotNull(configManager.getChangeList(1, "1"));
    assertNotNull(configManager.getChangeList(12, "5291"));
  }


  public void test_copyChangeListsToBuild() throws Exception {
    final int newChangeListID = configManager.copyChangeListsToBuild(TestHelper.TEST_P4_VALID_BUILD_ID, TestHelper.TEST_RECURRENT_BUILD_ID, 7);
    assertEquals(8, newChangeListID);
  }


  /**
   */
  public void test_getSequenceBuildConfig() throws Exception {
    final StepRun stepRun = new StepRun();
    stepRun.setBuildRunID(1);
    stepRun.setName("BUILD");
    stepRun.setStartedAt(new Date());
    stepRun.setFinishedAt(new Date());
    final BuildConfig buildConfiguration = configManager.getBuildRunConfig(stepRun);
    assertNotNull(buildConfiguration);
  }


  /**
   * Tests getting e-mail map
   */
  public void test_getVCSUserToEmailMap() {
    final Map result = configManager.getVCSUserToEmailMap(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertTrue(!result.isEmpty());

    for (Iterator iter = result.values().iterator(); iter.hasNext(); ) {
      final Object o = iter.next();
      assertTrue(o instanceof VCSUserToEmailMap);
    }

    for (Iterator iter = result.keySet().iterator(); iter.hasNext(); ) {
      final Object o = iter.next();
      assertTrue(o instanceof String);
    }
  }


  /**
   * Tests getting e-mail map
   */
  public void test_getVCSUserToEmailMap_Bug779() {
    final String userNameWithSpaces = " user1 ";
    final String userNameWOSpaces = userNameWithSpaces.trim();

    // validate we have a user with spaces in the name
    final VCSUserToEmailMap emailMap = (VCSUserToEmailMap) configManager.getObject(VCSUserToEmailMap.class, 1);
    assertEquals(emailMap.getUserName(), userNameWithSpaces);

    // assert
    final Map result = configManager.getVCSUserToEmailMap(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertNotNull(result.get(userNameWOSpaces));
  }


  /**
   * Tests getting build participants from sequence run
   */
  public void test_getBuildParticipantsNames() throws Exception {
    final StepRun stepRun = new StepRun();
    stepRun.setBuildRunID(1);
    stepRun.setName("BUILD");
    stepRun.setStartedAt(new Date());
    stepRun.setFinishedAt(new Date());
    final List result = configManager.getBuildParticipantsNames(stepRun);
    assertTrue(!result.isEmpty());

    boolean found = false;
    for (Iterator iter = result.iterator(); iter.hasNext(); ) {
      final String versionControlUserName = (String) iter.next();
      found = versionControlUserName.equals("test");
    }
    assertTrue(found);
  }


  public void test_createBuildRunParticipants() {

    final int TEST_CURRENT_BUILD_RUN_ID = 3;
    final int TEST_CURRENT_CHANGE_LIST_ID = 3;
    final BuildRun buildRun = configManager.getBuildRun(TEST_CURRENT_BUILD_RUN_ID);
    assertNotNull(buildRun);

    // check number of build run participants before creting them, should be 0
    List participants = configManager.getBuildRunParticipants(buildRun);
    assertEquals("Number od participants before creating them", 1, participants.size());
    configManager.createBuildRunParticipants(buildRun, TEST_CURRENT_CHANGE_LIST_ID, true);
    participants = configManager.getBuildRunParticipants(buildRun);

    // number of participants should be 2
    // NOTE: vimeshev - 09/07/2003 - this relays on the content
    // of the dataset.xml, which currently has a previos build
    // broken and containing one change list; there is one new
    // change list, summa is two.
    assertEquals(3, participants.size());
  }


  public void test_getBuildRuns() {
    Collection result = configManager.getBuildRuns(TestHelper.TEST_CVS_VALID_BUILD_ID, 10);
    assertTrue(result.size() <= 10);

    result = configManager.getBuildRuns(TestHelper.TEST_CVS_VALID_BUILD_ID, 2);
    assertTrue(result.size() <= 2);
  }


  public void test_getCompletedBuildRuns() {
    Collection result = configManager.getCompletedBuildRuns(TestHelper.TEST_CVS_VALID_BUILD_ID, 0, 10);
    assertTrue(result.size() <= 10);
    for (Iterator i = result.iterator(); i.hasNext(); ) {
      final BuildRun br = (BuildRun) i.next();
      assertEquals(BuildRun.RUN_COMPLETE, br.getComplete());
    }

    result = configManager.getCompletedBuildRuns(TestHelper.TEST_CVS_VALID_BUILD_ID, 0, 2);
    assertTrue(result.size() <= 2);
  }


  public void test_getLastCompleteBuildRun() {

    // make sure returns correct build run result
    // NOTE: vimeshev - 09/07/2003 - this test relays on the fact that the
    // latest complete build run for build TestHelper.TEST_CVS_VALID_BUILD_ID
    // defined in dataset.xml has ID == 2. If new complete build runs are added,
    // this test will have to be adjusted to pass.
    final BuildRun buildRun = configManager.getLastCompleteBuildRun(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertNotNull(buildRun);
    assertEquals(2, buildRun.getBuildRunID());
    assertTrue(buildRun.completed());

    // additionally test that copy preserves completion
    final BuildRun buildRunCopy = BuildRun.copy(buildRun);
    assertTrue(buildRunCopy.completed());

    // make sure can not find non-exisitng build
    if (ConfigurationManager.validateActiveID) {
      try {
        assertNull(configManager.getLastCompleteBuildRun(999999999));
      } catch (IllegalArgumentException e) {

      }
    } else {
      assertNull(configManager.getLastCompleteBuildRun(999999999));
    }
  }


  public void test_getScheduleSetting() {
    final ScheduleProperty property = configManager.getScheduleSetting(TestHelper.TEST_CVS_VALID_BUILD_ID, ScheduleProperty.AUTO_POLL_INTERVAL);
    assertNotNull(property);
  }


  public void test_getLabelSetting() {
    final LabelProperty property = configManager.getLabelSetting(TestHelper.TEST_CVS_VALID_BUILD_ID, LabelProperty.LABEL_TYPE);
    assertNotNull(property);
  }


  public void test_getLabelSettings() {
    final List result = configManager.getLabelSettings(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertNotNull(result);
    assertTrue(!result.isEmpty());
  }


  /**
   * Tesst getting latest change list ID
   */
  public void test_getLatestChangeListID() throws Exception {

    // NOTE: vimeshev - 09/10/2003 - these asserts relay on the current
    // dataset. If more change lists are added, it may required adjustment

    int latestChangeListID = configManager.getLatestChangeListID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertEquals(9, latestChangeListID);

    latestChangeListID = configManager.getLatestChangeListID(TestHelper.TEST_CVS_EMPTY_BUILD_ID);
    assertEquals(ChangeList.UNSAVED_ID, latestChangeListID);
  }


  /**
   * Tests getChangesOrderedByUserAndComment
   */
  public void test_getChangeListsOrderedByUserAndComment() throws Exception {
    final List result = configManager.getChangeListsOrderedByUserAndCommentAndDate(1);
    assertTrue(!result.isEmpty());
  }


  /**
   * Tests getChangeListsOrderedByDate
   */
  public void test_getChangeListsOrderedByDate() throws Exception {
    final List result = configManager.getChangeListsOrderedByDate(1);
    assertTrue(!result.isEmpty());
  }


  /**
   * Tests getChangeListsOrderedByDate
   */
  public void test_getBuildRunParticipantsOrderedByDate() throws Exception {
    final List result = configManager.getBuildRunParticipantsOrderedByDate(1);
    assertTrue(!result.isEmpty());
    assertTrue(result.get(0) instanceof BuildRunParticipantVO);
  }


  /**
   * Tests getChanges
   *
   * @throws Exception
   */
  public void test_getChanges() throws Exception {
    final List result = configManager.getChanges(1);
    assertNotNull(result);
    assertTrue(!result.isEmpty());
  }


  public void test_makeBuildLogURL() throws Exception {
    final StepLog stepLog = new StepLog();
    stepLog.setID(1000);
    final String url = configManager.makeBuildLogURL(stepLog);
    assertNotNull(url);
    assertTrue(url.indexOf("localhost") == -1);
    assertTrue(url.indexOf(":" + ServiceManager.getInstance().getListenPort()) > 0);
    if (log.isDebugEnabled()) log.debug("url = " + url);
  }


  public void test_getStepRuns() throws Exception {
    final List result = configManager.getStepRuns(1);
    assertNotNull(result);
  }


  public void test_saveScheduleItems() throws Exception {
    // item 1
    final ScheduleItem item1 = new ScheduleItem();
    item1.setBuildID(TestHelper.TEST_RECURRENT_BUILD_ID);
    item1.setDayOfMonth("1");
    item1.setHour("1");
    item1.setDayOfWeek("1");

    // item 2
    final ScheduleItem item2 = new ScheduleItem();
    item2.setBuildID(TestHelper.TEST_RECURRENT_BUILD_ID);
    item2.setDayOfMonth("2");
    item2.setHour("2");
    item2.setDayOfWeek("2");

    // compose list
    final List items = new ArrayList(2);
    items.add(item1);
    items.add(item2);

    configManager.saveScheduleItems(items);
    assertEquals(4, configManager.getScheduleItems(TestHelper.TEST_RECURRENT_BUILD_ID).size());
  }


  public void test_deleteScheduleItems() throws Exception {
    // get items
    final List list = configManager.getScheduleItems(TestHelper.TEST_RECURRENT_BUILD_ID);
    assertNotNull(list);
    assertTrue(!list.isEmpty());

    // delete items
    configManager.deleteScheduleItems(list);
    assertEquals(0, configManager.getScheduleItems(TestHelper.TEST_RECURRENT_BUILD_ID).size());
  }


  public void test_getEffectiveSourceControlSettings() throws Exception {
    // general test
    final int testBuild = TestHelper.TEST_DEPENDENT_PARALLEL_BUILD_ID_1;
    assertTrue(configManager.getEffectiveSourceControlSettings(testBuild).size() > 1);
    // test that it does not return custom directory
    TestHelper.setSourceControlProperty(TestHelper.TEST_LEADER_PARALLEL_BUILD_ID, SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, "/test/dir");
    assertNull(configManager.getEffectiveSourceControlSettingsAsMap(testBuild).get(SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE));
  }


  public void test_getSourceControlSettings() throws Exception {
    final List settings = configManager.getSourceControlSettings(TestHelper.TEST_P4_VALID_BUILD_ID);
    assertTrue(settings.size() > 1);
    boolean found = false;
    for (int i = 0; i < settings.size(); i++) {
      final SourceControlSetting scs = (SourceControlSetting) settings.get(i);
      if (scs.getPropertyName().equals(SourceControlSetting.P4_DEPOT_PATH)) {
        found = true;
        assertEquals("//test/sourceline/alwaysvalid/...", scs.getPropertyValue());
      }
    }
    assertTrue(found);
  }


  public void test_saveSourceControlSettings() throws Exception {
    // get setting
    final Map effectiveSourceControlSettingsAsMap = configManager.getEffectiveSourceControlSettingsAsMap(TestHelper.TEST_P4_VALID_BUILD_ID);
    final SourceControlSetting scs = (SourceControlSetting) effectiveSourceControlSettingsAsMap.get(SourceControlSetting.P4_DEPOT_PATH);

    // prepare test value
    final StringBuffer sb = new StringBuffer(10000);
    for (int i = 0; i < 1000; i++) sb.append("0123456789");

    // set and save
    scs.setPropertyValue(sb.toString());
    configManager.saveSourceControlSettings(TestHelper.TEST_P4_VALID_BUILD_ID, new ArrayList(effectiveSourceControlSettingsAsMap.values()));

    // assert got saved correctly
    final Map newEffectiveSourceControlSettingsAsMap = configManager.getEffectiveSourceControlSettingsAsMap(TestHelper.TEST_P4_VALID_BUILD_ID);
    final SourceControlSetting newScs = (SourceControlSetting) newEffectiveSourceControlSettingsAsMap.get(SourceControlSetting.P4_DEPOT_PATH);
    assertEquals(sb.toString(), newScs.getPropertyValue());
  }


  public void test_getReferencedSourceControlSettings() throws Exception {
    final List settings = configManager.getEffectiveSourceControlSettings(TestHelper.TEST_RECURRENT_BUILD_ID);
    assertTrue(settings.size() > 1);
  }


  public void test_getChangeList() throws Exception {
    final ChangeList chl = configManager.getChangeList(1);
    assertNotNull(chl);
  }


  public void test_getFirstBokenLog() throws Exception {
    final StepLog log = configManager.getFirstBokenLog(1);
    assertEquals(3, log.getID());
  }


  public void test_getNewBuildNumber() throws Exception {
    final int first = configManager.getNewBuildNumber(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertTrue(first > 0);
    final int second = configManager.getNewBuildNumber(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertEquals(1, second - first);
  }


  public void test_getWatchers() throws Exception {
    configManager.getWatchers(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }


  public void test_onlyDefaultAdminExists() throws Exception {
    // add more admins
    final User adm = new User();
    adm.setName("new_admin_name");
    adm.setPassword(StringUtils.digest("new_admin_password"));
    adm.setEmail("new_admin@email");
    adm.setAdmin(true);
    configManager.saveObject(adm);
    assertTrue(!SecurityManager.getInstance().onlyDefaultAdminExists());
  }


  public void test_getBuildAttributes() {
    configManager.getBuildAttributes(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }


  public void test_getLogConfigs() {
    configManager.getLogConfigs(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }


  public void test_saveLogConfig() {
    // len before
    final int lengthBefore = configManager.getLogConfigs(TestHelper.TEST_CVS_VALID_BUILD_ID).size();
    // create
    final LogConfig logConfig = new LogConfig();
    logConfig.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    logConfig.setDescription("Tomcat logs");
    logConfig.setPath("temp/result/prod/logs");
    logConfig.setType(LogConfig.LOG_TYPE_TEXT_DIR);
    // save
    configManager.save(logConfig);
    // assert created
    final int lengthAfter = configManager.getLogConfigs(TestHelper.TEST_CVS_VALID_BUILD_ID).size();
    assertEquals(lengthBefore + 1, lengthAfter);
  }


  public void test_getLogConfigAttributes() {
    configManager.getLogConfigProperties(TEST_LOG_CONFIG_ID);
  }


  public void test_saveLogConfigAttribute() {
    // len before
    final int lengthBefore = configManager.getLogConfigProperties(TEST_LOG_CONFIG_ID).size();
    // create
    final LogConfigProperty logConfigProp = new LogConfigProperty();
    logConfigProp.setLogConfigID(TEST_LOG_CONFIG_ID);
    logConfigProp.setName(LogConfigProperty.ATTR_FILE_EXTENSIONS);
    logConfigProp.setValue("log,txt");
    // save
    configManager.saveObject(logConfigProp);
    // assert created
    final int lengthAfter = configManager.getLogConfigProperties(TEST_LOG_CONFIG_ID).size();
    assertEquals(lengthBefore + 1, lengthAfter);
  }


  public void test_getBuildChangeList() {
    final BuildChangeList buildChangeList = configManager.getBuildChangeList(TEST_BUILD_ID_1, 1);
    assertNotNull(buildChangeList);
    assertEquals("N", buildChangeList.getNew());
    assertEquals(TEST_BUILD_ID_1, buildChangeList.getBuildID());
    assertEquals(1, buildChangeList.getChangeListID());
  }


  public void test_getUserByNameAndEmail() {
    final User user = SecurityManager.getInstance().getUserByNameAndEmail("test_user", "imeshev@yahoo.com");
    assertNotNull(user);
  }


  public void test_saveIssueTracker() {
    final IssueTracker issueTracker = new IssueTracker();
    issueTracker.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    issueTracker.setType(IssueTracker.TYPE_JIRA_LISTENER);
    configManager.saveObject(issueTracker);
  }


  public void test_saveIssueTrackerProperty() {
    final IssueTracker issueTracker = new IssueTracker();
    issueTracker.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    issueTracker.setType(IssueTracker.TYPE_JIRA_LISTENER);
    final IssueTracker savedTracker = (IssueTracker) configManager.saveObject(issueTracker);

    final IssueTrackerProperty trackerProperty = new IssueTrackerProperty();
    trackerProperty.setTrackerID(savedTracker.getID());
    trackerProperty.setName(IssueTrackerProperty.JIRA_PROJECT);
    trackerProperty.setValue("Integration");
    configManager.saveObject(trackerProperty);
  }


  public void test_getIssuesInBuildRun() {
    // NOTE: vimeshev - 05/15/2004 - this test relays on the fact that
    // there are TWO issues in release notes table in dataset.xml. It
    // otherwise breaks.
    assertEquals(2, configManager.getBuildRunIssues(1).size());
  }


  public void test_findIssueIDByKeyAndAttributes() {
    final List attrs = new ArrayList(3);
    attrs.add(new IssueAttribute("other.attribute.1", "other_value"));
    attrs.add(new IssueAttribute("other.attribute.2", "other_value"));
    final Integer result = configManager.findIssueIDByKeyAndAttributes("301", attrs);
    assertNotNull(result);
    assertEquals(3, result.intValue());
  }


  public void test_findIssueIDByKeyAndAttributesDoesNotFindWhatItShouldNot() {
    final List attrs = new ArrayList(3);
    attrs.add(new IssueAttribute("other.attribute.1", "blah"));
    attrs.add(new IssueAttribute("other.attribute.2", "other_value"));
    final Integer result = configManager.findIssueIDByKeyAndAttributes("301", attrs);
    assertNull(result);
  }


  public void test_findIssueTrackersByProjectAndAffectedVersions() {
    final List result = configManager.findIssueTrackersByProjectAndAffectedVersions(IssueTracker.TYPE_JIRA_LISTENER, "test_project_1", "v1.0");
    assertNotNull(result);
    if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
    assertEquals("Number of issue trackers", 1, result.size());
  }


  public void test_findIssueIDByProductAndVersion() {
    final Integer result = configManager.findIssueIDByProductAndVersion(Issue.TYPE_BUGZILLA, "303", "test product", "test version");
    assertNotNull(result);
    assertEquals(new Integer(5), result);
  }


  public void test_findIssueIDByProjectAndVersion() {
    final Integer result = configManager.findIssueIDByProjectAndVersion(Issue.TYPE_JIRA, "TEST-0005", "test_project", "");
    assertNotNull(result);
    assertEquals(new Integer(7), result);
  }


  public void test_saveChangeListsAndIssues() {

    final Change change = new Change();
    change.setChangeType(Change.TYPE_ADDED);
    change.setFilePath("test/file/path");
    change.setRevision("1.0");

    final ChangeList changeList = new ChangeList();
    changeList.setCreatedAt(new Date());
    changeList.setUser("test_user");
    changeList.setDescription("test description");
    changeList.getChanges().add(change);

    final Issue issue = new Issue();
    issue.setClosed(new Date());
    issue.setDescription("test issue description");
    issue.setKey("test key nnn");
    issue.setReceived(new Date());
    issue.setTrackerType(Issue.TYPE_PERFORCE);

    final ChangeListsAndIssues changeListsAndIssues = new ChangeListsAndIssuesImpl();
    changeListsAndIssues.addChangelist(changeList);
    changeListsAndIssues.addBinding(new ChangeListIssueBinding(changeList, issue));

    final int result = configManager.saveChangeListsAndIssues(TestHelper.TEST_P4_VALID_BUILD_ID, changeListsAndIssues);
    assertTrue(result > 0);

    assertEquals(1, configManager.getIssueChangeLists(issue.getID()).size());
    assertEquals(1, configManager.getIssueChangeLists(issue.getID()).size());
  }


  public void test_getReferencingBuildIDs() {
    // can detect existing?
    final int referencingBuild = TestHelper.TEST_RECURRENT_BUILD_ID;
    final List configs = configManager.getReferencingBuildConfigs(TestHelper.TEST_P4_VALID_BUILD_ID);
    assertTrue(!configs.isEmpty());
    assertEquals(referencingBuild, ((BuildConfig) configs.get(0)).getBuildID());

    // won't detect deleted
    configManager.markActiveBuildDeleted(referencingBuild);
    final List deleted = configManager.getReferencingBuildConfigs(TestHelper.TEST_P4_VALID_BUILD_ID);
    assertEquals("There should not be deleted retrieved", 0, deleted.size());
  }


  public void test_referringIssueTrackersExist() {
    assertTrue(configManager.referringIssueTrackersExist(TestHelper.TEST_P4_VALID_BUILD_ID, IssueTracker.TYPE_PERFORCE));
  }


  public void test_issueTrackersExist() {
    assertTrue(!configManager.issueTrackersExist(TestHelper.TEST_P4_VALID_BUILD_ID, IssueTracker.TYPE_PERFORCE));
    assertTrue(configManager.issueTrackersExist(TestHelper.TEST_RECURRENT_BUILD_ID, IssueTracker.TYPE_PERFORCE));
  }


  public void test_getPreviousStepRuns() {
    final StepRun stepRun = configManager.getStepRun(2);
    final List result = configManager.getPreviousStepRuns(stepRun);
    assertEquals(1, result.size());
  }


  public void test_saveStepLog() {
    final StepLog stepLog = new StepLog();
    stepLog.setStepRunID(1);
    stepLog.setDescription("test description");
    stepLog.setPath("test path");
    stepLog.setArchiveFileName("archive/name");
    stepLog.setType(StepLog.TYPE_CUSTOM);
    stepLog.setPathType(StepLog.PATH_TYPE_TEXT_FILE); // path type is file
    stepLog.setFound((byte) 1);
    configManager.save(stepLog);
  }


  public void test_getLastCleanBuildRun() {
    final int TEST_CLEAN_BUILD_ID = 4;
    final BuildRun run = configManager.getLastCleanBuildRun(TEST_CLEAN_BUILD_ID);
    assertNotNull(run);
    assertEquals(15, run.getBuildID());
    assertEquals(7, run.getBuildRunID());
    assertEquals(BuildRun.BUILD_RESULT_SUCCESS, run.getResultID());
    assertEquals(BuildRun.RUN_COMPLETE, run.getComplete());

    // is the original correct?
    final BuildRunConfig buildRunConfig = configManager.getBuildRunConfig(run);
    assertEquals(TEST_CLEAN_BUILD_ID, buildRunConfig.getActiveBuildID());
  }


  public void test_deleteUserFromGroup() {
    SecurityManager.getInstance().deleteUserFromGroup(1, 1);
  }


  public void test_deleteBuildFromGroup() {
    SecurityManager.getInstance().deleteBuildFromGroup(1, 1);
  }


  public void test_getResultConfigs() {
    configManager.getResultConfigs(TEST_BUILD_ID_1);
  }


  public void test_getResultConfigProperties() {
    final ResultConfig resultConfig = createAndSaveResultConfig();
    configManager.getResultConfigProperties(resultConfig.getID());
  }


  /**
   * Helper method.
   */
  private ResultConfig createAndSaveResultConfig() {
    final ResultConfig resultConfig = new ResultConfig();
    resultConfig.setBuildID(TEST_BUILD_ID_1);
    resultConfig.setDescription("Description");
    resultConfig.setPath("test/path");
    resultConfig.setType(ResultConfig.RESULT_TYPE_DIR);
    configManager.saveObject(resultConfig);
    return resultConfig;
  }


  public void test_saveResultConfigProperties() {
    final ResultConfig rc = createAndSaveResultConfig();
    final ResultConfigProperty rcp = new ResultConfigProperty(rc.getID(), ResultConfigProperty.ATTR_FILE_EXTENSIONS, ".zip");
    final List list = new ArrayList(11);
    list.add(rcp);
    configManager.saveResultConfigProperties(rc.getID(), list);
  }


  public void test_getBuildRunResults() {
    final List results = configManager.getBuildRunResults(configManager.getBuildRun(1).getBuildRunID());
    assertEquals("Size of the results list", 3, results.size());
    for (Iterator i = results.iterator(); i.hasNext(); ) {
      assertTrue(i.next() instanceof StepResult);
    }
  }


  public void test_getIMUsers() {
    final List imUsers = configManager.getIMUsers(User.IM_TYPE_JABBER, UserProperty.IM_SEND_FAILURES);
    assertNotNull(imUsers);
  }


  public void test_getIMUsersEmailMap() {
    final Map imUsersMap = configManager.getIMUsersEmailMap(User.IM_TYPE_JABBER, UserProperty.IM_SEND_FAILURES);
    assertNotNull(imUsersMap);
  }


  public void test_isChangeListBelongsToBuild() {
    assertTrue(configManager.isChangeListBelongsToBuild(2, 1));
    assertTrue(!configManager.isChangeListBelongsToBuild(3, 1));
  }


  public void test_getBuildRunByNumber() {
    final int activeBuildID = 4;
    final int buildRunNumber = 2;
    final List buildRunListByNumber = configManager.getBuildRunListByNumber(activeBuildID, buildRunNumber);
    final BuildRun buildRunByNumber = (BuildRun) buildRunListByNumber.get(0);
    assertNotNull(buildRunByNumber);
  }


  public void test_getPendingChangeLists() {
    final List pendingChangeLists = configManager.getPendingChangeLists(TEST_BUILD_ID_3);
    assertEquals(2, pendingChangeLists.size());
  }


  public void test_getEffectiveBuildConfig() {
    final BuildConfig referringBuildConfig = configManager.getBuildConfiguration(TestHelper.TEST_RECURRENT_BUILD_ID);
    final BuildConfig effectiveBuildConfig = configManager.getEffectiveBuildConfig(referringBuildConfig);
    assertEquals(TestHelper.TEST_P4_VALID_BUILD_ID, effectiveBuildConfig.getBuildID());
  }


  public void test_isCircularReference() {
    // curretly point to leaf P4 build config
    final BuildConfig referringBuildConfig = configManager.getBuildConfiguration(TestHelper.TEST_RECURRENT_BUILD_ID);
    // curretly points to referringBuildConfig above
    final BuildConfig referredBuildConfig = configManager.getBuildConfiguration(TestHelper.TEST_REF_RECURRENT_BUILD_ID);
    // should return true as we are tying to repoint
    assertTrue(configManager.isCircularReference(referringBuildConfig, referredBuildConfig));
  }


  public void test_isCircularReferenceDosntFindNoCircular() {
    assertTrue(!configManager.isCircularReference(configManager.getBuildConfiguration(TestHelper.TEST_REF_RECURRENT_BUILD_ID), configManager.getBuildConfiguration(TestHelper.TEST_RECURRENT_BUILD_ID)));
    assertTrue(!configManager.isCircularReference(configManager.getBuildConfiguration(TestHelper.TEST_REF_RECURRENT_BUILD_ID), configManager.getBuildConfiguration(TestHelper.TEST_P4_VALID_BUILD_ID)));
  }


  public void test_getNewBuildRunParticipantsCount() {
    final BuildRun buildRun = configManager.getBuildRun(2);
    final int newBuildRunParticipantsCount = configManager.getNewBuildRunParticipantsCount(buildRun);
    assertEquals(1, newBuildRunParticipantsCount);
  }


  public void test_findBuildRunResults() {
    final List buildRunResults = configManager.findBuildRunResults(1, StepResult.PATH_TYPE_DIR, "test/result/other_dir");
    assertEquals(1, buildRunResults.size());
    assertTrue(buildRunResults.get(0) instanceof StepResult);
  }


  public void test_getLatestCleanChangeListID() {
    final int latestCleanChangeListID = configManager.getLatestCleanChangeListID(TEST_BUILD_ID_3);
    if (log.isDebugEnabled()) log.debug("latestCleanChangeListID: " + latestCleanChangeListID);
    assertEquals(8, latestCleanChangeListID);
  }


  /**
   * Assures that we can add values up to 4096 characters to
   * {@link SourceControlSetting}
   */
  public void test_Bug706SourceControlSettingValueAccepts4KBValues() {
    final StringBuffer value = new StringBuffer(4096);
    for (int i = 0; i < 4095; i++) value.append('-');
    final String propertyName = "name" + System.currentTimeMillis();
    configManager.saveObject(new SourceControlSetting(TEST_BUILD_ID_1, propertyName, value.toString()));
    final SourceControlSetting retrieved = configManager.getSourceControlSetting(TEST_BUILD_ID_1, propertyName);
    assertEquals(value.length(), retrieved.getPropertyValue().length());
  }


  public void test_issueChangeListExists() {
    assertTrue(configManager.issueChangeListExists(16, 11));
    assertTrue(!configManager.issueChangeListExists(9999, 999999));
  }


  public void test_findIssueIDByKey() {
    assertEquals(1, configManager.findIssueIDByKey(TEST_BUILD_ID_1, "TEST-0001").size());
    assertEquals(0, configManager.findIssueIDByKey(TEST_BUILD_ID_4, "TEST-0001").size());
  }


  public void test_getReleaseNotesReportList() {
    // is list there?
    final List releaseNotesReportList = configManager.getReleaseNotesReportList(6);
    assertEquals(1, releaseNotesReportList.size());

    // is change list there?
    final ReleaseNoteReport releaseNotesReport = (ReleaseNoteReport) releaseNotesReportList.get(0);
    assertEquals(1, releaseNotesReport.getChageLists().size());

    // is this expected change list?
    final List chageLists = releaseNotesReport.getChageLists();
    final ReleaseNoteChangeList releaseNoteChangeList = (ReleaseNoteChangeList) chageLists.get(0);
    assertEquals(15, releaseNoteChangeList.getChangeListID().intValue());
  }


  public void test_getManualRunParameters() {
    assertEquals(4, configManager.getStartParameters(StartParameterType.BUILD, configManager.getActiveBuildConfig(TestHelper.TEST_PVCS_VALID_BUILD_ID).getBuildID()).size());
  }


  public void test_getChangeLists() {
    assertEquals(2, configManager.getChangeLists(TestHelper.TEST_CVS_VALID_BUILD_ID, 1, 99).size());
  }


  public void test_getFileChangeLists() {
    assertEquals(2, configManager.getChangeLists(3, 2, 2, "//test/sourceline/alwaysvalid/src/readme.txt").size());
  }


  public void testGetChangedFiles() {
    assertEquals(2, configManager.getChangedFiles(TestHelper.TEST_CVS_VALID_BUILD_ID, 1, 99).size());
  }


  public void test_findBuildRunAttributes() {
    final List buildRunAttributes = configManager.findBuildRunAttributes(TestHelper.TEST_VALID_MKS_BUILD_ID, BuildRunAttribute.ATTR_CLEAN_CHECKOUT, "true");
    assertEquals(1, buildRunAttributes.size());
    assertTrue(buildRunAttributes.get(0) instanceof BuildRunAttribute);
  }


  public void test_getBuildRunAttributeValue() {
    assertEquals(new Integer(1), configManager.getBuildRunAttributeValue(9, BuildRunAttribute.ATTR_STARTED_USER_ID, new Integer(-1)));
    assertEquals(new Long(1), configManager.getBuildRunAttributeValue(9, BuildRunAttribute.ATTR_STARTED_USER_ID, new Long(-1)));
    assertEquals(true, configManager.getBuildRunAttributeValue(9, BuildRunAttribute.ATTR_CLEAN_CHECKOUT, false));
    assertEquals(false, configManager.getBuildRunAttributeValue(2, BuildRunAttribute.ATTR_CLEAN_CHECKOUT, false));
  }


  public void test_getBuildRunActionLogVOs() {
    final List buildRunActionLogVOs = configManager.getBuildRunActionLogVOs(1);
    assertNotNull(buildRunActionLogVOs);
  }


  public void test_getAllParallelBuildRuns() {
    assertEquals(3, configManager.getAllParallelBuildRuns(configManager.getBuildRun(TestHelper.LEADING_BUILD_RUN_ID)).size());
    assertEquals(3, configManager.getAllParallelBuildRuns(configManager.getBuildRun(TestHelper.DEPENDENT_BUILD_RUN_ID_1)).size());
    assertEquals(3, configManager.getAllParallelBuildRuns(configManager.getBuildRun(TestHelper.DEPENDENT_BUILD_RUN_ID_2)).size());
  }


  public void test_getLatestPendingChangeListID() {
    // just make sure it doesn't blow up
    configManager.getLatestPendingChangeListID(TEST_BUILD_ID_1);
  }


  public void test_getActiveBuildAttributeValue() {
    assertNotNull(configManager.getActiveBuildAttributeValue(TEST_BUILD_ID_1, ActiveBuildAttribute.STAT_CHANGE_LISTS_TO_DATE));
    assertNull(configManager.getActiveBuildAttributeValue(TEST_BUILD_ID_1, "never_existed_attr", (Integer) null));
  }


  public void test_getBuildRunFromStepRun() {
    assertNotNull(configManager.getBuildRunFromStepRun(1));
  }


  public void test_getCompletedSuccessfulBuildRunIDs() {
    assertNotNull(configManager.getCompletedSuccessfulBuildRunIDs(0, -1));
  }


  public void test_getBuildRunChangeListFromBuildRunParicipants() {
    assertEquals(36, configManager.getBuildRunChangeListFromBuildRunParicipants(12).getChangeListID());
  }


  public void test_POCFindByDate() {
    assertNotNull(ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        Query query = session.createQuery("select chl from ChangeList chl where chl.number = ? and chl.user = ? ");
        query.setString(0, "5291");
        query.setString(1, "vimeshev");
        final ChangeList chl = (ChangeList) query.uniqueResult();
        if (log.isDebugEnabled()) log.debug("chl: " + chl);
        query = session.createQuery("select chl from ChangeList chl where chl.createdAt = ? and chl.number = ? and chl.user = ? ");
        final Date deb = TestHelper.makeDate(2005, 12, 12, 2, 17, 29);
        if (log.isDebugEnabled()) log.debug("deb: " + deb);
        query.setTimestamp(0, chl.getCreatedAt());
        query.setString(1, "5291");
        query.setString(2, "vimeshev");
        return query.uniqueResult();
      }
    }));
  }


  public void test_getCompletedBuildRunsCount() {
    assertTrue(configManager.getCompletedBuildRunsCount(TEST_BUILD_ID_1) >= 0);
  }


  public void test_getCompletedSuccessfulBuildRunsCount() {
    assertTrue(configManager.getCompletedSuccessfulBuildRunsCount(TEST_BUILD_ID_1) >= 0);
  }


  public void test_getCompletedUnsuccessfulBuildRunsCount() {
    assertTrue(configManager.getCompletedUnsuccessfulBuildRunsCount(TEST_BUILD_ID_1) >= 0);
  }


  public void test_isLastEnabledBuildSequence() {
    assertTrue(configManager.isLastEnabledBuildSequence(TEST_BUILD_ID_1, BuildStepType.BUILD, "TEST"));
  }


  public void test_getExistingBuildConfig() {
    assertNotNull(configManager.getExistingBuildConfig(TEST_BUILD_ID_1));
  }


  public void test_findBuildConfigsByBuildAttributeValue() {
    assertNotNull(configManager.findBuildConfigsByBuildAttributeValue(BuildConfigAttribute.LOG_RETENTION_DAYS, "2"));
  }


  public void testFindLastCleanBuildRuns() {
    final List list = configManager.findLastCleanBuildRuns(1, 1000);
  }


  /**
   * Tests {@link ConfigurationManager#getCompletedBuildRuns(java.util.List, int)}
   */
  public void testGetCompletedBuildRuns() {
    final ArrayList iDs = new ArrayList(6);
    iDs.add(new Integer(1));
    iDs.add(new Integer(2));
    iDs.add(new Integer(3));
    iDs.add(new Integer(4));
    iDs.add(new Integer(5));
    iDs.add(new Integer(6));
    assertEquals(7, configManager.getCompletedBuildRuns(iDs, 20).size());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestConfigurationManager.class, new String[]{
            "testGetCompletedBuildRuns",
            "testFindLastCleanBuildRuns",
            "test_POCFindByDate",
            "test_getBuildRunChangeListFromBuildRunParicipants",
            "test_getBuildRunFromStepRun",
            "test_getActiveBuildAttributeValue",
            "test_getChangeListByBuildRunAndChangeListNumber",
            "test_findBuildRunAttributes",
            "test_getLastCleanBuildRun",
            "test_getCompletedBuildRuns",
            "test_getLatestCleanChangeListID",
            "test_getReferencingBuildIDs",
            "test_saveSourceControlSettings",
            "test_getPreviousStepRuns",
            "test_findIssueIDByProductAndVersion",
            "test_getLogConfigs",
            "test_saveLogConfig",
            "test_getBuildConfiguration",
            "test_getBuildConfigViaStepLog",
            "test_getBuildConfigurationDontFindUnexisting",
            "test_saveBuildConfiguration",
            "test_saveBuildRunResult",
            "test_saveStepRunResult",
            "test_saveChange",
            "test_saveBuildRunParticipant",
            "test_saveScheduleItems",
            "test_deleteScheduleItems"
    });
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
    configManager = ConfigurationManager.getInstance();
  }
}
