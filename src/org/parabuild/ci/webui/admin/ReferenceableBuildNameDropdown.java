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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.CodeNameDropDown;

import java.util.Iterator;


/**
 * Dropdown to show builds, with reference build excluded.
 */
public final class ReferenceableBuildNameDropdown extends CodeNameDropDown {

  private static final long serialVersionUID = 4643818514802669312L; // NOPMD

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ReferenceableBuildNameDropdown.class); // NOPMD

  public static final String ITEM_PLEASE_SELECT = "Please select: ";


  /**
   * Constructor
   */
  public ReferenceableBuildNameDropdown(final boolean allowNonexistingCodes) {
    super(allowNonexistingCodes);
    // add empty item
    addCodeNamePair(BuildConfig.UNSAVED_ID, ITEM_PLEASE_SELECT);
    setSelection(0);

    // populate dropdown with builds
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (final Iterator i = cm.getExistingBuildConfigs().iterator(); i.hasNext();) {
      final ActiveBuildConfig abc = (ActiveBuildConfig)i.next();
      if (abc.getScheduleType() == ActiveBuildConfig.SCHEDULE_TYPE_PARALLEL) {
        continue; // ignore parallel schedule types
      }
      addCodeNamePair(abc.getBuildID(), abc.getBuildName());
    }
  }


  /**
   * Constructor
   */
  public ReferenceableBuildNameDropdown() {
    this(PROHIBIT_NONEXISTING_CODES);
  }


  /**
   * Returns currently selected build ID.
   * <p/>
   * Returns BuildConfig.UNSAVED_ID if user didn't make a
   * selection yet.
   */
  public int getSelectedBuildID() {
    return getCode();
  }


  /**
   * Sets selected code
   */
  public void setCode(final int code) {
    if (!codeExists(code)) {
      // NOTE: vimeshev - 2006-13-30 - it is a dirty hack to
      // address the fact that configuration report deals with
      // build run configs rather than active configs.
      final BuildConfig bc = ConfigurationManager.getInstance().getBuildConfiguration(code);
      if (bc != null) {
        addCodeNamePair(bc.getBuildID(), bc.getBuildName());
        setCode(code);
      }
    }

    //
    super.setCode(code);
  }


  /**
   * Sets selected build ID
   */
  public void setSelectedBuildID(final int buildID) {
    setCode(buildID);
  }


  /**
   * Excludes given build ID.
   *
   * @param buildIDToExclude
   */
  public void excludeBuildID(final int buildIDToExclude) {
    // don't exclude unset id
    if (buildIDToExclude == BuildConfig.UNSAVED_ID) return;
    final int selectedCode = getCode();
    removeCode(buildIDToExclude);
    // offset changes in the drop-down internal structures
    if (selectedCode != buildIDToExclude) {
      setCode(selectedCode);
    }
  }
}