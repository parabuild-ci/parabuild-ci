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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.ResultGroup;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.admin.EditManualStartParametersTable;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.BreakLabel;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.DefaultTableCommands;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebUIConstants;
import viewtier.ui.CheckBox;
import viewtier.ui.Component;
import viewtier.ui.DropDownSelectedEvent;
import viewtier.ui.DropDownSelectedListener;
import viewtier.ui.Field;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Tierlet;

import java.util.Iterator;
import java.util.List;

/**
 * Class to hold table commands specific to the results table.
 *
 * @see AbstractFlatTable.TableCommands
 */
final class ResultsTableCommandPanel extends Panel implements AbstractFlatTable.TableCommands {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ResultsTableCommandPanel.class); // NOPMD
  private static final long serialVersionUID = 4559198266842642844L;

  private final Label lbGroupsCaption = new CommonFieldLabel("Publish selected to: "); // NOPMD
  private final CodeNameDropDown ddResultGroups = new UserResultGroupsDropDown(); // NOPMD

  // comment on publishing
  private final Label lbCommentCaption = new CommonLabel("  Comment: "); // NOPMD
  private final Field flComment = new CommonField(100, 30); // NOPMD

  // pin result on publishing
  private final Label lbPinResult = new BoldCommonLabel("Pin selected results: "); // NOPMD
  private final CheckBox flPinResult = new CheckBox(); // NOPMD

  // run commands on publishing
  private final Label lbRunPublishCommand = new BoldCommonLabel("Run publishing commands: "); // NOPMD
  private final CodeNameDropDown flRunPublishCommands = new RunPublishingCommandsDropDown(); // NOPMD
  private final EditManualStartParametersTable tblParameters = new EditManualStartParametersTable(true, WebUIConstants.MODE_EDIT);


  private final Flow flwPublishingControls = new Flow();

  // here we make use of of the fact that the content of
  // default table commands is known
  private final DefaultTableCommands defaultTableCommands;

//  /**
//   * Build configuration ID for this panel.
//   */
//  private int buildID = BuildConfig.UNSAVED_ID;


  ResultsTableCommandPanel(final ResultsTable resultsTable) {

    defaultTableCommands = new DefaultTableCommands(resultsTable); // NOPMD

    // Hide the parameters table initially
    tblParameters.setVisible(false);

    // add controls
    flwPublishingControls.add(lbGroupsCaption).add(ddResultGroups).add(lbCommentCaption).add(flComment)
            .add(new BreakLabel()).add(lbPinResult).add(flPinResult);
    if (SystemConfigurationManagerFactory.getManager().isPublishingCommandsEnabled()) {
      flwPublishingControls.add(new BreakLabel()).add(lbRunPublishCommand).add(flRunPublishCommands)
              .add(new BreakLabel()).add(tblParameters);
    }

    // align commands to the right
    defaultTableCommands.setAlignX(Layout.RIGHT);
    defaultTableCommands.setAlignY(Layout.TOP);

    // define left side with publising controls
    //noinspection ThisEscapedInObjectConstruction
    final GridIterator gi = new GridIterator(this, 2);
    gi.addPair(flwPublishingControls, defaultTableCommands);
    setWidth(Pages.PAGE_WIDTH);

    // Add selection handler for running publishing commands
    flRunPublishCommands.addListener(new DropDownSelectedListener() {
      private static final long serialVersionUID = 1293468376344732029L;


      public Tierlet.Result dropDownSelected(final DropDownSelectedEvent event) {
        final RunPublishingCommandsDropDown dropDown = (RunPublishingCommandsDropDown) event.getDropDown();
        if (dropDown.getCode() == RunPublishingCommandsDropDown.CODE_RUN_COMMANDS) {
          if (log.isDebugEnabled()) log.debug("Load and display command parameters if necessary.");
          // Load and display command parameters if necessary.
          final ConfigurationManager cm = ConfigurationManager.getInstance();
          if (log.isDebugEnabled()) log.debug("buildID: " + resultsTable.getBuildID());
          final List parameterDefinitions = cm.getStartParameters(StartParameterType.PUBLISH, resultsTable.getBuildID());
          if (log.isDebugEnabled()) log.debug("parameterDefinitions: " + parameterDefinitions);
          if (!parameterDefinitions.isEmpty()) {
            if (tblParameters.getRowCount() == 0) {
              if (log.isDebugEnabled()) log.debug("Populating command parameters");
              tblParameters.populate(parameterDefinitions);
            }
            if (log.isDebugEnabled()) log.debug("Setting parameters visible");
            tblParameters.setVisible(true);
          }
        } else {
          // No running commands selected, hide the parameters
          if (log.isDebugEnabled()) log.debug("Hiding parameters");
          tblParameters.setVisible(false);
        }

        return Tierlet.Result.Continue();
      }
    });
  }


  /**
   * @return a component contaning controls.
   */
  public Component getComponent() {
    return this;
  }


  /**
   * @param visible true if delete command should be visible.
   */
  public void setAddRowCommandVisible(final boolean visible) {
    defaultTableCommands.setAddRowCommandVisible(visible);
  }


  /**
   * @param visible true if insert command should be visible.
   */
  public void setInsertRowCommandVisible(final boolean visible) {
    // do nothing, ResultsTable doesn't have insert command
  }

  public void setMoveRowCommandsVisible(final boolean visible) {
    // do nothing, ResultsTable doesn't have insert command
  }

  /**
   * @param visible true if publising controls should be visible.
   */
  public void setPublisingControlsVisible(final boolean visible) {
    flwPublishingControls.setVisible(visible);
  }


  /**
   * @return selected result group ID or {@link
   *         ResultGroup#UNSAVED_ID} if not selected.
   */
  public int getSelectedResultGroupID() {
    return ddResultGroups.getCode();
  }


  /**
   * @return comment for the publish operation.
   */
  public String getComment() {
    return flComment.getValue();
  }


  /**
   * @return name of the selected result group.
   */
  public String getSelectedResultGroupName() {
    return ddResultGroups.getValue();
  }


  /**
   * @return true if a result requested to be pinned
   */
  public boolean getPinResult() {
    return flPinResult.isChecked();
  }


  /**
   * @return true if a result requested to run a
   *         publishing command.
   */
  public int getRunPublishCommands() {
    return flRunPublishCommands.getCode();
  }


  /**
   * @return a list of parameters for publishing commands.
   */
  public List getUpdatedParameters() {
    return tblParameters.getUpdatedParameterList();
  }


  public String toString() {
    return "ResultsTableCommandPanel{" +
            "lbGroupsCaption=" + lbGroupsCaption +
            ", ddResultGroups=" + ddResultGroups +
            ", lbCommentCaption=" + lbCommentCaption +
            ", flComment=" + flComment +
            ", lbPinResult=" + lbPinResult +
            ", flPinResult=" + flPinResult +
            ", lbRunPublishCommand=" + lbRunPublishCommand +
            ", flRunPublishCommands=" + flRunPublishCommands +
            ", parametersTable=" + tblParameters +
            ", flwPublishingControls=" + flwPublishingControls +
            ", defaultTableCommands=" + defaultTableCommands +
            '}';
  }


  /**
   * This drop down list shows resul groups available for
   * the context user.
   */
  private static final class UserResultGroupsDropDown extends CodeNameDropDown {

    private static final long serialVersionUID = -8908299689870282286L;


    UserResultGroupsDropDown() {

      // add default
      addCodeNamePair(ResultGroup.UNSAVED_ID, "Select:");

      // add groups
      final org.parabuild.ci.security.SecurityManager sm = SecurityManager.getInstance();
      final List userResultGroups = sm.getUserResultGroups(getTierletContext());
      for (final Iterator i = userResultGroups.iterator(); i.hasNext();) {
        final ResultGroup resultGroup = (ResultGroup) i.next();
        final String name = resultGroup.getName().length() > 20 ? resultGroup.getName().substring(0, 19) : resultGroup.getName();
        addCodeNamePair(resultGroup.getID(), name);
      }

      // select default
      setSelection(0);
    }
  }
}


