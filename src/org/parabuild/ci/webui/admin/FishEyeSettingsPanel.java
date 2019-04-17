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

import java.util.*;

import org.parabuild.ci.webui.common.*;
import org.parabuild.ci.object.*;
import viewtier.ui.*;

/**
 *
 */
public final class FishEyeSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 7269513984227746767L; // NOPMD

  public static final String NAME_FISHEYE_URL = "FishEye URL:";
  public static final String NAME_FISHEYE_ROOT = "FishEye root:";

  private final Field flFishEyeURL = new CommonField(200, 60);
  private final Field flFishEyeRoot = new CommonField(200, 60);


  /**
   * @param showViewVCRoot subversion requires this to be set to false.
   */
  public FishEyeSettingsPanel(final boolean showViewVCRoot) {
    super("FishEye Integration");
    gridIterator.moveToNextLine();
    gridIterator.addPair(new CommonFieldLabel(NAME_FISHEYE_URL), flFishEyeURL);
    if (showViewVCRoot) {
      gridIterator.addPair(new CommonFieldLabel(NAME_FISHEYE_ROOT), flFishEyeRoot);
    }
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.FISHEYE_URL, flFishEyeURL);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.FISHEYE_ROOT, flFishEyeRoot);

  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  protected final void doSetMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else if (mode == WebUIConstants.MODE_INHERITED) {
      setEditable(false); // disable all
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flFishEyeURL.setEditable(editable);
    flFishEyeRoot.setEditable(editable);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  protected final boolean doValidate() {
    clearMessage();
    final ArrayList errors = new ArrayList(11);

    // show error if there are any
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
