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
package org.parabuild.ci.webui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.OKButton;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ShowHideFilesCommandLink;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Field;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Panel to enter build diff query parameters.
 */
final class DiffPanel extends MessagePanel {

  private static final long serialVersionUID = -7355330766753424778L; // NOPMD

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(DiffPanel.class); // NOPMD

  private static final String CAPTION_FROM_BUILD = "From build:";
  private static final String CAPTION_TO_BUILD = "To build:";
  private static final String COOKIE_SHOW_DIFF_FILES = "parabuild_show_diff_changelist_files";
  private static final String VIEW_BY_CHANGE = "View by Change";
  private static final String VIEW_BY_FILE = "View by File";

  private final Field flStartBuildNumber = new CommonField(6, 6);
  private final Field flEndBuildNumber = new CommonField(6, 6);

  private final MessagePanel pnlSearchRequest = new MessagePanel(false); // NOPMD SingularField
  private final Panel pnlChangesHolder = new Panel(); // NOPMD SingularField

  private final String searchProcessingPage;


  public DiffPanel(final String title, final String searchProcessingPage, final String returnPage,
                   final boolean cancelButtonIsVisible, final boolean startBuildNumberIsEditable) {

    super(false);
    super.showHeaderDivider(true);
    if (!StringUtils.isBlank(title)) {
      super.setTitle(title);
    }
    setWidth("100%");
    pnlChangesHolder.setWidth("100%");


    // NOTE: vimeshev - 2006-04-13 - set static field names
    // because this panel is lately to be used from stateless
    // dialog.
    flStartBuildNumber.setName("start_build_number");
    flStartBuildNumber.setEditable(startBuildNumberIsEditable);
    flEndBuildNumber.setName("end_build_number");

    // buttons
    final Button btnOK = new OKButton();
    btnOK.setName("ok_button");

    final Button btnCancel = new CancelButton();
    btnCancel.setName("cancel_button");
    btnCancel.setVisible(cancelButtonIsVisible);

    final GridIterator gi = new GridIterator(pnlSearchRequest.getUserPanel(), 2);
    gi.addPair(new CommonFieldLabel(CAPTION_FROM_BUILD), flStartBuildNumber);
    gi.addPair(new CommonFieldLabel(CAPTION_TO_BUILD), flEndBuildNumber);
    gi.moveToNextLine();
    gi.add(new CommonFlow(btnOK, new Label(" "), btnCancel), 2);

    // layout
    getUserPanel().add(pnlSearchRequest);
    getUserPanel().add(pnlChangesHolder);

    // listeners
    btnCancel.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -5673542014022161441L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        final Parameters parameters = new Parameters();
        parameters.addParameter(Pages.PARAM_BUILD_ID, getActiveBuildIDFromTierletContext());
        return Tierlet.Result.Done(returnPage, parameters);
      }
    });
    btnOK.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -6355653309943112413L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
//        if (log.isDebugEnabled()) log.debug("ok button was pressed ");
        if (StringUtils.isBlank(flStartBuildNumber.getValue())) {
          // try to get build run number from build run
          final BuildRun buildRun = getBuildRunFromTierletContext();
          if (buildRun != null) {
            flStartBuildNumber.setValue(buildRun.getBuildRunNumberAsString());
          }
        }
        if (valid()) {
          return doQuery();
        }
        return null;
      }
    });
    this.searchProcessingPage = searchProcessingPage;
  }


  /**
   * Validates form input is OK.
   *
   * @return true if input is OK.
   */
  private boolean valid() {
    final List errors = new ArrayList(3);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_FROM_BUILD, flStartBuildNumber);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_TO_BUILD, flEndBuildNumber);
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }
    return true;
  }


  /**
   * Forwards request to the {@link DiffBuildPage} with populated
   * start and end parameters.  This method expects that it is
   * called when this panel is part of a window executed with
   * parameter <code>buildid</code> set.
   */
  private Tierlet.Result doQuery() {

    final Parameters parameters = new Parameters();
    final int activeBuildIDFromTierletContext = getActiveBuildIDFromTierletContext();
    if (activeBuildIDFromTierletContext >= 0) {
      parameters.addParameter(Pages.PARAM_BUILD_ID, activeBuildIDFromTierletContext);
    }

    final int buildRunIDFromTierletContext = getBuildRunIDFromTierletContext();
    if (buildRunIDFromTierletContext > 0) {
      parameters.addParameter(Pages.PARAM_BUILD_RUN_ID, buildRunIDFromTierletContext);
    }

    parameters.addParameter(Pages.PARAM_BUILD_START_NUMBER, flStartBuildNumber.getValue());
    parameters.addParameter(Pages.PARAM_BUILD_END_NUMBER, flEndBuildNumber.getValue());
//    if (log.isDebugEnabled()) log.debug("buildRunIDFromTierletContext: " + buildRunIDFromTierletContext);
//    if (log.isDebugEnabled()) log.debug("activeBuildIDFromTierletContext: " + activeBuildIDFromTierletContext);
    return Tierlet.Result.Done(searchProcessingPage, parameters);
  }


  public void setBuildStartNumber(final Integer startBuildNumber) {
    if (startBuildNumber != null) {
      flStartBuildNumber.setValue(startBuildNumber.toString());
    }
  }


  public void setBuildEndNumber(final Integer endBuildNumber) {
    if (endBuildNumber != null) {
      flEndBuildNumber.setValue(endBuildNumber.toString());
    }
  }


  private int getActiveBuildIDFromTierletContext() {
    // try to get from parameter
    final HttpServletRequest httpServletRequest = getTierletContext().getHttpServletRequest();
    final String parameter = httpServletRequest.getParameter(Pages.PARAM_BUILD_ID);
    final int activeBuildID = StringUtils.isValidInteger(parameter) ? Integer.parseInt(parameter) : BuildConfig.UNSAVED_ID;
    if (activeBuildID >= 0) {
      return activeBuildID;
    }

    // try to get from build run id
    final BuildRun buildRun = getBuildRunFromTierletContext();
    if (buildRun != null) {
      return buildRun.getActiveBuildID();
    }
    return BuildConfig.UNSAVED_ID;
  }


  private BuildRun getBuildRunFromTierletContext() {
    final int buildRunIDFromTierletContext = getBuildRunIDFromTierletContext();
    BuildRun buildRun = null;
    if (buildRunIDFromTierletContext > 0) {
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      buildRun = cm.getBuildRun(buildRunIDFromTierletContext);
    }
    return buildRun;
  }


  private int getBuildRunIDFromTierletContext() {
    final HttpServletRequest httpServletRequest = getTierletContext().getHttpServletRequest();
    final String parameter = httpServletRequest.getParameter(Pages.PARAM_BUILD_RUN_ID);
    return StringUtils.isValidInteger(parameter) ? Integer.parseInt(parameter) : BuildRun.UNSAVED_ID;
  }


  public void display(final int paramStartBuildNumber, final int paramEndBuildNumber) {

    final int activeBuildID = getActiveBuildIDFromTierletContext();
    final int startBuildNumber = Math.min(paramStartBuildNumber, paramEndBuildNumber);
    final int endBuildNumber = Math.max(paramStartBuildNumber, paramEndBuildNumber);

    final HttpServletRequest httpServletRequest = getTierletContext().getHttpServletRequest();
    final String mode = WebuiUtils.getViewChangesModeFromParamOrCookie(httpServletRequest);
    if (Pages.PARAM_VIEW_CHANGES_MODE_VALUE_BY_FILE.equals(mode)) {

      displayByFile(httpServletRequest, activeBuildID, startBuildNumber, endBuildNumber);
    } else if (Pages.PARAM_VIEW_CHANGES_MODE_VALUE_BY_CHANGE.equals(mode)) {

      displayByChange(httpServletRequest, activeBuildID, startBuildNumber, endBuildNumber);
    } else {

      displayByChange(httpServletRequest, activeBuildID, startBuildNumber, endBuildNumber);
    }
  }


  /**
   * Display diff by change.
   *
   * @param httpServletRequest
   * @param activeBuildID
   * @param startBuildNumber
   * @param endBuildNumber
   */
  private void displayByChange(final HttpServletRequest httpServletRequest, final int activeBuildID, final int startBuildNumber, final int endBuildNumber) {

    // Clear holder
    pnlChangesHolder.clear();

    //
    // Display show/hide files link
    //
    final boolean showFiles = WebuiUtils.getShowFilesFromParamOrCookie(Pages.PARAM_SHOW_FILES, COOKIE_SHOW_DIFF_FILES, httpServletRequest);
    saveShowFilesInCookie(showFiles);
    final Properties showFilesParameters = WebuiUtils.parametersFromHttpServletRequest(httpServletRequest);
    final CommonLink lnkShowHideFiles = new ShowHideFilesCommandLink(showFiles, searchProcessingPage, showFilesParameters);
    lnkShowHideFiles.setAlignX(Layout.LEFT);
    lnkShowHideFiles.setHeight(BasePage.HEADER_DIVIDER_HEIGHT);

    //
    // Show by change/by file link
    //
    final Flow flViewControls = new Flow();
    flViewControls.add(WebuiUtils.makeBlueBulletSquareImage16x16());
    flViewControls.add(lnkShowHideFiles);
    flViewControls.add(new MenuDividerLabel());
    flViewControls.add(new CommonLabel(VIEW_BY_CHANGE, Pages.FONT_COMMON_MENU));
    flViewControls.add(new MenuDividerLabel());
    flViewControls.add(createViewModeLink(activeBuildID, VIEW_BY_FILE, Pages.PARAM_VIEW_CHANGES_MODE_VALUE_BY_FILE));
    pnlChangesHolder.add(flViewControls);

    //
    // Get change lists
    //
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final List changeLists = cm.getChangeLists(activeBuildID, startBuildNumber + 1, endBuildNumber);

    //
    // Display
    //
    final ChangeListsPanel pnlChangeLists = new ChangeListsPanel();
    pnlChangeLists.setShowFiles(showFiles);
    pnlChangeLists.showChangeLists(activeBuildID, changeLists);
    pnlChangesHolder.add(pnlChangeLists);
  }


  /**
   * Display diff by file.
   *
   * @param httpServletRequest
   * @param activeBuildID
   * @param startBuildNumber
   * @param endBuildNumber
   */
  private void displayByFile(final HttpServletRequest httpServletRequest, final int activeBuildID, final int startBuildNumber, final int endBuildNumber) {

    // Clear
    pnlChangesHolder.clear();

    //
    // Display show/hide files link
    //
    final boolean showFiles = WebuiUtils.getShowFilesFromParamOrCookie(Pages.PARAM_SHOW_FILES, COOKIE_SHOW_DIFF_FILES, httpServletRequest);
    saveShowFilesInCookie(showFiles);

    //
    // Show by change/by file link
    //
    final Flow flViewControls = new Flow();
    flViewControls.add(WebuiUtils.makeBlueBulletSquareImage16x16());
    flViewControls.add(new CommonLabel(showFiles ? ShowHideFilesCommandLink.CAPTION_HIDE_FILES : ShowHideFilesCommandLink.CAPTION_SHOW_FILES, Pages.FONT_COMMON_MENU));
    flViewControls.add(new MenuDividerLabel());
    flViewControls.add(createViewModeLink(activeBuildID, VIEW_BY_CHANGE, Pages.PARAM_VIEW_CHANGES_MODE_VALUE_BY_CHANGE));
    flViewControls.add(new MenuDividerLabel());
    flViewControls.add(new CommonLabel(VIEW_BY_FILE, Pages.FONT_COMMON_MENU));
    pnlChangesHolder.add(flViewControls);

    final SecurityManager sm = SecurityManager.getInstance();
    if (sm.userCanSeeChangeListFiles(getTierletContext())) {

      //
      // Get changed files.
      //
      final ConfigurationManager cm = ConfigurationManager.getInstance();

      // Each element in the list contains a full path for the changed file
      final List changedFiles = cm.getChangedFiles(activeBuildID, startBuildNumber + 1, endBuildNumber);

      //
      // Display
      //
      final boolean userCanSeeChangeListDescriptions = sm.userCanSeeChangeListDescriptions(getTierletContext());
      final ChangedFilesTable pnlChangedFiles = new ChangedFilesTable(activeBuildID, startBuildNumber, endBuildNumber,
              userCanSeeChangeListDescriptions);
      pnlChangedFiles.setFiles(changedFiles);
      pnlChangedFiles.populate();
      pnlChangesHolder.add(pnlChangedFiles);
    } else {

      //
      // Security prohibits seeing changed files.
      //
      pnlChangesHolder.add(new BoldCommonLabel("You are not allowed to see changed files"));
    }
  }


  private CommonCommandLink createViewModeLink(final int activeBuildID, final String linkCaption, final String mode) {

    final Properties params = new Properties();
    params.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(activeBuildID));
    params.setProperty(Pages.PARAM_BUILD_RUN_ID, Integer.toString(getBuildRunIDFromTierletContext()));
    params.setProperty(Pages.PARAM_BUILD_START_NUMBER, flStartBuildNumber.getValue());
    params.setProperty(Pages.PARAM_BUILD_END_NUMBER, flEndBuildNumber.getValue());
    params.setProperty(Pages.PARAM_VIEW_CHANGES_MODE, mode);
    return new CommonCommandLink(linkCaption, Pages.BUILD_DIFF, params);
  }


  /**
   * Saves show files in a cookie for future use.
   */
  private void saveShowFilesInCookie(final boolean showFiles) {
    getTierletContext().addCookie(new Cookie(COOKIE_SHOW_DIFF_FILES, Boolean.toString(showFiles)));
  }
}
