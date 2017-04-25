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
package org.parabuild.ci.merge.merger.build.perforce;

import org.parabuild.ci.build.BuildRunnerVersionControlFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.perforce.P4SourceControl;

/**
 * This class simply returns pre-configured Perforce
 * instance instead of creating a new one.
 *
 * @see BuildRunnerVersionControlFactory
 */
final class MergerBuildRunnerVersionControlFactory implements BuildRunnerVersionControlFactory {

  private final P4SourceControl perforce;


  public MergerBuildRunnerVersionControlFactory(final P4SourceControl perforce) {
    this.perforce = perforce;
  }


  public SourceControl getVersionControl(final BuildConfig buildConfig) {
    return perforce;
  }
}
