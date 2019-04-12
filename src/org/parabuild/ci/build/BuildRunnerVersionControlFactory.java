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

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.versioncontrol.SourceControl;

/**
 * Defines a factory used by build runner to create a
 * version control to work on the workspace.
 */
public interface BuildRunnerVersionControlFactory {

  /**
   * Creates a new instance of SourceControl based on the given
   * build config.
   *
   * @param buildConfig - build config to use to create a
   * SourceControl for. BuildConfig can be either BuildRunConfig
   * or ActiveBuildConfig. ActiveBuildConfig is passed by
   * automatic schedulers that watch source line for changes.
   * BuildRunConfig is passed by build runners to run a build
   * against the given build configuration.
   */
  SourceControl getVersionControl(final BuildConfig buildConfig);
}
