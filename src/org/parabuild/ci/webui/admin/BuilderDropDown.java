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
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.common.CodeNameDropDown;

import java.util.List;

/**
 * BuilderDropDown
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 19, 2009 8:46:00 PM
 */
public final class BuilderDropDown extends CodeNameDropDown {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BuilderDropDown.class); // NOPMD
  private static final long serialVersionUID = 9122628134658040332L;


  public BuilderDropDown() {
    super(PROHIBIT_NONEXISTING_CODES);
  }


  /**
   * Populates the drop down.
   *
   * @param includeDeletedBuilders if true, will include deleted builders. This is
   *                               necessary to support displaying configurations
   *                               of build runs for that a builder has been market
   *                               as deleted.
   * @noinspection UnnecessaryParentheses
   */
  public void populate(final boolean includeDeletedBuilders) {
    final List list = BuilderConfigurationManager.getInstance().getAllBuilders();
    for (int i = 0; i < list.size(); i++) {
      final BuilderConfiguration builderConfiguration = (BuilderConfiguration) list.get(i);
      final boolean deleted = builderConfiguration.isDeleted();
      if (!deleted || (deleted && includeDeletedBuilders)) {
        addCodeNamePair(builderConfiguration.getID(), builderConfiguration.getName());
      }
    }
  }
}
