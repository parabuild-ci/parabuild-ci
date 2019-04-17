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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import viewtier.ui.Color;
import viewtier.ui.Flow;
import viewtier.ui.Label;

/**
 * Shows currently buildng steps for a given build if form
 * <p/>
 * BUILD >> TEST >> DEPLOY >> ARCHIVE
 * <p/>
 * If there are now steps builing, will not show anything.
 */
public final class CurrentlyBuildingStepsFlow extends Flow {

  private static final long serialVersionUID = 4684969097362622085L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(CurrentlyBuildingStepsFlow.class); // NOPMD


  public CurrentlyBuildingStepsFlow(final BuildState currentState) {
    this(currentState, true);
  }


  /**
   * Default constructor
   */
  public CurrentlyBuildingStepsFlow(final BuildState currentState, final boolean showRunningBuild) {
    if (currentState == null || !currentState.isRunning()) {
      return; // nothing to show
    }

    if (showRunningBuild) {
      add(new CommonLabel("Running build "));
      add(new BoldCommonLabel("#" + currentState.getCurrentlyRunningBuildNumber() + " @ " + currentState.getCurrentlyRunningChangeListNumber()));
      add(new CommonLabel(", step: "));
    } else {
      add(new CommonLabel("Running step: "));
    }

    if (currentState.getStatus().equals(BuildStatus.CHECKING_OUT)) {
      // only checking out
      add(new BoldCommonLabel(currentState.getStatusAsString()));
    } else {

      // show steps already run
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final List stepRuns = cm.getStepRunNames(currentState.getCurrentlyRunningBuildRunID());
      boolean isDirty = false;
      int currentIndex = 0;
      for (final Iterator i = stepRuns.iterator(); i.hasNext();) {
        final String stepRunName = (String)i.next();
        add(makeStepLabel(stepRunName));
        isDirty = true;
        // add separator
        if (i.hasNext()) {
          add(new StepNameSeparatorLabel());
        }
        currentIndex++;
      }

      // show future/current steps
      final int currentlyRunningBuildRunID = currentState.getCurrentlyRunningBuildRunID();
      final BuildRun buildRun = cm.getBuildRun(currentlyRunningBuildRunID);
      final BuildStepType type = buildRun == null ? BuildStepType.BUILD : buildRun.getType() == BuildRun.TYPE_PUBLISHING_RUN ? BuildStepType.PUBLISH : BuildStepType.BUILD;
      final List configuredSteps = cm.getEnabledBuildSequences(currentState.getCurrentlyRunningBuildConfigID(), type);
      final int configuredStepsSize = configuredSteps.size();
      if (currentIndex >= 0 && currentIndex < configuredStepsSize) {
        if (isDirty) {
          add(new StepNameSeparatorLabel());
        }
        for (int i = currentIndex; i < configuredStepsSize; i++) {
          final BuildSequence buildSequence = (BuildSequence)configuredSteps.get(i);
          final Label lbStepName = makeStepLabel(buildSequence.getStepName());
          add(lbStepName);
          if (buildSequence.getSequenceID() == currentState.getCurrentlyRunningSequenceID()) {
            // currently building - mark black
            lbStepName.setForeground(Color.Black);
          }
          if (i < configuredStepsSize - 1) {
            add(new StepNameSeparatorLabel());
          }
        }
      }
    }
  }


  /**
   * Makes grey bold step label.
   *
   * @param stepName String step name
   *
   * @return created Label
   */
  private static Label makeStepLabel(final String stepName) {
    final Label result = new BoldCommonLabel(stepName);
    result.setForeground(Color.Gray);
    return result;
  }


  /**
   * Used to separate step names.
   */
  private static final class StepNameSeparatorLabel extends Label {

    public static final String STEP_NAME_SEPARATOR = " &gt; ";
    private static final long serialVersionUID = -9107788882956405865L;


    /**
     * Constructor
     *
     * @see #STEP_NAME_SEPARATOR
     */
    StepNameSeparatorLabel() {
      super(STEP_NAME_SEPARATOR);
    }


    public String toString() {
      return "StepNameSeparatorLabel{}";
    }
  }


  public String toString() {
    return "CurrentlyBuildingStepsFlow{}";
  }
}
