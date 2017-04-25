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
package org.parabuild.ci.services;

import org.parabuild.ci.build.BuildState;

import java.util.Collection;
import java.util.List;

/**
 * BuildService service interface.
 * <p/>
 * The lifecycle of build service is the following - method
 * startupService is called, other methods are called,
 * shutdownService is called.
 *
 * @see Service
 */
public interface BuildListService extends Service {

  /**
   * Returns collection of builds
   *
   * @return
   */
  Collection getBuilds();


  /**
   * Returns the build
   */
  BuildService getBuild(int buildID);


  /**
   * Removes build
   */
  void removeBuild(int buildID);


  /**
   * Returns a list of current build statuses
   *
   * @return List of BuildStatus objects
   * @see BuildState
   */
  List getCurrentBuildStatuses();


  /**
   * This method is used by build manager clients to notify that
   * there were unspecified changes made in current build set
   * configuration.
   */
  void notifyConfigurationsChanged();
}
