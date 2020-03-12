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
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.versioncontrol.FileSystemSourceControl;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.util.ArrayList;

/**
 * Settings panel for {@link FileSystemSourceControl}
 */
public final class GenericVCSSettingsPanel extends AbstractCommandVCSSettingsPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD
  private static final Log log = LogFactory.getLog(GenericVCSSettingsPanel.class);

  private static final String CAPTION_GET_CHANGES = "Command to get changes: ";
  private static final String CAPTION_COLUM_DIVIDER = "Column divider: ";
  private static final String CAPTION_END_OF_RECORD = "End of record: ";
  private static final String CAPTION_CHANGE_DATE_FORMAT = "Change date format: ";
  private static final String CAPTION_CHANGE_WINDOW = "Change window, seconds: ";

  /** @noinspection InstanceVariableMayNotBeInitialized*/
  private CommonField flColumnDivider;
  /** @noinspection InstanceVariableMayNotBeInitialized*/
  private CommonField flEndOfRecord;
  /** @noinspection InstanceVariableMayNotBeInitialized*/
  private CommonField flChangeDateFormat;
  /** @noinspection InstanceVariableMayNotBeInitialized*/
  private CommonField flChangeWindow;
  /** @noinspection InstanceVariableMayNotBeInitialized*/
  private CommonField flGetChangesCommand;


  public GenericVCSSettingsPanel() {
    super("Generic version control system");
  }


  public GenericVCSSettingsPanel(final String title) {
    super(title);
  }


  protected final void createHeaderFields() {
    // we create them here because super is not initialized yet.
    this.flColumnDivider = new CommonField(10, 10);
    this.flEndOfRecord = new CommonField(10, 10);
    this.flChangeDateFormat = new CommonField(20, 20);
    this.flGetChangesCommand = new CommonField(200, 80);
    this.flChangeWindow = new CommonField(3, 3);
    this.flChangeWindow.setValue("60");
  }


  protected void addHeaderFields(final GridIterator gi) {
    if (log.isDebugEnabled()) log.debug("adding header");
    gi.addBlankLine();
    gi.add(new CommonFieldLabel("Calls to lifecycle shell scripts:"), 2);
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_GET_CHANGES), new RequiredFieldMarker(flGetChangesCommand));
    gi.addPair(new CommonFieldLabel(CAPTION_COLUM_DIVIDER), new RequiredFieldMarker(flColumnDivider));
    gi.addPair(new CommonFieldLabel(CAPTION_END_OF_RECORD), new RequiredFieldMarker(flEndOfRecord));
    gi.addPair(new CommonFieldLabel(CAPTION_CHANGE_DATE_FORMAT), new RequiredFieldMarker(flChangeDateFormat));
    gi.addPair(new CommonFieldLabel(CAPTION_CHANGE_WINDOW), flChangeWindow);
  }


  protected void bindHeaderFields() {
    if (log.isDebugEnabled()) log.debug("binding header");
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.GENERIC_VCS_GET_CHANGES_COMMAND, flGetChangesCommand);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.COMMAND_VCS_COLUMN_DIVIDER, flColumnDivider);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.COMMAND_VCS_END_OF_RECORD, flEndOfRecord);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.COMMAND_VCS_CHANGE_DATE_FORMAT, flChangeDateFormat);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.COMMAND_VCS_CHANGE_WINDOW, flChangeWindow);
  }


  protected void setHeaderMode(final int mode) {
    if (log.isDebugEnabled()) log.debug("mode: " + mode);
    if (mode == WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else if (mode == WebUIConstants.MODE_INHERITED) {
      setEditable(false); // diable all
      flGetChangesCommand.setEditable(true); // but enable this
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flColumnDivider.setEditable(editable);
    flEndOfRecord.setEditable(editable);
    flChangeDateFormat.setEditable(editable);
    flGetChangesCommand.setEditable(editable);
  }


  protected void validateHeader(final ArrayList errors) {
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_CHANGE_DATE_FORMAT, flChangeDateFormat);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_COLUM_DIVIDER, flColumnDivider);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_END_OF_RECORD, flEndOfRecord);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_GET_CHANGES, flGetChangesCommand);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, CAPTION_CHANGE_WINDOW, flChangeWindow);
  }
}
