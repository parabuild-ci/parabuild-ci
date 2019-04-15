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
package org.parabuild.ci.webui;

import java.util.*;

import org.parabuild.ci.webui.common.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.configuration.*;

/**
 * This dropdown holds names and IDs of a leading and dependant build runs
 * paricipated in a particilar build run.
 */
final class ParallelBuildRunListDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = -467728395828454585L;


  public ParallelBuildRunListDropDown(final BuildRun buildRun) {
    final List dependentParallelBuildRuns = ConfigurationManager.getInstance().getAllParallelBuildRunVOs(buildRun);
    for (final Iterator i = dependentParallelBuildRuns.iterator(); i.hasNext();) {
      final ParallelBuildRunVO vo = (ParallelBuildRunVO)i.next();
      addCodeNamePair(vo.getBuildRunID(), vo.getBuildName() + (vo.getDependence() == BuildRun.DEPENDENCE_LEADER ? " [Lead]" : ""));
    }
    setCode(buildRun.getBuildRunID());
  }
}
