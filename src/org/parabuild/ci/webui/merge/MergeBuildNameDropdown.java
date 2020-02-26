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
package org.parabuild.ci.webui.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.CodeNameDropDown;

import java.util.Iterator;

/**
 * Dropdown to show builds that can be used as sources and desitnations in merges.
 */
final class MergeBuildNameDropdown extends CodeNameDropDown {

  private static final long serialVersionUID = 4643818514802669312L; // NOPMD

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(MergeBuildNameDropdown.class); // NOPMD

  private static final String ITEM_PLEASE_SELECT = "Please select";
  public static final int NOT_SELECTED = BuildConfig.UNSAVED_ID;


  /**
   * Constructor
   */
  public MergeBuildNameDropdown() {
    super.addCodeNamePair(NOT_SELECTED, ITEM_PLEASE_SELECT);
    super.setSelection(0);
  }


  public void populate(final int projectID) {

    // populate dropdown with builds
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (final Iterator i = cm.findBuildConfigsByVCS(projectID, VersionControlSystem.SCM_PERFORCE).iterator(); i.hasNext();) {
      final ActiveBuildConfig abc = (ActiveBuildConfig)i.next();
      if (abc.getScheduleType() != ActiveBuildConfig.SCHEDULE_TYPE_PARALLEL) {
        addCodeNamePair(abc.getBuildID(), abc.getBuildName());
      }
    }
  }
}
