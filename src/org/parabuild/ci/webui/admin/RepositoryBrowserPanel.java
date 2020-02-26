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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.DropDownSelectedEvent;
import viewtier.ui.DropDownSelectedListener;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;

/**
 * A panel to show a configuration for a given repository browser such as Fisheye.
 */
public class RepositoryBrowserPanel extends SourceControlPanel {

  private static final long serialVersionUID = 575240664676879927L;
  private static final Log log = LogFactory.getLog(RepositoryBrowserPanel.class); // NOPMD

  public static final String CAPTION_REPOSITORY_BROWSER = "Repository browser: ";

  private final CommonFieldLabel lbRepositoryBrowser = new CommonFieldLabel(CAPTION_REPOSITORY_BROWSER);
  private final RepositoryBrowserTypeDropDown ddRepositoryBrowserTypeDropDown = new RepositoryBrowserTypeDropDown();
  private final ViewVCSettingsPanel pnlViewVCSettings;
  private final FishEyeSettingsPanel pnlFishEyeSettings;
  private final WebSVNSettingsPanel pnlWebSVNSettings;
  private final GithubSettingsPanel pnlGithubSettings;
  protected final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(false, AbstractSourceControlPanel.makePropertyHandler()); // strict map
  protected final GridIterator gridIterator;

  private int buildID = BuildConfig.UNSAVED_ID;


  public RepositoryBrowserPanel(final boolean showRoot) {
    super.showContentBorder(false);

    pnlViewVCSettings = new ViewVCSettingsPanel(showRoot);
    pnlFishEyeSettings = new FishEyeSettingsPanel(showRoot);
    pnlWebSVNSettings = new WebSVNSettingsPanel(showRoot);
    pnlGithubSettings = new GithubSettingsPanel(showRoot);

    this.gridIterator = new GridIterator(super.getUserPanel(), 1);
    gridIterator.add(new CommonFlow(lbRepositoryBrowser, ddRepositoryBrowserTypeDropDown));
    gridIterator.add(WebuiUtils.makePanelDivider());
    // NOTE: vimeshev - 2006-12-15 - here we add panels that
    // are both set to be invisible initially. Later either
    // load method or selections of the dropdown will make
    // the corresponding panel visible.
    gridIterator.add(pnlFishEyeSettings, 1);
    gridIterator.add(pnlViewVCSettings, 1);
    gridIterator.add(pnlWebSVNSettings, 1);
    gridIterator.add(pnlGithubSettings, 1);
    pnlFishEyeSettings.setVisible(false);
    pnlViewVCSettings.setVisible(false);
    pnlWebSVNSettings.setVisible(false);
    pnlGithubSettings.setVisible(false);
    pnlFishEyeSettings.setWidth(Pages.PAGE_WIDTH - 5);
    pnlViewVCSettings.setWidth(Pages.PAGE_WIDTH - 5);
    pnlWebSVNSettings.setWidth(Pages.PAGE_WIDTH - 5);
    pnlGithubSettings.setWidth(Pages.PAGE_WIDTH - 5);

    // bind browser type selection
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.REPOSITORY_BROWSER_TYPE, ddRepositoryBrowserTypeDropDown);

    // set up listener for drop down selection
    ddRepositoryBrowserTypeDropDown.addListener(new DropDownSelectedListener() {
      private static final long serialVersionUID = -109167926654968507L;


      public Tierlet.Result dropDownSelected(final DropDownSelectedEvent dropDownSelectedEvent) {
        final RepositoryBrowserTypeDropDown dropDown = (RepositoryBrowserTypeDropDown) dropDownSelectedEvent.getDropDown();
        setVisibilityOfSelectedPanel(dropDown.getCode());
        return Tierlet.Result.Continue();
      }
    });
  }


  public void setBuildID(final int buildID) {
    pnlViewVCSettings.setBuildID(buildID);
    pnlFishEyeSettings.setBuildID(buildID);
    pnlWebSVNSettings.setBuildID(buildID);
    pnlGithubSettings.setBuildID(buildID);
    this.buildID = buildID;
  }


  public void setMode(final int mode) {

    if (mode == WebUIConstants.MODE_VIEW) {

      lbRepositoryBrowser.setVisible(false);
      ddRepositoryBrowserTypeDropDown.setVisible(false);
    } else if (mode == WebUIConstants.MODE_INHERITED) {

      if (ddRepositoryBrowserTypeDropDown.getCode() == VersionControlSystem.CODE_NOT_SELECTED) {
        lbRepositoryBrowser.setVisible(false);
        ddRepositoryBrowserTypeDropDown.setVisible(false);
      } else {
        lbRepositoryBrowser.setVisible(true);
        ddRepositoryBrowserTypeDropDown.setVisible(true);
        ddRepositoryBrowserTypeDropDown.setEditable(false);
      }
    }

    pnlViewVCSettings.setMode(mode);
    pnlFishEyeSettings.setMode(mode);
    pnlWebSVNSettings.setMode(mode);
    pnlGithubSettings.setMode(mode);
  }


  public int getBuildID() {
    return buildID;
  }


  public void load(final BuildConfig buildConfig) {

    buildID = buildConfig.getBuildID();
    propertyToInputMap.setProperties(ConfigurationManager.getInstance().getSourceControlSettings(buildConfig.getBuildID()));
    pnlViewVCSettings.load(buildConfig);
    pnlFishEyeSettings.load(buildConfig);
    pnlWebSVNSettings.load(buildConfig);
    pnlGithubSettings.load(buildConfig);
    final int code = ddRepositoryBrowserTypeDropDown.getCode();
    if (log.isDebugEnabled()) {
      log.debug("code: " + code);
    }
    setVisibilityOfSelectedPanel(code);
  }


  /**
   * @return selected browser panel or null if nothing is
   *         selected.
   */
  private SourceControlPanel getSelectedBrowserPanel() {
    if (ddRepositoryBrowserTypeDropDown.getCode() == VersionControlSystem.CODE_FISHEYE) {
      return pnlFishEyeSettings;
    }
    if (ddRepositoryBrowserTypeDropDown.getCode() == VersionControlSystem.CODE_WEB_SVN) {
      return pnlWebSVNSettings;
    }
    if (ddRepositoryBrowserTypeDropDown.getCode() == VersionControlSystem.CODE_VIEWVC) {
      return pnlViewVCSettings;
    }
    if (ddRepositoryBrowserTypeDropDown.getCode() == VersionControlSystem.CODE_GITHUB) {
      return pnlGithubSettings;
    }
    return null;
  }


  public boolean save() {

    // save our settings
    final List updatedProperties = propertyToInputMap.getUpdatedProperties();
//    if (log.isDebugEnabled()) log.debug("updatedProperties: " + updatedProperties);
    ConfigurationManager.getInstance().saveSourceControlSettings(buildID, updatedProperties);

    // save selected delegate
    final SourceControlPanel pnlSelected = getSelectedBrowserPanel();
    return pnlSelected == null || pnlSelected.save();
  }


  public void setUpDefaults(final BuildConfig buildConfig) {
    pnlViewVCSettings.setUpDefaults(buildConfig);
    pnlFishEyeSettings.setUpDefaults(buildConfig);
    pnlWebSVNSettings.setUpDefaults(buildConfig);
    pnlGithubSettings.setUpDefaults(buildConfig);
  }


  public List getUpdatedSettings() {

    final List result = new ArrayList(11);
    result.addAll(propertyToInputMap.getUpdatedProperties());

    // get settings from selected delegate
    final SourceControlPanel pnlSelected = getSelectedBrowserPanel();
    if (pnlSelected != null) {
      result.addAll(pnlSelected.getUpdatedSettings());
    }
    return result;
  }


  public void setBuilderID(final int builderID) {
    pnlFishEyeSettings.setBuilderID(builderID);
    pnlViewVCSettings.setBuilderID(builderID);
    pnlWebSVNSettings.setBuilderID(builderID);
    pnlGithubSettings.setBuilderID(builderID);
  }


  public void setCustomCheckoutDir(final String value) {
    // do nothing
  }


  public String getCustomCheckoutDir() {
    return "";
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not
   * valid, a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    final SourceControlPanel pnlSelected = getSelectedBrowserPanel();
    return pnlSelected == null || pnlSelected.validate();
  }


  /**
   * Shows/hides browser panels according to their visibility.
   *
   * @param code
   */
  private void setVisibilityOfSelectedPanel(final int code) {

    if (code == VersionControlSystem.CODE_FISHEYE) {
      pnlFishEyeSettings.setVisible(true);
      pnlViewVCSettings.setVisible(false);
      pnlWebSVNSettings.setVisible(false);
      pnlGithubSettings.setVisible(false);
    } else if (code == VersionControlSystem.CODE_VIEWVC) {
      pnlFishEyeSettings.setVisible(false);
      pnlViewVCSettings.setVisible(true);
      pnlWebSVNSettings.setVisible(false);
      pnlGithubSettings.setVisible(false);
    } else if (code == VersionControlSystem.CODE_WEB_SVN) {
      pnlFishEyeSettings.setVisible(false);
      pnlViewVCSettings.setVisible(false);
      pnlWebSVNSettings.setVisible(true);
      pnlGithubSettings.setVisible(false);
    } else if (code == VersionControlSystem.CODE_GITHUB) {
      pnlFishEyeSettings.setVisible(false);
      pnlViewVCSettings.setVisible(false);
      pnlWebSVNSettings.setVisible(false);
      pnlGithubSettings.setVisible(true);
    } else {
      pnlFishEyeSettings.setVisible(false);
      pnlViewVCSettings.setVisible(false);
      pnlWebSVNSettings.setVisible(false);
      pnlGithubSettings.setVisible(false);
    }
  }
}
