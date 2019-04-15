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
 * Holds GitSettings panel and panel(s) for integration with repository browsing systems.
 */
public final class GitCompoundSettingsPanel extends SourceControlPanel {

  private static final long serialVersionUID = 1028867987158176236L;
  private final GitSettingsPanel pnlGitSettings = new GitSettingsPanel();
  private final RepositoryBrowserPanel pnlRepositoryBrowser = new RepositoryBrowserPanel(true);


  public GitCompoundSettingsPanel() {
    hideTitle();
    showContentBorder(false);
    add(pnlGitSettings);
    add(WebuiUtils.makePanelDivider());
    add(pnlRepositoryBrowser);

    pnlGitSettings.setWidth(Pages.PAGE_WIDTH - 5);
    pnlRepositoryBrowser.setWidth(Pages.PAGE_WIDTH - 5);
  }


  public void setBuildID(final int buildID) {
    pnlGitSettings.setBuildID(buildID);
    pnlRepositoryBrowser.setBuildID(buildID);
  }


  public void setMode(final int mode) {
    pnlGitSettings.setMode(mode);
    pnlRepositoryBrowser.setMode(mode);
  }


  public int getBuildID() {
    return pnlGitSettings.getBuildID();
  }


  public boolean validate() {
    return pnlGitSettings.validate() && pnlRepositoryBrowser.validate();
  }


  public void load(final BuildConfig buildConfig) {
    pnlGitSettings.load(buildConfig);
    pnlRepositoryBrowser.load(buildConfig);
  }


  public boolean save() {
    return pnlGitSettings.save() && pnlRepositoryBrowser.save();
  }


  public void setUpDefaults(final BuildConfig buildConfig) {
    pnlGitSettings.setUpDefaults(buildConfig);
    pnlRepositoryBrowser.setUpDefaults(buildConfig);
  }


  public List getUpdatedSettings() {
    final List result = new ArrayList(31);
    result.addAll(pnlGitSettings.getUpdatedSettings());
    result.addAll(pnlRepositoryBrowser.getUpdatedSettings());
    return result;
  }


  public String getPathToGitExe() {
    return pnlGitSettings.getPathToGitExe();
  }


  public void setBuilderID(final int builderID) {
    pnlGitSettings.setBuilderID(builderID);
    pnlRepositoryBrowser.setBuilderID(builderID);
  }
}