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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.configuration.BuildConfigCloner;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.ServiceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Tests BuildConfigCloner
 */
public class SSTestBuildConfigurationCloner extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestBuildConfigurationCloner.class);

  public static final int TEST_BUILD_ID_TO_CLONE = TestHelper.TEST_CVS_VALID_BUILD_ID;
  public static final int TEST_REFERENCE_BUILD_ID_TO_CLONE = TestHelper.TEST_RECURRENT_BUILD_ID;

  private ErrorManager errorManager = null;
  private ConfigurationManager cm;
  protected BuildListService buildListService;
  private BuildConfigCloner cloner;
  private static final String TEST_SOME_NEW_BRANCH = "some_new_branch";


  public SSTestBuildConfigurationCloner(final String s) {
    super(s);
  }


  public void test_createActiveBuildConfig() throws Exception {
    int newBuildID = BuildConfig.UNSAVED_ID;
    BuildConfig newBuildConfig = null;
    try {
      // is new config there?
      newBuildConfig = cloner.createActiveBuildConfig(TEST_BUILD_ID_TO_CLONE);
      assertNotNull(newBuildConfig);
      assertEquals(newBuildConfig.getBuildID(), newBuildConfig.getActiveBuildID());

      // is new active build there?
      newBuildID = newBuildConfig.getBuildID();
      final ActiveBuild newActiveBuild = cm.getActiveBuild(newBuildID);
      assertNotNull(newActiveBuild);
      assertEquals(BuildStatus.INACTIVE_VALUE, newActiveBuild.getStartupStatus());

      // is all structure and values are correct?
      assertCopyIsCorrect(TEST_BUILD_ID_TO_CLONE, newBuildID);
      assertEquals(SecurityManager.getInstance().getGroupBuildAccessList(TEST_BUILD_ID_TO_CLONE).size(), SecurityManager.getInstance().getGroupBuildAccessList(newBuildID).size());
    } finally {
      // cleanup - setUp will remove all the builds but this will
      // remain in the build list.
      if (newBuildID != BuildConfig.UNSAVED_ID) {
        buildListService.removeBuild(newBuildID);
      }
    }
  }


  public void test_createBuildRunConfig() throws Exception {
    int newBuildID = BuildConfig.UNSAVED_ID;
    // is new config there?
    final BuildConfig newBuildConfig = cloner.createBuildRunConfig(TEST_BUILD_ID_TO_CLONE, null);
    assertNotNull(newBuildConfig);
    assertEquals(cm.getBuildConfiguration(TEST_BUILD_ID_TO_CLONE).getBuildName(), newBuildConfig.getBuildName());

    // is new active build NOT there (new build run should not have a NEW one)?
    newBuildID = newBuildConfig.getBuildID();
    final ActiveBuild newActiveBuild = cm.getActiveBuild(newBuildID);
    assertTrue(newActiveBuild == null);
    assertEquals(TEST_BUILD_ID_TO_CLONE, newBuildConfig.getActiveBuildID());

    // is all structure and values are correct?
    assertCopyIsCorrect(TEST_BUILD_ID_TO_CLONE, newBuildID);
    assertEquals(0, SecurityManager.getInstance().getGroupBuildAccessList(newBuildID).size());
  }


  public void test_createBuildRunConfigTakesInAccountOwerwrites() throws Exception {
    // prepare
    final List overwriteList = new ArrayList(11);
    overwriteList.add(new SourceControlSettingVO(VCSAttribute.CVS_BRANCH_NAME, TEST_SOME_NEW_BRANCH));
    // run
    final BuildConfigCloner clonerWithOverwrites = new BuildConfigCloner(overwriteList);
    final BuildConfig newBuildConfig = clonerWithOverwrites.createBuildRunConfig(TEST_BUILD_ID_TO_CLONE, null);
    // assert
    final SourceControlSetting sourceControlSetting = cm.getSourceControlSetting(newBuildConfig.getBuildID(), VCSAttribute.CVS_BRANCH_NAME);
    assertEquals(TEST_SOME_NEW_BRANCH, sourceControlSetting.getPropertyValue());
  }


  /**
   * Asserts that large peforce settings are handled correctly.
   *
   * @throws Exception
   */
  public void test_createBuildRunConfig_Bug777() throws Exception {
    // set big p4 setting
    final int testP4ValidBuildId = TestHelper.TEST_P4_VALID_BUILD_ID;
    final Map effectiveSourceControlSettingsAsMap = cm.getEffectiveSourceControlSettingsAsMap(testP4ValidBuildId);
    final SourceControlSetting scs = (SourceControlSetting) effectiveSourceControlSettingsAsMap.get(VCSAttribute.P4_DEPOT_PATH);
    final StringBuffer sb = new StringBuffer(10000);
    for (int i = 0; i < 1000; i++) sb.append("0123456789");
    scs.setPropertyValue(sb.toString());
    cm.saveSourceControlSettings(testP4ValidBuildId, new ArrayList(effectiveSourceControlSettingsAsMap.values()));

    // clone
    final BuildConfig newBuildConfig = cloner.createBuildRunConfig(testP4ValidBuildId, null);

    // is all structure are correct?
    assertCopyIsCorrect(testP4ValidBuildId, newBuildConfig.getBuildID());

    // assert got saved correctly
    final Map newEffectiveSourceControlSettingsAsMap = cm.getEffectiveSourceControlSettingsAsMap(newBuildConfig.getBuildID());
    final SourceControlSetting newScs = (SourceControlSetting) newEffectiveSourceControlSettingsAsMap.get(VCSAttribute.P4_DEPOT_PATH);
    assertEquals(sb.toString(), newScs.getPropertyValue());
  }


  /**
   * Tests that cloning of a "reference" build (a build refeing
   * othe build as a source of VCS settings, normally scheduled
   * build) does not break because referenced build VCS setting
   * are copied instead of shallow SourceControlSettings for the
   * build being copied.
   */
  public void test_bug518() {
    BuildConfig clonedConfig = null;
    int clonedBuildID = BuildConfig.UNSAVED_ID;
    try {
      clonedConfig = cloner.createActiveBuildConfig(TEST_REFERENCE_BUILD_ID_TO_CLONE);
      clonedBuildID = clonedConfig.getBuildID();
      assertTrue(clonedBuildID != BuildConfig.UNSAVED_ID);
      assertNotNull(buildListService.getBuild(clonedBuildID));
      assertEquals(0, errorManager.errorCount());
    } finally {
      // cleanup
      if (clonedBuildID != BuildConfig.UNSAVED_ID) {
        buildListService.removeBuild(clonedBuildID);
      }
    }
  }


  public void test_createReferenceBuildRunConfig() throws Exception {
    final BuildConfig newBuildConfig = cloner.createBuildRunConfig(TEST_REFERENCE_BUILD_ID_TO_CLONE, null);
    final SourceControlSetting sourceControlSetting = cm.getSourceControlSetting(newBuildConfig.getBuildID(), VCSAttribute.REFERENCE_BUILD_ID);
    assertNotNull(sourceControlSetting);
    final int backingCleanBuildRunConfigID = sourceControlSetting.getPropertyValueAsInt();
    // get current
    final int activeBackingBuildConfigID = cm.getSourceControlSetting(TEST_REFERENCE_BUILD_ID_TO_CLONE,
            VCSAttribute.REFERENCE_BUILD_ID).getPropertyValueAsInt();
    // assert
    assertTrue(backingCleanBuildRunConfigID != activeBackingBuildConfigID);

    // check if build config belongs to a clean build run
    final int activeIDFromBuildID = cm.getActiveIDFromBuildID(backingCleanBuildRunConfigID);
    final BuildRun lastCleanBuildRun = cm.getLastCleanBuildRun(activeIDFromBuildID);
    assertEquals(lastCleanBuildRun.getBuildID(), backingCleanBuildRunConfigID);
  }


  public void test_createActiveReferenceBuildConfig() throws Exception {
    int newBuildID = BuildConfig.UNSAVED_ID;
    BuildConfig newBuildConfig = null;
    try {
      // is new config there?
      newBuildConfig = cloner.createActiveBuildConfig(TEST_REFERENCE_BUILD_ID_TO_CLONE);
      newBuildID = newBuildConfig.getBuildID();
      final int backingActiveBuildConfigID = cm.getSourceControlSetting(newBuildID,
              VCSAttribute.REFERENCE_BUILD_ID).getPropertyValueAsInt();
      assertNotNull(cm.getActiveBuildConfig(backingActiveBuildConfigID));
    } finally {
      // cleanup - setUp will remove all the builds but this will
      // remain in the build list.
      if (newBuildID != BuildConfig.UNSAVED_ID) {
        buildListService.removeBuild(newBuildID);
      }
    }
  }


  public void test_createActiveBuildClearCaseVWSStorageIsNotCopied() throws Exception {
    BuildConfig newBuildConfig = null;
    try {
      final int testClearcaseBuildId = TestHelper.TEST_CLEARCASE_VALID_BUILD_ID;
      cm.save(new SourceControlSetting(testClearcaseBuildId, VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE, VCSAttribute.CLEARCASE_STORAGE_CODE_VWS));
      cm.save(new SourceControlSetting(testClearcaseBuildId, VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION, "test_view_storage_path"));
      newBuildConfig = cloner.createActiveBuildConfig(testClearcaseBuildId);
      assertNull(cm.getSourceControlSetting(newBuildConfig.getActiveBuildID(), VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE));
      assertNull(cm.getSourceControlSetting(newBuildConfig.getActiveBuildID(), VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION));
    } finally {
      if (newBuildConfig != null) {
        buildListService.removeBuild(newBuildConfig.getBuildID());
      }
    }
  }


  public void test_createActiveBuildClearCaseStglocStorageIsCopied() throws Exception {
    BuildConfig newBuildConfig = null;
    try {
      final int testClearcaseBuildId = TestHelper.TEST_CLEARCASE_VALID_BUILD_ID;
      cm.save(new SourceControlSetting(testClearcaseBuildId, VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE, VCSAttribute.CLEARCASE_STORAGE_CODE_STGLOC));
      cm.save(new SourceControlSetting(testClearcaseBuildId, VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION, "test_view_storage_name"));
      newBuildConfig = cloner.createActiveBuildConfig(testClearcaseBuildId);
      assertNotNull(cm.getSourceControlSetting(newBuildConfig.getActiveBuildID(), VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE));
      assertNotNull(cm.getSourceControlSetting(newBuildConfig.getActiveBuildID(), VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION));
    } finally {
      if (newBuildConfig != null) {
        buildListService.removeBuild(newBuildConfig.getBuildID());
      }
    }
  }


  public void test_createBuildRunConfigClearCaseVWSStorageIsNotCopied() throws Exception {
    BuildConfig newBuildConfig = null;
    final int testClearcaseBuildId = TestHelper.TEST_CLEARCASE_VALID_BUILD_ID;
    cm.save(new SourceControlSetting(testClearcaseBuildId, VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE, VCSAttribute.CLEARCASE_STORAGE_CODE_VWS));
    cm.save(new SourceControlSetting(testClearcaseBuildId, VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION, "test_view_storage_path"));

    newBuildConfig = cloner.createBuildRunConfig(testClearcaseBuildId, null);

    assertNotNull(cm.getSourceControlSetting(newBuildConfig.getActiveBuildID(), VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE));
    assertNotNull(cm.getSourceControlSetting(newBuildConfig.getActiveBuildID(), VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION));
  }


  public void test_createBuildRunConfigClearCaseStglocStorageIsCopied() throws Exception {
    BuildConfig newBuildConfig = null;
    final int testClearcaseBuildId = TestHelper.TEST_CLEARCASE_VALID_BUILD_ID;
    cm.save(new SourceControlSetting(testClearcaseBuildId, VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE, VCSAttribute.CLEARCASE_STORAGE_CODE_STGLOC));
    cm.save(new SourceControlSetting(testClearcaseBuildId, VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION, "test_view_storage_name"));

    newBuildConfig = cloner.createBuildRunConfig(testClearcaseBuildId, null);

    assertNotNull(cm.getSourceControlSetting(newBuildConfig.getActiveBuildID(), VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION_CODE));
    assertNotNull(cm.getSourceControlSetting(newBuildConfig.getActiveBuildID(), VCSAttribute.CLEARCASE_VIEW_STORAGE_LOCATION));
  }


  /**
   * Helper bulk assert method.
   *
   * @param sourceBuildID
   * @param newBuildID
   */
  private void assertCopyIsCorrect(final int sourceBuildID, final int newBuildID) {
    assertTrue(newBuildID != sourceBuildID);
    assertEquals(cm.getBuildAttributes(sourceBuildID).size() + 1, cm.getBuildAttributes(newBuildID).size()); // +1 accounts for added sources build config ID
    assertEquals(cm.getAllBuildSequences(sourceBuildID, BuildStepType.BUILD).size(), cm.getAllBuildSequences(newBuildID, BuildStepType.BUILD).size());
    assertEquals(cm.getIssueTrackers(sourceBuildID).size(), cm.getIssueTrackers(newBuildID).size());
    assertEquals(cm.getLabelSettings(sourceBuildID).size(), cm.getLabelSettings(newBuildID).size());
    assertEquals(cm.getLogConfigs(sourceBuildID).size(), cm.getLogConfigs(newBuildID).size());
    assertEquals(cm.getVCSUserToEmailMaps(sourceBuildID).size(), cm.getVCSUserToEmailMaps(newBuildID).size());
    assertEquals(cm.getScheduleItems(sourceBuildID).size(), cm.getScheduleItems(newBuildID).size());
    assertEquals(cm.getScheduleSettings(sourceBuildID).size(), cm.getScheduleSettings(newBuildID).size());
    assertEquals(cm.getEffectiveSourceControlSettings(sourceBuildID).size(), cm.getEffectiveSourceControlSettings(newBuildID).size());
    assertEquals(cm.getWatchers(sourceBuildID).size(), cm.getWatchers(newBuildID).size());

    // check nested issue tracker properties
    int oldCounter = 0;
    for (Iterator i = cm.getIssueTrackers(sourceBuildID).iterator(); i.hasNext();) {
      final List issueTrackerProperties = cm.getIssueTrackerProperties(((IssueTracker) i.next()).getID());
      for (Iterator j = issueTrackerProperties.iterator(); j.hasNext();) {
        j.next();
        oldCounter++;
      }
    }
    int newCounter = 0;
    for (Iterator i = cm.getIssueTrackers(newBuildID).iterator(); i.hasNext();) {
      final List issueTrackerProperties = cm.getIssueTrackerProperties(((IssueTracker) i.next()).getID());
      for (Iterator j = issueTrackerProperties.iterator(); j.hasNext();) {
        j.next();
        newCounter++;
      }
    }
    assertEquals("Total number of issue trackers properties", oldCounter, newCounter);

    // check nested log config properties
    oldCounter = 0;
    for (Iterator i = cm.getLogConfigs(sourceBuildID).iterator(); i.hasNext();) {
      final List LogConfigProperties = cm.getLogConfigProperties(((LogConfig) i.next()).getID());
      for (Iterator j = LogConfigProperties.iterator(); j.hasNext();) {
        j.next();
        oldCounter++;
      }
    }
    newCounter = 0;
    for (Iterator i = cm.getLogConfigs(newBuildID).iterator(); i.hasNext();) {
      final List LogConfigProperties = cm.getLogConfigProperties(((LogConfig) i.next()).getID());
      for (Iterator j = LogConfigProperties.iterator(); j.hasNext();) {
        j.next();
        newCounter++;
      }
    }
    assertEquals("Total number of log config properties", oldCounter, newCounter);
  }


  protected void setUp() throws Exception {
    super.setUp();
    System.setProperty("parabuild.print.stacktrace", "true");
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    buildListService = ServiceManager.getInstance().getBuildListService();
    cm = ConfigurationManager.getInstance();
    cloner = new BuildConfigCloner();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestBuildConfigurationCloner.class, new String[]{
            "test_createActiveBuildConfig",
            "test_createBuildRunConfig",
            "test_bug518",
            "test_createReferenceBuildRunConfig",
            "test_createActiveReferenceBuildConfig"
    });
  }
}
