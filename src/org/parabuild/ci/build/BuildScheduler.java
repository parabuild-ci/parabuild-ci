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

import org.parabuild.ci.services.BuildStartRequest;

import java.util.Date;

/**
 * Build scheduler is responsible for invoking build runner when
 * it's necessary.
 */
public interface BuildScheduler {

  /**
   * Enables build scheduler
   */
  void requestActivate();


  /**
   * Method to start thread implementing this interface
   */
  void start();


  /**
   * Requests scheduler to die
   */
  void requestShutdown();


  /**
   * Requests this schedule to reload it's configuration. This
   * method is used to notify a scheduler about changes in
   * persistent configuration.
   */
  void reloadSchedule();


  /**
   * @return Current scheduler status
   *
   * @see SchedulerStatus#CHECKING_OUT - if scheduler performs
   *      initial check out.
   * @see SchedulerStatus#GETTING_CHANGES - if scheduler analyzes
   *      if there are changes
   * @see SchedulerStatus#IDLE - if scheduler is active but idle
   *      between scheduler cycles;
   */
  SchedulerStatus getStatus();


  /**
   * Request scheduler to pause until further notification
   * request to activate.
   */
  void requestPause();


  /**
   * Request scheduler to resume.
   *
   * @see #requestPause
   */
  void requestResume();


  /**
   * Requests that next checkout runs cleanly.
   */
  void requestCleanCheckout();


  /**
   * @return time when next build will run or null if there is no
   * information.
   */
  Date nextBuildTime();


  /**
   * Requests to run a build once
   */
  void requestRunOnce(final BuildStartRequest startRequest);
}
