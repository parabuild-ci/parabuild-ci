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
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.versioncontrol.VersionControlSystem;
import org.parabuild.ci.webui.admin.mercurial.MercurialManualScheduleStartParametersPanel;

/**
 * Creates instances of {@link
 * ManualScheduleStartParametersPanel} according to the build
 * configuration.
 *
 */
final class ManualScheduleStartParametersPanelFactory {

  private static final String P4_CLIENT_VIEW_SOURCE = SourceControlSetting.P4_CLIENT_VIEW_SOURCE;
  private static final byte P4_CLIENT_VIEW_SOURCE_VALUE_CLIENT_NAME = SourceControlSetting.P4_CLIENT_VIEW_SOURCE_VALUE_CLIENT_NAME;
  private static final byte P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH = SourceControlSetting.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH;


  /**
   * Factory class constructor.
   */
  private ManualScheduleStartParametersPanelFactory() {
  }


  public static ManualScheduleStartParametersPanel makePanel(final int buildID) {

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildConfig buildConfiguration = cm.getBuildConfiguration(buildID);

    if (buildConfiguration.getScheduleType() == BuildConfig.SCHEDULE_TYPE_MANUAL) {
      final byte sourceControl = buildConfiguration.getSourceControl();
      if (sourceControl == VersionControlSystem.SCM_SVN) {

        return new SVNManualScheduleStartParametersPanel();

      } else if (sourceControl == VersionControlSystem.SCM_CVS) {

        return new CVSManualScheduleStartParametersPanel();

      } else if (sourceControl == VersionControlSystem.SCM_PERFORCE) {

        // Check if this build is configured to use Perforce client name as a view source
        final boolean showParameters = showPerforceParameters(buildID);
        final boolean showDepotPathOverride = showPerforceView(buildID) && showParameters;
        return new P4ManualScheduleStartParametersPanel(showDepotPathOverride, showParameters);

      } else if (sourceControl == VersionControlSystem.SCM_BAZAAR) {

        final boolean showParameters = showBazaarParameters(buildID);
        return new BazaarManualScheduleStartParametersPanel(false, showParameters);

      } else if (sourceControl == VersionControlSystem.SCM_MERCURIAL) {

        final boolean showParameters = showMercurialParameters(buildID);
        return new MercurialManualScheduleStartParametersPanel(false, showParameters);

      } else {
        return makeInvisibleEmptyPanel();
      }
    } else {
      return makeInvisibleEmptyPanel();
    }
  }


  private static boolean showPerforceParameters(final int buildID) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    return cm.getBuildAttributeValue(buildID,
            BuildConfigAttribute.SHOW_PERFORCE_PARAMETERS, BuildConfigAttribute.OPTION_UNCHECKED)
            .equals(BuildConfigAttribute.OPTION_CHECKED);
  }


  private static boolean showPerforceView(final int buildID) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final SourceControlSetting setting = cm.getSourceControlSetting(buildID, P4_CLIENT_VIEW_SOURCE);
    return !(setting != null && (setting.getPropertyValueAsInt() == P4_CLIENT_VIEW_SOURCE_VALUE_CLIENT_NAME
            || setting.getPropertyValueAsInt() == P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH));
  }


  private static boolean showBazaarParameters(final int buildID) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    return cm.getBuildAttributeValue(buildID,
            BuildConfigAttribute.SHOW_BAZAAR_PARAMETERS, BuildConfigAttribute.OPTION_UNCHECKED)
            .equals(BuildConfigAttribute.OPTION_CHECKED);
  }


  private static boolean showMercurialParameters(final int buildID) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    return cm.getBuildAttributeValue(buildID,
            BuildConfigAttribute.SHOW_MERCURIAL_PARAMETERS, BuildConfigAttribute.OPTION_UNCHECKED)
            .equals(BuildConfigAttribute.OPTION_CHECKED);
  }


  /**
   * Helper factory method.
   *
   * @return empty and invisible {@link
   *         ManualScheduleStartParametersPanel}
   */
  private static EmptyManualScheduleStartParametersPanel makeInvisibleEmptyPanel() {
    final EmptyManualScheduleStartParametersPanel result = new EmptyManualScheduleStartParametersPanel();
    result.setVisible(false);
    return result;
  }
}
