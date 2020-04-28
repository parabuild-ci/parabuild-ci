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
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildService;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.ListBox;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;
import viewtier.ui.TierletContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * StopManyBuildsPanel
 *
 * @author Slava Imeshev
 * @noinspection MethodOnlyUsedFromInnerClass @since Apr 30, 2009 10:53:51 AM
 */
public final class StopGroupPanel extends MessagePanel {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(StopGroupPanel.class); // NOPMD
  private static final long serialVersionUID = -7510168143392460776L;

  private final ListBox lbSelectionList = new ListBox();
  private final ListBox lbSelectedList = new ListBox();

  private final Button btnSelect = new Button(" > ");
  private final Button btnDeselect = new Button(" < ");
  private final Button btnStop = new Button(" Stop ");
  private final CancelButton btnCancel = new CancelButton();
  private final Parameters parameters;
  private static final String ALL = "<ALL>";
  private static final String EMPTY = "                              ";


  /**
   * @param parameters
   * @noinspection ThisEscapedInObjectConstruction
   */
  public StopGroupPanel(final Parameters parameters) {
    this.parameters = parameters;

    // Enable multiselect
    lbSelectionList.enableMultiSelect(true);
    lbSelectionList.setVerticalSize(30);
    lbSelectedList.enableMultiSelect(true);
    lbSelectedList.setVerticalSize(30);

    // Layout
    final GridIterator gridIterator = new GridIterator(this, 3);
    gridIterator.add(new Label("Available:"));
    gridIterator.add(new Label(""));
    gridIterator.add(new Label("Selected:"));
    gridIterator.add(lbSelectionList);
    final Panel pnlButtons = new Panel();
    pnlButtons.setAlignY(Layout.CENTER);
    pnlButtons.add(btnSelect);
    pnlButtons.add(WebuiUtils.makePanelDivider());
    pnlButtons.add(btnDeselect);
    gridIterator.add(pnlButtons);
    gridIterator.add(lbSelectedList);
    gridIterator.add(new CommonFlow(btnStop, new Label("    "), btnCancel));

    // Add listeners
    btnStop.addListener(new StopButtonPressedListener());
    btnCancel.addListener(new CancelButtonPressedListener());
    btnSelect.addListener(new SelectButtonPressedListener());
    btnDeselect.addListener(new DeselectButtonPressedListener());


    // Populate lists
    lbSelectedList.addItem(EMPTY);
    lbSelectionList.addItem(ALL);

    final String source = getSource(parameters);
    final String buildID = getBuildID(parameters);

    final List selectedBuildIDs;
    final List selectionBuildIDs;
    if (Pages.STOP_GROUP_SOURCE_BUILD_COMMANDS.equals(source)) {
      // Prepare selected
      selectedBuildIDs = new ArrayList(11);
      if (StringUtils.isValidInteger(buildID)) {
        selectedBuildIDs.add(new Integer(buildID));
      }
      // Prepare selection
      selectionBuildIDs = new ArrayList(11);
      final List list = BuildManager.getInstance().getCurrentBuildsStatuses();
      for (int i = 0; i < list.size(); i++) {
        final BuildState buildState = (BuildState) list.get(i);
        if (buildState.isStoppable() && !Integer.toString(buildState.getActiveBuildID()).equals(buildID)) {
          selectionBuildIDs.add(new Integer(buildState.getActiveBuildID()));
        }
      }
    } else if (Pages.STOP_GROUP_SOURCE_AGENT_STATUS.equals(source)) {
      final String agentID = getAgentID(parameters);
      if (StringUtils.isValidInteger(agentID)) {
        selectedBuildIDs = BuilderConfigurationManager.getInstance().getBuildConfigIDsForAgent(Integer.parseInt(agentID));
        selectionBuildIDs = Collections.emptyList();
      } else {
        selectedBuildIDs = Collections.emptyList();
        selectionBuildIDs = Collections.emptyList();
      }
    } else {
      selectedBuildIDs = Collections.emptyList();
      selectionBuildIDs = Collections.emptyList();
    }

    addBuildsToListBox(selectedBuildIDs, lbSelectedList);
    addBuildsToListBox(selectionBuildIDs, lbSelectionList);

    // Make sure lists look as they should
    normalize();
  }


  private static String getSource(final Parameters parameters) {
    return parameters.getParameterValue(Pages.PARAM_STOP_GROUP_SOURCE);
  }


  private static void addBuildsToListBox(final List selectedBuildIDs, final ListBox listBox) {
    final BuildListService buildListService = ServiceManager.getInstance().getBuildListService();
    for (int i = 0; i < selectedBuildIDs.size(); i++) {
      final Integer buildID = (Integer) selectedBuildIDs.get(i);
      final BuildService buildService = buildListService.getBuild(buildID);
      if (buildService == null) {
        continue;
      }
      final BuildState state = buildService.getBuildState();
      if (!state.isStoppable()) {
        continue;
      }
      listBox.addItem(state.getBuildName());
    }
  }


  private static String getBuildID(final Parameters parameters) {
    return parameters.getParameterValue(Pages.PARAM_BUILD_ID);
  }


  private static String getAgentID(final Parameters parameters) {
    return parameters.getParameterValue(Pages.PARAM_AGENT_ID);
  }


  /**
   * Processes a click on the Select button.
   */
  private Tierlet.Result processSelect() {

    // Check selected
    final int[] selectedIndexes = lbSelectionList.getSelectedIndexes();

    // Copy selection to selected list
    for (int i = 0; i < selectedIndexes.length; i++) {
      final String item = lbSelectionList.getItem(selectedIndexes[i]);
      if (EMPTY.equals(item)) {
        continue;
      }
      lbSelectedList.addItem(item);
    }

    // Remove selected from the selection
    final int selectedItemCount = lbSelectedList.getItemCount();
    for (int i = 0; i < selectedItemCount; i++) {
      final String item = lbSelectedList.getItem(i);
      if (EMPTY.equals(item)) {
        continue;
      }
      lbSelectionList.removeItem(item);
    }
    if (lbSelectionList.getItemCount() == 0) {
      lbSelectionList.addItem(EMPTY);
    }

    // Make sure lists look as they should
    normalize();

    return Tierlet.Result.Continue();
  }


  /**
   * Processes a click on the Deselect button.
   */
  private Tierlet.Result processDeselect() {

    // Check selected
    final int[] selectedIndexes = lbSelectedList.getSelectedIndexes();

    // Copy selected to selection list
    for (int i = 0; i < selectedIndexes.length; i++) {
      final String item = lbSelectedList.getItem(selectedIndexes[i]);
      if (EMPTY.equals(item)) {
        continue;
      }
      lbSelectionList.addItem(item);
    }

    // Remove selected from the selected
    final int selectedItemCount = lbSelectionList.getItemCount();
    for (int i = 0; i < selectedItemCount; i++) {
      final String item = lbSelectionList.getItem(i);
      if (EMPTY.equals(item)) {
        continue;
      }
      lbSelectedList.removeItem(item);
    }

    // Make sure lists look as they should
    normalize();

    return Tierlet.Result.Continue();
  }


  /**
   * Processes the stop request.
   *
   * @return result.
   * @noinspection UnnecessaryParentheses
   */
  private Tierlet.Result processStop() {
    final int count = lbSelectedList.getItemCount();
    if (count == 0 || (count == 0 && lbSelectedList.getItem(0).equals(EMPTY))) {
      showErrorMessage("Nothing is selected. Please select a build or press \"Cancel\" button");
      return Tierlet.Result.Continue();
    }

    // Get user ID
    final SecurityManager sm = SecurityManager.getInstance();
    final TierletContext context = getTierletContext();
    int userID = User.UNSAVED_ID;
    if (context != null) {
      final User userFromRequest = sm.getUserFromRequest(context.getHttpServletRequest());
      if (userFromRequest != null) {
        userID = userFromRequest.getUserID();
      }
    }

    // Compose a list of builds to stop
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final Set result = new HashSet(11);
    for (int i = 0; i < count; i++) {
      final String item = lbSelectedList.getItem(i);
      if (ALL.equals(item)) {
        // Add all build IDs to the list
        final List list = BuildManager.getInstance().getCurrentBuildsStatuses();
        for (int j = 0; j < list.size(); j++) {
          final BuildState buildState = (BuildState) list.get(j);
          if (buildState.isStoppable()) {
            result.add(new Integer(buildState.getActiveBuildID()));
          }
        }
      } else {
        // Add build ID to the list
        final BuildConfig buildConfig = cm.findActiveBuildConfigByName(item);
        if (buildConfig != null) {
          result.add(new Integer(buildConfig.getActiveBuildID()));
        }
      }
    }

    // Process stop
    final BuildListService buildListService = ServiceManager.getInstance().getBuildListService();
    for (final Iterator iterator = result.iterator(); iterator.hasNext();) {
      final Integer buildID = (Integer) iterator.next();
      buildListService.getBuild(buildID).stopBuild(userID);
    }

    return createDoneResult();
  }


  private Tierlet.Result processCancel() {
    return createDoneResult();
  }


  /**
   * Ensures that lists look as they should.
   */
  private void normalize() {
    // Add empty if necessary
    ensureNotEmpty(lbSelectedList);
    ensureNotEmpty(lbSelectionList);

    // Sort
    lbSelectionList.sort(String.CASE_INSENSITIVE_ORDER);
    lbSelectedList.sort(String.CASE_INSENSITIVE_ORDER);
  }


  /**
   * Ensures that a list is not empty.
   */
  private static void ensureNotEmpty(final ListBox lb) {
    lb.removeItem(EMPTY);
    if (lb.isEmpty()) {
      lb.addItem(EMPTY);
    }
  }


  /**
   * Creates a Tierlet.Result that can be used to forward to the initial location.
   *
   * @return Tierlet.Result that can be used to forward to the initial location.
   */
  private Tierlet.Result createDoneResult() {
    final String source = getSource(parameters);
    if (Pages.STOP_GROUP_SOURCE_BUILD_COMMANDS.equals(source)) {
      final String buildID = getBuildID(parameters);
      if (StringUtils.isValidInteger(buildID)) {
        final Parameters properties = new Parameters();
        properties.addParameter(Pages.PARAM_BUILD_ID, buildID);
        return Tierlet.Result.Done(Pages.ADMIN_BUILD_COMMANDS_LIST, properties);
      }
    } else if (Pages.STOP_GROUP_SOURCE_AGENT_STATUS.equals(source)) {
      final String agentID = getAgentID(parameters);
      if (StringUtils.isValidInteger(agentID)) {
        final Parameters properties = new Parameters();
        properties.addParameter(Pages.PARAM_AGENT_STATUS_VIEW, Pages.AGENT_STATUS_VIEW_LIST);
        properties.addParameter(Pages.PARAM_AGENT_ID, agentID);
        return Tierlet.Result.Done(Pages.PAGE_AGENTS, properties);
      } else {
        return Tierlet.Result.Done(Pages.PAGE_AGENTS);
      }
    } else {
    }

    return Tierlet.Result.Done(Pages.PUBLIC_BUILDS);
  }


  private final class StopButtonPressedListener implements ButtonPressedListener {

    private static final long serialVersionUID = -2079336182852851594L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
      return processStop();
    }
  }

  private class SelectButtonPressedListener implements ButtonPressedListener {

    private static final long serialVersionUID = -1618344547405068171L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
      return processSelect();
    }
  }

  private class DeselectButtonPressedListener implements ButtonPressedListener {

    private static final long serialVersionUID = 6812256827748802645L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
      return processDeselect();
    }
  }


  private class CancelButtonPressedListener implements ButtonPressedListener {

    private static final long serialVersionUID = 8540358842076116331L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
      return processCancel();
    }
  }


  public String toString() {
    return "StopGroupPanel{" +
            "lbSelectionList=" + lbSelectionList +
            ", lbSelectedList=" + lbSelectedList +
            ", btnSelect=" + btnSelect +
            ", btnDeselect=" + btnDeselect +
            ", btnStop=" + btnStop +
            ", btnCancel=" + btnCancel +
            "} " + super.toString();
  }
}
