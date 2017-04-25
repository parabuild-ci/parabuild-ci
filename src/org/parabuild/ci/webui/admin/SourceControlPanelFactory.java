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

import org.parabuild.ci.configuration.UnexpectedErrorException;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.admin.accurev.AccurevSettingsPanel;
import org.parabuild.ci.webui.admin.mercurial.MercurialSettingsPanel;

/**
 *
 */
public final class SourceControlPanelFactory {


  private SourceControlPanelFactory() {
  }


  /**
   * @noinspection IfStatementWithTooManyBranches
   */
  public static SourceControlPanel getPanel(final BuildConfig buildConfig) {
    final SourceControlPanel result;
    final byte sourceControl = buildConfig.getSourceControl();
    if (sourceControl == BuildConfig.SCM_CVS) {
      result = new CVSCompoundSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_BAZAAR) {
      result = new BazaarSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_MERCURIAL) {
      result = new MercurialSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_PERFORCE) {
      result = new P4SettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_VSS) {
      result = new VSSSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_SVN) {
      result = new SVNCompoundSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_SURROUND) {
      result = new SurroundSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_CLEARCASE) {
      result = new ClearCaseSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_ACCUREV) {
      result = new AccurevSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_STARTEAM) {
      result = new StarTeamSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_VAULT) {
      result = new VaultSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_PVCS) {
      result = new PVCSSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_MKS) {
      result = new MKSSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_FILESYSTEM) {
      result = new FileSystemVCSSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_GENERIC) {
      result = new GenericVCSSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_GIT) {
      result = new GitSettingsPanel();
    } else if (sourceControl == BuildConfig.SCM_REFERENCE) {
      if (buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
        result = new ParallelSourceControlPanel();
      } else {
        result = new ReferenceSourceControlPanel();
      }
    } else if (sourceControl == BuildConfig.SCM_SYNERGY) {
      result = new SynergySettingsPanel();
    } else {
      throw new UnexpectedErrorException("Unknown version control type");
    }
    result.setBuilderID(buildConfig.getBuilderID());
    result.setUpDefaults(buildConfig);
    return result;
  }
}
