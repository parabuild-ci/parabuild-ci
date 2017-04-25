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

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds SVNSettings panel and panel(s) for integration with repository browsing systems.
 */
public final class SVNCompoundSettingsPanel extends SourceControlPanel {

  private SVNSettingsPanel pnlSVNSettings = new SVNSettingsPanel();
  private RepositoryBrowserPanel pnlRepositoryBrowser = new RepositoryBrowserPanel(true);


  public SVNCompoundSettingsPanel() {
    hideTitle();
    showContentBorder(false);
    add(pnlSVNSettings);
    add(WebuiUtils.makePanelDivider());
    add(pnlRepositoryBrowser);

    pnlSVNSettings.setWidth(Pages.PAGE_WIDTH - 5);
    pnlRepositoryBrowser.setWidth(Pages.PAGE_WIDTH - 5);
  }


  public void setBuildID(final int buildID) {
    pnlSVNSettings.setBuildID(buildID);
    pnlRepositoryBrowser.setBuildID(buildID);
  }


  public void setMode(final int mode) {
    pnlSVNSettings.setMode(mode);
    pnlRepositoryBrowser.setMode(mode);
  }


  public int getBuildID() {
    return pnlSVNSettings.getBuildID();
  }


  public boolean validate() {
    return pnlSVNSettings.validate()
            && pnlRepositoryBrowser.validate()
            ;
  }


  public void load(final BuildConfig buildConfig) {
    pnlSVNSettings.load(buildConfig);
    pnlRepositoryBrowser.load(buildConfig);
  }


  public boolean save() {
    return pnlSVNSettings.save() && pnlRepositoryBrowser.save();
  }


  public void setUpDefaults(final BuildConfig buildConfig) {
    pnlSVNSettings.setUpDefaults(buildConfig);
    pnlRepositoryBrowser.setUpDefaults(buildConfig);
  }


  public List getUpdatedSettings() {
    final List result = new ArrayList(31);
    result.addAll(pnlSVNSettings.getUpdatedSettings());
    result.addAll(pnlRepositoryBrowser.getUpdatedSettings());
    return result;
  }


  public String getPathToSVNExe() {
    return pnlSVNSettings.getPathToSVNExe();
  }


  public void setBuilderID(final int builderID) {
    pnlSVNSettings.setBuilderID(builderID);
    pnlRepositoryBrowser.setBuilderID(builderID);
  }
}
