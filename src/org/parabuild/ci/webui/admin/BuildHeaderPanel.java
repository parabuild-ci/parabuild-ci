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
package org.parabuild.ci.webui.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.object.ProjectBuild;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.versioncontrol.VersionControlSystem;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebUIConstants;
import viewtier.ui.Field;

import java.util.regex.Pattern;

/**
 */
public final class BuildHeaderPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = -4862874807618704796L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildHeaderPanel.class); // NOPMD

  private static final String CAPTION_BUILD_NAME = "Build name:";
  private static final String CAPTION_BUILD_FARM = "Build farm:";
  private static final String CAPTION_RESULTS_ACCESS = "Build results access:";
  private static final String CAPTION_SCHEDULE_TYPE = "Build type:";
  private static final String CAPTION_VCS = "Version control:";
  private static final String CAPTION_PROJECT = "Project";

  private final AccessDropDown ddAccess = new AccessDropDown();
  private final Field buildNameField = new Field(40, 20);
  private final BuilderDropDown ddBuilder = new BuilderDropDown();
  private final ScheduleTypeDropDown ddScheduleType = new ScheduleTypeDropDown();
  private final VersionControlDropDown ddSCM = new VersionControlDropDown();
  private final ProjectDropDown ddProject = new ProjectDropDown();

  //private Password buildPasswordField = new Password(15, 15);
  //private Label buildPasswordLabel = new BoldCommonLabel(NAME_BUILDER_PASSWORD);


  private int buildID = BuildConfig.UNSAVED_ID;
  private BuildConfig buildConfig = null;


  public BuildHeaderPanel() {
    super("General Settings");

    // Populate without deleted
    ddBuilder.populate(false);

    // layout
    final GridIterator gridIterator = new GridIterator(getUserPanel(), 6);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BUILD_NAME), new RequiredFieldMarker(buildNameField));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_RESULTS_ACCESS), ddAccess);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BUILD_FARM), ddBuilder);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROJECT), ddProject);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_VCS), ddSCM);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SCHEDULE_TYPE), ddScheduleType);

    setWidth(Pages.PAGE_WIDTH);
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  public void setMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      // disable
      ddSCM.setEditable(false);
      ddScheduleType.setEditable(false);
      ddProject.setEditable(true);
      //buildPasswordField.setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      // enable
      ddSCM.setEditable(true);
      ddScheduleType.setEditable(true);
      ddProject.setEditable(true);
      //buildPasswordField.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  public void setBuildConfig(final BuildConfig buildConfig) {
    this.buildConfig = buildConfig;
    this.buildID = buildConfig.getBuildID();
    // load data
    buildNameField.setValue(buildConfig.getBuildName());
    ddBuilder.clear();
    ddBuilder.populate(isBuildRunConfig(buildConfig));
    ddBuilder.setCode(buildConfig.getBuilderID());
    ddAccess.setAccessType(buildConfig.getAccess());
    ddSCM.setCode(buildConfig.getSourceControl());
    ddScheduleType.setScheduleType(buildConfig.getScheduleType());

    // load project selection into the drop-down
    final ProjectManager pm = ProjectManager.getInstance();
    final ProjectBuild projectBuild = pm.getProjectBuild(buildConfig.getActiveBuildID());
    ddProject.setCode(projectBuild.getProjectID());
  }


  public String getBuildName() {
    return buildNameField.getValue().trim();
  }


  /**
   * @return build ID associated with the panel
   */
  public int getBuildID() {
    return buildID;
  }


  public BuildConfig getUpdatedBuildConfig() {
    if (buildConfig == null) {
      buildConfig = new ActiveBuildConfig(); // new configs are always active configs
      buildConfig.setScheduleType((byte) ddScheduleType.getScheduleType());
      buildConfig.setSourceControl((byte) ddSCM.getCode());
    }
    buildConfig.setAccess((byte) ddAccess.getAccessType());
    buildConfig.setBuildName(buildNameField.getValue().trim());
    buildConfig.setBuilderID(ddBuilder.getCode());
    return buildConfig;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();

    // trim
    buildNameField.setValue(buildNameField.getValue().trim());

    // build name
    if (StringUtils.isBlank(buildNameField.getValue())) {
      showErrorMessage("Build name can not be blank.");
      return false;
    }

    // letter as a first character
    if (!StringUtils.isFirstLetter(buildNameField.getValue())) {
      showErrorMessage("Build name may start only with a letter");
      return false;
    }

    // Valid characters in the build name
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (scm.isCustomBuildNameValidation()) {
      final String regex = scm.getCustomBuildNameRegex();
      if (!Pattern.compile(regex).matcher(buildNameField.getValue()).matches()) {
        showErrorMessage("Build name \"" + buildNameField.getValue() + "\" does not match a custom regex defined in the system user interface properties: " + regex);
        return false;
      }
    } else {
      // Default build name format validation
      if (!StringUtils.isValidStrictName(buildNameField.getValue())) {
        showErrorMessage("Build name can contain only alphanumeric characters, \"-\" and \"_\".");
        return false;
      }
    }

    // check if build with such name already exist
    if (buildID == BuildConfig.UNSAVED_ID) {
      final BuildConfig found = ConfigurationManager.getInstance().findActiveBuildConfigByName(buildNameField.getValue());
      if (found != null) {
        showErrorMessage("Build with name \"" + buildNameField.getValue() + "\" already exist.");
        return false;
      }
    }

    // check if project is selected
    if (ddProject.getCode() == Project.UNSAVED_ID) {
      showErrorMessage("Please select project for this build.");
      return false;
    }

    // if scheduled build selected, make sure it is a refrence version control
    if (ddScheduleType.getScheduleType() == BuildConfig.SCHEDULE_TYPE_RECURRENT
            || ddScheduleType.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
      if (ddSCM.getCode() != VersionControlSystem.SCM_REFERENCE) {
        showErrorMessage("Please select \"" + VersionControlSystem.NAME_SCM_REFERENCE + "\" as " + CAPTION_VCS + ". Selection of a reference build will be offered on the next step.");
        return false;
      }
    }

    if (ddSCM.getCode() == VersionControlSystem.SCM_REFERENCE) {
      if (ddScheduleType.getScheduleType() != BuildConfig.SCHEDULE_TYPE_RECURRENT
              && ddScheduleType.getScheduleType() != BuildConfig.SCHEDULE_TYPE_PARALLEL) {
        showErrorMessage("Only \"" + ScheduleTypeDropDown.STRING_SCHEDULED + "\" and \"" + ScheduleTypeDropDown.STRING_PARALLEL + "\" " + CAPTION_SCHEDULE_TYPE + " can be used for the reference " + CAPTION_VCS);
        return false;
      }
    }

    // agent host/password
    if (ddBuilder.getItemCount() == 0) {
      // Build farm is undefined.
      showErrorMessage("There are no build farms configured. Please configure at least one build farm with one agent and try again.");
      return false;
    }

    if (!(getBuilderID() == 0)) {

      // NOTE: vimeshev - 2007-03-30 - we validate only
      // builds that are public because otherwise it is
      // impossible to make build private if a remote
      // agent is not accessible. See CR #1119 for
      // details.
      if (ddAccess.getAccessType() == BuildConfig.ACCESS_PUBLIC) {
        try {
          // just call systemType to make sure we can access the remote host
          final AgentEnvironment agentEnvironment = AgentManager.getInstance().getFirstLiveAgentEnvironment(getBuilderID());
          agentEnvironment.systemType();
        } catch (final Exception e) {
          // NOTE: simeshev@parabuilci.org - 2005-01-16 - we limit the
          // error length for there can be "HTML-ed" errors coming from
          // servlet level errors.
          showErrorMessage("Error accessing agent host: " + StringUtils.truncate(StringUtils.toString(e), 250));
          return false;
        }
      }
    }

    return true;
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildConfig configToSave = getUpdatedBuildConfig();
    final ProjectManager pm = ProjectManager.getInstance();
    if (buildID == BuildConfig.UNSAVED_ID) {

      // save active build config
      buildID = cm.save(configToSave);
      configToSave.setActiveBuildID(buildID); // always same for new
      cm.save(buildConfig); // save again

      // create active build
      final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
      final ActiveBuild activeBuild = new ActiveBuild();
      activeBuild.setStartupStatus(BuildStatus.INACTIVE_VALUE);
      activeBuild.setID(buildID);
      activeBuild.setSequenceNumber(scm.incrementBuildSequenceNumber());
      cm.saveNew(activeBuild);

      // create a link between a build and a project
      final ProjectBuild projectBuild = new ProjectBuild(activeBuild.getID(), ddProject.getCode());
      pm.saveProjectBuild(projectBuild);

      // attach active build to anon if allowed
      final SecurityManager sm = SecurityManager.getInstance();
      if (sm.isAnonymousAccessEnabled()) {
        sm.addBuildToAnonymousGroup(buildID);
      }
    } else {
      // save build
      buildID = cm.save(configToSave);

      // save link to the project
      final ProjectBuild projectBuild = pm.getProjectBuild(buildID);
      projectBuild.setProjectID(ddProject.getCode());
      pm.saveProjectBuild(projectBuild);
    }
    return true;
  }


  /**
   * Returns currently selected builder ID.
   *
   * @return currently selected builder ID.
   */
  public int getBuilderID() {
    return ddBuilder.getCode();
  }


  private static boolean isBuildRunConfig(final BuildConfig buildConfig) {
    if (buildConfig.getActiveBuildID() == BuildConfig.UNSAVED_ID) {
      return false;
    }
    if (buildConfig.getBuildID() == BuildConfig.UNSAVED_ID) {
      return false;
    }
    return buildConfig.getActiveBuildID() != buildConfig.getBuildID();
  }
}
