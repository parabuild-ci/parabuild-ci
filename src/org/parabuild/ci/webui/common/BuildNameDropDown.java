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
package org.parabuild.ci.webui.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;

import java.util.Iterator;

/**
 * Dropdown to show builds.
 */
public final class BuildNameDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = -4566528785783535158L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BuildNameDropDown.class); // NOPMD


  /**
   * Constructor
   */
  public BuildNameDropDown() {
    // add "any" build ID
    super.addCodeNamePair(BuildConfig.UNSAVED_ID, "Any");

    // populate dropdown with builds
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (final Iterator i = cm.getExistingBuildConfigs().iterator(); i.hasNext();) {
      final BuildConfig bc = (BuildConfig)i.next();
      super.addCodeNamePair(bc.getBuildID(), bc.getBuildName());
    }
  }


  /**
   * Returns currently selected build ID.
   * <p/>
   * Returns BuildConfig.UNSAVED_ID if user has selected "Any"
   * build.
   */
  public int getSelectedBuildID() {
    return super.getCode();
  }
}