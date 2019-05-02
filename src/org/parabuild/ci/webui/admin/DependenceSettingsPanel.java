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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import viewtier.ui.CheckBox;

/**
 * Used to set what other build to launch after a successful build.
 */
public final class DependenceSettingsPanel extends MessagePanel implements Loadable, Validatable, Saveable {

  private static final String CAPTION_START_THIS_BUILD_UPON_SUCCESS = "Start build on success: ";
  private static final String CAPTION_FAIL_IF_CANNOT_BE_STARTED = "Fail if cannot be started: ";
  private static final long serialVersionUID = 696419954639548040L;

  private int buildID = BuildConfig.UNSAVED_ID;
  private final ReferenceableBuildNameDropdown flSelectedBuild = new ReferenceableBuildNameDropdown(CodeNameDropDown.ALLOW_NONEXISTING_CODES);
  private final CheckBox flFailIfCannotBeStarted = new CheckBox();
  private final PropertyToInputMap inputMap = new PropertyToInputMap(false, new BuildAttributeHandler());


  public DependenceSettingsPanel() {
    super("Dependent Build");

    // layout
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    gi.addPair(new CommonFieldLabel(CAPTION_START_THIS_BUILD_UPON_SUCCESS), flSelectedBuild);
    gi.addPair(new CommonFieldLabel(CAPTION_FAIL_IF_CANNOT_BE_STARTED), flFailIfCannotBeStarted);
    setWidth("100%");

    // data binding
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.DEPENDENT_BUILD_ID, flSelectedBuild);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.FAIL_IF_DEPENDENT_BUILD_CANNOT_BE_STARTED, flFailIfCannotBeStarted);
  }


  /**
   * Sets build ID.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    this.flSelectedBuild.excludeBuildID(buildID);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
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
    ConfigurationManager.getInstance().saveBuildAttributes(buildID, inputMap.getUpdatedProperties());
    return true;
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    inputMap.setProperties(ConfigurationManager.getInstance().getBuildAttributes(buildConfig.getBuildID()));
    flSelectedBuild.excludeBuildID(buildID);
  }
}
