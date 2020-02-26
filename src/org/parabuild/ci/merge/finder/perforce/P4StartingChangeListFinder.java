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
package org.parabuild.ci.merge.finder.perforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.ThreadUtils;
import org.parabuild.ci.util.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.merge.MergeClientNameGenerator;
import org.parabuild.ci.merge.MergeDAO;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.versioncontrol.perforce.P4SourceControl;

/**
 * Finds a change list number from that we should be
 * checking for changes a branch.
 * <p/>
 * The idea is to create clients for both sides of the
 * branch definfition. Look up for the first change list in
 * a branch. The latest on is the first commmon point.
 */
public final class P4StartingChangeListFinder {

  private static final Log log = LogFactory.getLog(P4StartingChangeListFinder.class);


  private final BranchMergeConfiguration mc;


  public P4StartingChangeListFinder(final BranchMergeConfiguration mergeConfiguration) {
    this.mc = mergeConfiguration;
  }


  public int findStartingChangeListNumber() throws CommandStoppedException, ValidationException, BuildException, AgentFailureException {

    if (log.isDebugEnabled()) log.debug("=========== begin finding starting change list number ===========");

    // try to find last already found change list number
    final ChangeList latestBranchChangeList = MergeDAO.getInstance().findLatestBranchChangeList(mc.getActiveMergeID());
    if (latestBranchChangeList != null) {
      // return incremented last found. otherwise we will be
      // recording it all over again
      return Integer.parseInt(latestBranchChangeList.getNumber()) + 1;
    }
    ThreadUtils.checkIfInterrupted();

    // find first change lists
    final P4BranchViewToClientViewTransformer transformer = new P4BranchViewToClientViewTransformer(mc.getBranchView(), mc.isReverseBranchView());
    final MergeClientNameGenerator clientNameGenerator = new MergeClientNameGenerator(mc.getActiveMergeID());
    final P4SourceControl perforce = new P4SourceControl(ConfigurationManager.getInstance().getBuildConfiguration(mc.getSourceBuildID()));

    final Integer firstFromChangeList = perforce.findFirstChangeList(clientNameGenerator.generateSourceClientName(), transformer.transformToSourceClientView());
    validateChangeListNumber(firstFromChangeList, "From");
    ThreadUtils.checkIfInterrupted();

    final Integer firstToChangeList = perforce.findFirstChangeList(clientNameGenerator.generateTargetClientName(), transformer.transformToTargetClientView());
    validateChangeListNumber(firstToChangeList, "To");
    ThreadUtils.checkIfInterrupted();

    if (log.isDebugEnabled()) log.debug("=========== end finding starting change list number ===========");
    return Math.max(firstFromChangeList, firstToChangeList);
  }


  /**
   * Validates that the change list number is not null.
   */
  private static void validateChangeListNumber(final Integer firstFromChangeList, final String description) throws ValidationException {
    if (firstFromChangeList == null) {
      throw new ValidationException("Cannot find first change list for \"" + description + "\" part of the branch view");
    }
  }


  public String toString() {
    return "P4StartingChangeListFinder{" +
            "mc=" + mc +
            '}';
  }
}
