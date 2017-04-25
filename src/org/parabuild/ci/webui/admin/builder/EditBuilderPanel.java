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
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.SaveErrorProcessor;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel to edit display builder.
 */
final class EditBuilderPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log LOG = LogFactory.getLog(EditBuilderPanel.class); // NOPMD

  public static final String CAPTION_NAME = "Build farm name:";
  public static final String CAPTION_DESCRIPTION = "Description:";

  public static final String FNAME_NAME = "builder-name";
  public static final String FNAME_DESCR = "builder-descr";

  // inputs
  private final CommonField flName = new CommonField(FNAME_NAME, 50, 50);
  private final CommonField flDescr = new CommonField(FNAME_DESCR, 300, 50);

  private int builderID = BuilderConfiguration.UNSAVED_ID;


  /**
   * Creates message panel without title.
   */
  EditBuilderPanel(final byte mode) {
    super(true);
    showHeaderDivider(true);
    final boolean editable = mode == WebUIConstants.MODE_EDIT;
    getUserPanel().setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    gi.addPair(new CommonFieldLabel(CAPTION_NAME, Pages.SECTION_HEADER_COLOR), new RequiredFieldMarker(flName));
    gi.addPair(new CommonFieldLabel(CAPTION_DESCRIPTION), new RequiredFieldMarker(flDescr));

    // Set editability
    flName.setEditable(editable);
    flDescr.setEditable(editable);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    // general validation
    final List errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_NAME, flName);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescr);

    // check if new builder with this name already exists
    if (errors.isEmpty()) {
      if (builderID == BuilderConfiguration.UNSAVED_ID
              && BuilderConfigurationManager.getInstance().findBuilderByName(flName.getValue()) != null) {
        errors.add("Build farm \"" + flName.getValue() + "\" already exists.");
      }
    }

    // validation failed, show errors
    if (!errors.isEmpty()) {
      super.showErrorMessage(errors);
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
    flName.setValue(builderConfiguration.getName());
    flDescr.setValue(builderConfiguration.getDescription());
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

      // get display builderedBuilderConfiguration object
      if (LOG.isDebugEnabled()) {
        LOG.debug("builderID: " + builderID);
      }
      final BuilderConfiguration builderConfiguration;
      if (builderID == BuilderConfiguration.UNSAVED_ID) {
        builderConfiguration = new BuilderConfiguration();
      } else {
        builderConfiguration = BuilderConfigurationManager.getInstance().getBuilder(builderID);
      }

      // cover-ass check - if the builderedBuilderConfiguration is there
      if (builderConfiguration == null) {
        showErrorMessage("Build farm not found. Please cancel editing and try again.");
        return false;
      }

      // set props
      builderConfiguration.setDescription(flDescr.getValue().trim());
      builderConfiguration.setName(flName.getValue().trim());

      // save display builderedBuilderConfiguration object
      BuilderConfigurationManager.getInstance().saveBuilder(builderConfiguration);
      if (LOG.isDebugEnabled()) {
        LOG.debug("builderConfiguration: " + builderConfiguration);
      }
      return true;
    } catch (Exception e) {
      final SaveErrorProcessor exceptionProcessor = new SaveErrorProcessor();
      return exceptionProcessor.process(this, e);
    }
  }


  public String toString() {
    return "EditBuilderPanel{" +
            "flName=" + flName +
            ", flDescr=" + flDescr +
            ", builderID=" + builderID +
            '}';
  }
}
