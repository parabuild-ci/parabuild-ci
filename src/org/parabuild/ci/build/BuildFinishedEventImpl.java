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

import java.util.*;

import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.services.BuildFinishedEvent;

final class BuildFinishedEventImpl implements BuildFinishedEvent {

  private final BuildRun buildRun;


  BuildFinishedEventImpl(final BuildRun buildRun) {
    this.buildRun = buildRun;
  }


  public int getBuildRunID() {
    return buildRun.getBuildRunID();
  }


  public int getBuildTimeSeconds() {
    return (int)((buildRun.getFinishedAt().getTime() - buildRun.getStartedAt().getTime()) / 1000L);
  }


  public byte getBuildResultCode() {
    return buildRun.getResultID();
  }


  public Date getBuildFinishedAt() {
    return buildRun.getFinishedAt();
  }


  public String toString() {
    return "BuildFinishedEventImpl{" +
      "buildRun=" + buildRun +
      '}';
  }
}
