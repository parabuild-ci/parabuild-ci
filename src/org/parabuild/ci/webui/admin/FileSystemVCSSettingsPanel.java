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

import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.versioncontrol.FileSystemSourceControl;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.util.ArrayList;

/**
 * Settings panel for {@link FileSystemSourceControl}
 */
public final class FileSystemVCSSettingsPanel extends AbstractCommandVCSSettingsPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  public static final String CAPTION_PATH = "Path:";
  public static final String CAPTION_USER = "User:";

  /** @noinspection InstanceVariableMayNotBeInitialized*/
  private Field flUser;
  /** @noinspection InstanceVariableMayNotBeInitialized*/
  private Text flPath;


  public FileSystemVCSSettingsPanel() {
    super("File system VCS settings");
    new CommonFieldLabel(CAPTION_PATH).setAlignY(Layout.TOP);
  }


  protected void createHeaderFields() {
    this.flUser = new Field(20, 20);
    this.flPath = new Text(80, 4);
    this.flPath.setAlignY(Layout.TOP);
  }


  protected void addHeaderFields(final GridIterator gridIterator) {
    gridIterator.addPair(new CommonFieldLabel(CAPTION_USER), new RequiredFieldMarker(flUser));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PATH), new RequiredFieldMarker(flPath));
    gridIterator.addBlankLine();
    gridIterator.add(new CommonFieldLabel("Calls to lifecycle shell scripts:"), 2);
    gridIterator.addBlankLine();
  }


  protected void bindHeaderFields() {
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.FILESYSTEM_VCS_PATH, flPath);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.FILESYSTEM_VCS_USER, flUser);
  }


  protected void setHeaderMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == WebUIConstants.MODE_INHERITED) {
      setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flPath.setEditable(editable);
    flUser.setEditable(editable);
  }


  protected void validateHeader(final ArrayList errors) {
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_USER, flUser);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PATH, flPath);
  }
}
