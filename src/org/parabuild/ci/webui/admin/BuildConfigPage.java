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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ButtonSeparator;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.ContinueButton;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

/**
 */
public final class BuildConfigPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 459918320024016586L; // NOPMD
  private static final Log log = LogFactory.getLog(BuildConfigPage.class);

  // header part
  private final BuildHeaderPanel buildHeaderPanel = new BuildHeaderPanel();

  // details part
  private CommonButton detailsTopCancelButton = null;
  private CommonButton detailsTopSaveButton = null;
  private CommonButton btnSaveAndStartDetailsTop = null;
  private CommonButton detailsBottomCancelButton = null;
  private CommonButton detailsBottomSaveButton = null;
  private CommonButton btnSaveAndStartDetailsBottom = null;

  private BuildConfig buildConfig = null;
  private BuildConfigTabs buildConfigTabs = null;


  public BuildConfigPage() {
    super(FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
    addScriptPath("/parabuild/scripts/windows-js/prototype.js");
    addScriptPath("/parabuild/scripts/windows-js/window.js");
    addScriptPath("/parabuild/scripts/windows-js/browser-detect.js");
    addScriptPath("/parabuild/scripts/windows-js/get-object.js");
    addScriptPath("/parabuild/scripts/windows-js/parabuild-setup.js");
    addScriptPath("/parabuild/scripts/windows-js/window_ext.js");
    addStylePath("/parabuild/scripts/windows-js/themes/default.css");
    addStylePath("/parabuild/scripts/windows-js/themes/parabuild.css");
    setTitle(makeTitle("Build configuration"));
  }


  /**
   * Tierlet lifecycle method
   *
   * @param parameters
   */
  public Result executePage(final Parameters parameters) {
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.ADMIN_EDIT_BUILD, parameters);
    }

    if (!SystemConfigurationManagerFactory.getManager().isSystemConfigurationComplete()) {
      final Parameters forwardParameters = new Parameters();
      forwardParameters.addParameter(Pages.PARAM_VALIDATE_ON_LOAD, "true");
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.ADMIN_EMAIL_CONFIGURATION, forwardParameters, Pages.ADMIN_EDIT_BUILD, parameters);
    }

    // init conversion
    if (isNew()) {
      buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(parameters);
      // is user is creting a new configuration?
      if (buildConfig == null) {
        if (!super.isValidAdminUser()) {
          return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.ADMIN_EDIT_BUILD, parameters);
        }

        setPageHeader("New Build Configuration");

        // user is creating a new configuration
        setupNewHeaderLayout();
        setFocusOnFirstInput(true);
        return Result.Continue();
      } else {
        // authorise
        final BuildRights userRigths = super.getUserRigths(buildConfig.getActiveBuildID());
        if (!userRigths.isAllowedToUpdateBuild()) {
          return WebuiUtils.showNotAuthorized(this);
        }
        // user is editing existing configuration
        setTitle(makeTitle("Build configuration >> " + buildConfig.getBuildName()));
        setPageHeader("Configuration for Build " + buildConfig.getBuildName());
        setupDetailsLayout();
        loadExistingDetails();
        return Result.Continue();
      }
    }

    // NOTE: simeshev@parabuilci.org - we can get here when user
    // presses refresh. Other possibility is that as for
    // 01/10/2004 tab selection links cause execute that should
    // be ignored. In future this can go away if viewtier.ui
    // makes tab selection commands are made returning Result in
    // place
    return Result.Continue();
  }


  /**
   * Creates layout for new header
   */
  private void setupNewHeaderLayout() {

    // set new title
    setTitle("Add New Build");

    // create controls
    buildHeaderPanel.setMode(WebUIConstants.MODE_EDIT);
    final CommonButton headerContinueButton = makeHeaderContinueButton();
    final CommonButton headerCancelButton = makeHeaderCancelButton();

    // layout controls
    final Panel contentPanel = super.baseContentPanel().getUserPanel();
    contentPanel.add(buildHeaderPanel);
    contentPanel.add(WebuiUtils.makeHorizontalDivider(10));
    contentPanel.add(new CommonFlow(headerContinueButton, new Label("  "), headerCancelButton));
    contentPanel.add(WebuiUtils.makePanelDivider());
  }


  /**
   * Sets up details layout
   */
  private void setupDetailsLayout() {

    // clean up header panel
    buildHeaderPanel.clearMessage();
    buildHeaderPanel.setMode(WebUIConstants.MODE_VIEW);

    // get content panel
    final Panel contentPanel = super.baseContentPanel().getUserPanel();
    contentPanel.clear();

    // add top buttons
    detailsTopSaveButton = makeSaveDetailsButton();
    btnSaveAndStartDetailsTop = makeSaveDetailsAndStartButton();
    detailsTopCancelButton = makeCancelDetailsButton();
    final Flow flwTopButtons = new Flow().add(detailsTopSaveButton);
    flwTopButtons.setWidth("100%");
    flwTopButtons.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    flwTopButtons.setAlignX(Layout.CENTER);

    // add "Save and start" only if not parallel build
    if (!(buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL)) {
      flwTopButtons.add(new ButtonSeparator()).add(btnSaveAndStartDetailsTop);
    }
    flwTopButtons.add(new ButtonSeparator()).add(detailsTopCancelButton);
    contentPanel.add(flwTopButtons);
    contentPanel.add(WebuiUtils.makePanelDivider());

    // add header
    contentPanel.add(buildHeaderPanel);
    contentPanel.add(WebuiUtils.makePanelDivider());

    buildConfigTabs = new BuildConfigTabs(buildConfig);
    contentPanel.add(buildConfigTabs);
    contentPanel.add(WebuiUtils.makePanelDivider());

    // add bottom buttons
    detailsBottomSaveButton = makeSaveDetailsButton();
    btnSaveAndStartDetailsBottom = makeSaveDetailsAndStartButton();
    detailsBottomCancelButton = makeCancelDetailsButton();
    final Flow flwBottomButtons = new Flow().add(detailsBottomSaveButton);
    flwBottomButtons.setWidth("100%");
    flwBottomButtons.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    flwBottomButtons.setAlignX(Layout.CENTER);

    // add "Save and start" only if not parallel build
    if (!(buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL)) {
      flwBottomButtons.add(new ButtonSeparator()).add(btnSaveAndStartDetailsBottom);
    }
    flwBottomButtons.add(new ButtonSeparator()).add(detailsBottomCancelButton);
    contentPanel.add(flwBottomButtons);
  }


  /**
   * Helper method to create header's cancel button
   */
  private CancelButton makeHeaderCancelButton() {
    final CancelButton result = new CancelButton();
    result.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -1088802222270820050L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        return WebuiUtils.createBuildActionReturnResult(BuildConfigPage.this.getTierletContext()); // cancel button returns to the build list page
      }
    });
    return result;
  }


  /**
   * Helper method to create header's cancel button
   */
  private ContinueButton makeHeaderContinueButton() {
    final ContinueButton result = new ContinueButton();
    // set button actions
    result.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -3195906039582674555L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        // continue button validates input
        if (buildHeaderPanel.validate()) {
          // header is valid, rearrange layout to be new details page
          buildConfig = buildHeaderPanel.getUpdatedBuildConfig();
          setupDetailsLayout();
          return Result.Continue();
        } else {
          // just continue editing
          return Result.Continue();
        }
      }
    });
    return result;
  }


  /**
   * Helper method to create save button
   *
   * @return SaveButton
   */
  private SaveButton makeSaveDetailsButton() {
    final SaveButton saveButton = new SaveButton();
    saveButton.addListener(new DetailsActionListener());
    return saveButton;
  }


  /**
   * Helper method to create save button
   *
   * @return SaveButton
   */
  private CommonButton makeSaveDetailsAndStartButton() {
    final CommonButton button = new CommonButton(" Save and Start ");
    button.addListener(new DetailsActionListener());
    return button;
  }


  /**
   * Helper method to create cancel button
   *
   * @return CancelButton
   */
  private CancelButton makeCancelDetailsButton() {
    final CancelButton cancelButton = new CancelButton();
    cancelButton.addListener(new DetailsActionListener());
    return cancelButton;
  }


  /**
   * Load details data
   */
  private void loadExistingDetails() {
    buildHeaderPanel.setBuildConfig(buildConfig);
    buildConfigTabs.load(buildConfig);
  }


  /**
   * Saves new build
   */
  private Result saveBuild() {
    //
    // validate
    //
    final Boolean validationResult = (Boolean) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (log.isDebugEnabled()) {
          log.debug("Validating");
        }
        // validate header first

        boolean valid = buildHeaderPanel.validate();
        if (log.isDebugEnabled()) {
          log.debug("Header valid: " + valid);
        }
        if (valid) {
          // set agent host to tabs, tabs ca use it to validate
          // other config items like version control exe paths.
          final int builderID = buildHeaderPanel.getBuilderID();
          if (log.isDebugEnabled()) {
            log.debug("Setting build farm");
          }
          buildConfigTabs.setBuilderID(builderID);
          // valdate tabs
          if (log.isDebugEnabled()) {
            log.debug("validating tabs");
          }
          valid = buildConfigTabs.validate() && valid;
          if (log.isDebugEnabled()) {
            log.debug("Config tabs valid: " + valid);
          }
        }

        // let customer correct errors
        return Boolean.valueOf(valid);
      }
    });
    if (!validationResult.booleanValue()) {
      return Result.Continue();
    }

    // check if new build
    final boolean newBuild = buildHeaderPanel.getBuildID() == BuildConfig.UNSAVED_ID;

    //
    // save
    //
    final Boolean saveResult = (Boolean) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        if (log.isDebugEnabled()) {
          log.debug("Saving");
        }
        // save build header
        boolean saved = buildHeaderPanel.save();
        session.flush();
        final int buildID = buildHeaderPanel.getBuildID();

        // save build details
        buildConfigTabs.setBuildID(buildID);
        saved &= buildConfigTabs.save();

        return Boolean.valueOf(saved);
      }
    });

    // continue editing if there are errors
    if (log.isDebugEnabled()) {
      log.debug("build config saved: " + saveResult.booleanValue());
    }
    if (!saveResult.booleanValue()) {
      return Result.Continue();
    }

    // NOTE: simeshev@parabuilci.org - at this point we saved the build,
    // and have to notify BuildManager there there are changes
    final BuildManager buildManager = BuildManager.getInstance();
    buildManager.notifyConfigurationsChanged();
    buildManager.notifyConfigurationChanged(buildHeaderPanel.getBuildID());
    SecurityManager.getInstance().invalidateBuildRightSetCache(buildHeaderPanel.getBuildID());

    // activate scheduler if this was a new build
    if (newBuild) {
      buildManager.activateBuild(buildHeaderPanel.getBuildID());
    }

    if (log.isDebugEnabled()) {
      log.debug("Returning to admin build list");
    }
    return WebuiUtils.createBuildActionReturnResult(getTierletContext());
  }


  /**
   * Saves the detailed build configuration and starts it if
   * the save was successful.
   */
  private Result saveAndStartBuild() {
    final Result result = saveBuild();
    if (result.isDone() && buildConfig != null) {
      // check if there are mandatory parameters with type "BUILD"
      if (ConfigurationManager.getInstance().getRequiredStartParameterCount(buildConfig.getActiveBuildID(), StartParameterType.BUILD) > 0) {
        // there are required parameters, forward to start page
        final Parameters params = new Parameters();
        params.addParameter(Pages.PARAM_BUILD_ID, buildConfig.getActiveBuildID());
        return Result.Done(Pages.ADMIN_START_BUILD, params);
      } else {
        // no required parameters, just start
        BuildManager.getInstance().startBuild(buildConfig.getActiveBuildID(), new BuildStartRequest(getUserID()));
      }
    }
    return result;
  }


  /**
   * Handles saving details
   */
  private final class DetailsActionListener implements ButtonPressedListener {

    private static final long serialVersionUID = -1184489846298738217L;


    public Result buttonPressed(final ButtonPressedEvent event) {
      final Button button = event.getButton();
      if (detailsTopSaveButton.equals(button) || detailsBottomSaveButton.equals(button)) {
        return saveBuild();
      } else if (btnSaveAndStartDetailsTop.equals(button) || btnSaveAndStartDetailsBottom.equals(button)) {
        return saveAndStartBuild();
      } else {
        return WebuiUtils.createBuildActionReturnResult(getTierletContext());
      }
    }


    public String toString() {
      return "DetailsActionListener{}";
    }
  }


  public String toString() {
    return "BuildConfigPage{" +
            "buildHeaderPanel=" + buildHeaderPanel +
            ", detailsTopCancelButton=" + detailsTopCancelButton +
            ", detailsTopSaveButton=" + detailsTopSaveButton +
            ", btnSaveAndStartDetailsTop=" + btnSaveAndStartDetailsTop +
            ", detailsBottomCancelButton=" + detailsBottomCancelButton +
            ", detailsBottomSaveButton=" + detailsBottomSaveButton +
            ", btnSaveAndStartDetailsBottom=" + btnSaveAndStartDetailsBottom +
            ", buildConfig=" + buildConfig +
            ", buildConfigTabs=" + buildConfigTabs +
            '}';
  }
}
