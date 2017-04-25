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
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.promotion.PromotionConfigurationManager;
import org.parabuild.ci.promotion.PromotionVO;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.util.List;

/**
 * Panel to hold promotion settings.
 */
public class PromotionSettingsPanel extends MessagePanel implements Loadable, Validatable, Saveable {

  private static final String PROMOTION_POLICY = "Promotion policy: ";
  private static final int GRID_ITERATOR_SIZE_X = 2;

  private int buildID = BuildConfig.UNSAVED_ID;

  private final CodeNameDropDown ddPolicy = new CodeNameDropDown();
  private final BuildSequenceTable tblPublishingSequence = new BuildSequenceTable(BuildStepType.PUBLISH);
  private final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(false, new BuildConfigAttributePropertyHandler());
  private final ManualStartSettingsTable tblManualStartParameters = new ManualStartSettingsTable("Publishing Parameters", StartParameterType.PUBLISH);


  public PromotionSettingsPanel() {
    super(false);
    showHeaderDivider(true);
    final GridIterator gi = new GridIterator(getUserPanel(), GRID_ITERATOR_SIZE_X);
    gi.addPair(new CommonFieldLabel(PROMOTION_POLICY), ddPolicy);

    // add publishing sequence
    gi.add(WebuiUtils.makePanelDivider(), GRID_ITERATOR_SIZE_X);
    gi.add(tblPublishingSequence, GRID_ITERATOR_SIZE_X);

    // add parameters
    gi.add(WebuiUtils.makeHorizontalDivider(20), GRID_ITERATOR_SIZE_X);
    gi.add(tblManualStartParameters, GRID_ITERATOR_SIZE_X);

    propertyToInputMap.bindPropertyNameToInput(BuildConfigAttribute.PROMOTION_POLICY_ID, ddPolicy);

    ddPolicy.addCodeNamePair(-1, "Select policy");
    ddPolicy.setCode(-1);
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load
   *                    configuration for.
   */
  public void load(final BuildConfig buildConfig) {

    // load selected policy
    final List promotionList = PromotionConfigurationManager.getInstance().getPromotionList();
    for (int i = 0; i < promotionList.size(); i++) {
      final PromotionVO vo = (PromotionVO) promotionList.get(i);
      ddPolicy.addCodeNamePair(vo.getPromotionID(), vo.getPromotionName());
    }

    // select policy if any
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    propertyToInputMap.setProperties(cm.getBuildAttributes(buildConfig.getBuildID()));

    // load steps
    tblPublishingSequence.load(buildConfig);

    // Load manual start parameters
    tblManualStartParameters.populate(cm.getStartParameters(StartParameterType.PUBLISH, buildConfig.getBuildID()));
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not
   * valid, a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    return tblPublishingSequence.validate() && tblManualStartParameters.validate();
  }


  /**
   * When called, component should save it's content. This method should
   * return <code>true</code> when content of a component is saved successfully.
   * If not, a component should dispaly a error message in it's area and return
   * <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    ConfigurationManager.getInstance().saveBuildAttributes(buildID, propertyToInputMap.getUpdatedProperties());
    tblPublishingSequence.setBuildID(buildID);
    tblManualStartParameters.setBuildID(buildID);
    return tblPublishingSequence.save() && tblManualStartParameters.save();
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    tblPublishingSequence.setBuildID(buildID);
    tblManualStartParameters.setBuildID(buildID);
  }


  public String toString() {
    return "PromotionSettingsPanel{" +
            "buildID=" + buildID +
            ", ddPolicy=" + ddPolicy +
            ", tblPublishingSequence=" + tblPublishingSequence +
            ", propertyToInputMap=" + propertyToInputMap +
            ", tblManualStartParameters=" + tblManualStartParameters +
            '}';
  }
}
