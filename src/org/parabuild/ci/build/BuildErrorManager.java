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
package org.parabuild.ci.build;

import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.remote.Agent;

/**
 * This is a specialized class to use to report errors occurred in
 * the build package. The main goal is to remove clutter by
 * taking out error creating and reporting code and move it to
 * here.
 */
final class BuildErrorManager {

  public void reportUnexpectedBuildError(final int activeBuildID, final String hostName, final Exception e) {
    final Error error = new Error("Error while running build: " + StringUtils.toString(e));
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    error.setBuildID(activeBuildID);
    error.setHostName(hostName);
    error.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public void reportUnexpectedBuildFinalizationError(final int activeBuildID, final Exception e) {
    final Error err = new Error("Error while finalizing build");
    err.setDetails(e);
    err.setBuildID(activeBuildID);
    err.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    err.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    ErrorManagerFactory.getErrorManager().reportSystemError(err);
  }


  public void reportUnexpectedBuildStepError(final int activeBuildID, final String hostName, final BuildSequence sequence, final Exception e) {
    final Error error = new Error(activeBuildID, sequence.getStepName(), Error.ERROR_SUBSYSTEM_BUILD, e);
    error.setHostName(hostName);
    error.setErrorLevel(Error.ERROR_LEVEL_ERROR);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  /**
   * Helper method to report error.
   *
   * @param agent
   */
  public void reportUndeleteableCheckoutDir(final Agent agent) {
    try {
      final StringBuffer sb = new StringBuffer(200).append("Files left in the directory:\n");
      final String[] strings = agent.listFilesInDirectory(agent.getCheckoutDirName(), "");
      for (int i = 0; i < strings.length; i++) {
        sb.append(strings[i]).append('\n');
      }
      final Error error = new Error("Checkout directory \"" + agent.getCheckoutDirName() + "\" is not empty after clean up. Consequent server operations may fail.");
      error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setHostName(agent.getHost().getHost());
      error.setBuildID(agent.getActiveBuildID());
      error.setDetails(sb);
      error.setSendEmail(true);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    } catch (final Exception e) { // last resort, nothing to report further.
      IoUtils.ignoreExpectedException(e);
    }
  }


  /**
   * Reports that the change list is missing.
   */
  public static void reportCannotFindChangeList(final BuildRun previousBuildRun, final String previousChangeListNumber) {
    final Error error = new Error("Change list # " + previousChangeListNumber + " was not found for the previous build run, dump: " + previousBuildRun + ". Shell variables marking previous build run will not be set.");
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setBuildID(previousBuildRun.getActiveBuildID());
    error.setSendEmail(false);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "BuildErrorManager{}";
  }
}
