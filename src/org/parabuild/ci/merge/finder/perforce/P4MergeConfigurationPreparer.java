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
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.merge.MergeDAO;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.versioncontrol.perforce.P4BranchView;
import org.parabuild.ci.versioncontrol.perforce.P4SourceControl;

import java.io.IOException;

/**
 * Prepares runtime merge configuration.
 */
public class P4MergeConfigurationPreparer {

  private static final Log log = LogFactory.getLog(P4NewChangeListsFinder.class);


  public BranchMergeConfiguration prepare(final ActiveMergeConfiguration activeConfiguration) throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("=========== begin preparing configuration ===========");

    // check if we can use existing
    BranchMergeConfiguration mc = MergeDAO.getInstance().findSameRuntime(activeConfiguration);
    if (mc == null) {
      // create a copy of the merge configuration to use with this detection cycle.
      mc = new BranchMergeConfiguration();
      mc.setActiveMergeID(activeConfiguration.getID());
      mc.setBranchView(activeConfiguration.getBranchView());
      mc.setBranchViewName(activeConfiguration.getBranchViewName());
      mc.setBranchViewSource(activeConfiguration.getBranchViewSource());
      mc.setConflictResolutionMode(activeConfiguration.getConflictResolutionMode());
      mc.setDescription(activeConfiguration.getDescription());
      mc.setIndirectMerge(activeConfiguration.isIndirectMerge());
      mc.setMarker(activeConfiguration.getMarker());
      mc.setMergeMode(activeConfiguration.getMergeMode());
      mc.setName(activeConfiguration.getName());
      mc.setPreserveMarker(activeConfiguration.isPreserveMarker());
      mc.setReverseBranchView(activeConfiguration.isReverseBranchView());
      mc.setSourceBuildID(activeConfiguration.getSourceBuildID());
      mc.setTargetBuildID(activeConfiguration.getTargetBuildID());
      // get set actual branch view and remember it in merge configuration
      if (mc.getBranchViewSource() == MergeConfiguration.BRANCH_VIEW_SOURCE_BRANCH_NAME) {
        final ConfigurationManager cm = ConfigurationManager.getInstance();
        final BuildConfig buildConfiguration = cm.getBuildConfiguration(mc.getSourceBuildID());
        final AgentHost host = AgentManager.getInstance().getNextLiveAgent(activeConfiguration.getTargetBuildID()).getHost();
        final P4SourceControl perforce = new P4SourceControl(buildConfiguration);
        perforce.setAgentHost(host);
        final P4BranchView branchView = perforce.getBranchView(mc.getBranchViewName());
        mc.setBranchView(branchView.view());
      }
      // save
      MergeDAO.getInstance().save(mc);
    }
    ThreadUtils.checkIfInterrupted();

    if (log.isDebugEnabled()) log.debug("=========== end preparing configuration ===========");

    return mc;
  }
}
