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

import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.webui.common.CommonCheckBox;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;

/**
 * Panel to configure a directory with HTML files as a customer
 * build log
 *
 * @see AbstractLogConfigPanel
 * @noinspection CommonFieldCanBeLocal
 */
public final class HTMLDirLogConfigPanel extends AbstractLogConfigPanel {

  private static final long serialVersionUID = -1731911756101403145L; // NOPMD

  private static final String NAME_INDEX_FILE_PATH = "Index file:";
  private static final String STRING_DEFAULT_INDEX_FILE = "index.html";
  private static final String CAPTION_NOTIFY_IF_MISSING = "Notify if index is missing:";

  private final CommonField flIndexFile = new CommonField(60, 60); // NOPMD
  private final CommonCheckBox flNotifyAboutMissingIndex = new CommonCheckBox(); // NOPMD


  /**
   * Creates message panel without title.
   */
  public HTMLDirLogConfigPanel() {
    super(false); // no conent border
    super.setLogType(LogConfig.LOG_TYPE_HTML_DIR);
    super.getGridIter().add(new CommonFieldLabel(NAME_INDEX_FILE_PATH));
    super.getGridIter().add(new RequiredFieldMarker(flIndexFile), 3);
    super.getGridIter().addPair(new CommonFieldLabel(CAPTION_NOTIFY_IF_MISSING), flNotifyAboutMissingIndex);
    super.inputMap.bindPropertyNameToInput(LogConfigProperty.ATTR_HTML_INDEX_FILE, flIndexFile);
    super.inputMap.bindPropertyNameToInput(LogConfigProperty.ATTR_NOTIFY_ABOUT_MISSING_INDEX, flNotifyAboutMissingIndex);
    // set default
    this.flIndexFile.setValue(STRING_DEFAULT_INDEX_FILE);
    this.flNotifyAboutMissingIndex.setChecked(true);
  }


  public boolean validateProperties() {
    WebuiUtils.validateFieldNotBlank(getErrors(), NAME_INDEX_FILE_PATH, flIndexFile);
    return getErrors().isEmpty();
  }


  /**
   * Sets relative path to index file.
   *
   * @param name
   */
  public void setIndexFileName(final String name) {
    flIndexFile.setValue(name);
  }
}
