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

import com.dautelle.util.Enum;

import java.util.Collection;

/**
 * Scheduler status enumeration
 */
public final class SchedulerStatus extends Enum {

  private static final long serialVersionUID = -8842534567135089994L; // NOPMD

  private static final byte INITIALIZING_VALUE = 0;
  private static final byte IDLE_VALUE = 1;
  private static final byte GETTING_CHANGES_VALUE = 2;
  private static final byte STARTING_BUILD_VALUE = 3;
  private static final byte CHECKING_OUT_VALUE = 4;
  private static final byte PAUSED_VALUE = 5;
  private static final byte PENDING_VALUE = 6;

  public static final SchedulerStatus INITIALIZING = new SchedulerStatus(INITIALIZING_VALUE, "initializing");
  public static final SchedulerStatus IDLE = new SchedulerStatus(IDLE_VALUE, "idle");
  public static final SchedulerStatus GETTING_CHANGES = new SchedulerStatus(GETTING_CHANGES_VALUE, "getting changes");
  public static final SchedulerStatus STARTING_BUILD = new SchedulerStatus(STARTING_BUILD_VALUE, "starting build");
  public static final SchedulerStatus CHECKING_OUT = new SchedulerStatus(CHECKING_OUT_VALUE, "checking out");
  public static final SchedulerStatus PAUSED = new SchedulerStatus(PAUSED_VALUE, "paused");

  /**
   * Means that a build is ready to start but it was serialized and cannot proceed while another build is building.
   */
  public static final SchedulerStatus PENDING_BUILD = new SchedulerStatus(PENDING_VALUE, "pending build");

  public static final Collection VALUES = getInstances(SchedulerStatus.class);


  public SchedulerStatus(final long l, final String absoluteFile) {
    super(l, absoluteFile);
  }


  public String toString() {
    return super.getName();
  }
}
