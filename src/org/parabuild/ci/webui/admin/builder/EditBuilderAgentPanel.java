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
package org.parabuild.ci.webui.admin.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.BuilderAgent;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.SaveErrorProcessor;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebUIConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel to edit display builder.
 */
final class EditBuilderAgentPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log log = LogFactory.getLog(EditBuilderAgentPanel.class);

  public static final String CAPTION_NAME = "Build farm name:";
  public static final String CAPTION_DESCRIPTION = "Description:";
  public static final String CAPTION_AGENT = "Agent:";

  public static final String FNAME_NAME = "builder-name";
  public static final String FNAME_DESCR = "builder-descr";

  // inputs
  private final CommonLabel lbBuilderNameValue = new CommonLabel();
  private final CommonLabel lbBuilderDescriptionValue = new CommonLabel();
  private final CodeNameDropDown ddAgent = new AgentDropDown();

  private int builderID = BuilderConfiguration.UNSAVED_ID;
  private int builderAgentID = BuilderAgent.UNSAVED_ID;


  /**
   * Creates message panel without title.
   */
  EditBuilderAgentPanel(final byte mode) {
    super(true); // don't show conent border
    final boolean editable = mode == WebUIConstants.MODE_EDIT;
    getUserPanel().setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    gi.addPair(new CommonFieldLabel(CAPTION_NAME), new RequiredFieldMarker(lbBuilderNameValue));
    gi.addPair(new CommonFieldLabel(CAPTION_DESCRIPTION), new RequiredFieldMarker(lbBuilderDescriptionValue));
    gi.addPair(new CommonFieldLabel(CAPTION_AGENT), new RequiredFieldMarker(ddAgent));
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    final List errors = new ArrayList(1);
    if (isNewAgent()) {
      // Check if this builder already has this agent
      if (BuilderConfigurationManager.getInstance().findBuilderAgentByAgentID(builderID, ddAgent.getCode()) != null) {
        errors.add("Build farm agent " + ddAgent.getItem(ddAgent.getSelection())
                + " has already been defined for build farm " + lbBuilderNameValue.getText());
      }
    }
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
    }
    return errors.isEmpty();
  }


  /**
   * Loads given display builderedBuilderConfiguration.
   *
   * @param builderConfiguration
   */
  public void load(final BuilderConfiguration builderConfiguration) {
    builderID = builderConfiguration.getID();
    lbBuilderNameValue.setText(builderConfiguration.getName());
    lbBuilderDescriptionValue.setText(builderConfiguration.getDescription());
  }


  public void load(final BuilderAgent builderAgent) {
    builderAgentID = builderAgent.getID();
    ddAgent.setCode(builderAgent.getAgentID());
  }


  /**
   * Saves display builder data.
   *
   * @return if saved successfuly.
   */
  public boolean save() {
    try {

      // validate
      if (!validate()) {
        return false;
      }

      if (log.isDebugEnabled()) {
        log.debug("builderID: " + builderID);
      }
      final BuilderAgent builderAgent;
      if (isNewAgent()) {
        builderAgent = new BuilderAgent();
        builderAgent.setBuilderID(builderID);
      } else {
        builderAgent = BuilderConfigurationManager.getInstance().getBuilderAgent(builderAgentID);
      }

      // Cover-ass check
      if (builderAgent == null) {
        showErrorMessage("Build farm agent not found. Please cancel editing and try again.");
        return false;
      }

      // set props
      builderAgent.setAgentID(ddAgent.getCode());

      // save display builderedBuilderConfiguration object
      BuilderConfigurationManager.getInstance().saveBuilderAgent(builderAgent);
      return true;
    } catch (final Exception e) {
      final SaveErrorProcessor exceptionProcessor = new SaveErrorProcessor();
      return exceptionProcessor.process(this, e);
    }
  }


  private boolean isNewAgent() {
    return builderAgentID == BuilderConfiguration.UNSAVED_ID;
  }


  public String toString() {
    return "EditBuilderPanel{" +
            "lbBuilderNameValue=" + lbBuilderNameValue +
            ", lbBuilderDescriptionValue=" + lbBuilderDescriptionValue +
            ", builderID=" + builderID +
            '}';
  }
}
