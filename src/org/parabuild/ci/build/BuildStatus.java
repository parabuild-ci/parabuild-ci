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
 * Build status enumeration. Build status describes consolidated
 * build status.
 */
public final class BuildStatus extends Enum {

  private static final long serialVersionUID = -1516219241786320963L; // NOPMD

  public static final byte INACTIVE_VALUE = 0;
  public static final byte UNDEFINED_VALUE = 1;
  public static final byte ACTIVE_VALUE = 2;
  public static final byte BUILDING_VALUE = 3;
  public static final byte IDLE_VALUE = 4;
  public static final byte STOPPING_VALUE = 5;
  public static final byte CHECKING_OUT_VALUE = 6;
  public static final byte INITIALIZING_VALUE = 7;
  public static final byte STARTING_VALUE = 8;
  public static final byte GETTING_CHANGES_VALUE = 9;
  public static final byte PAUSED_VALUE = 10;
  public static final byte INVALID_USER_VALUE = 12;
  public static final byte PENDING_VALUE = 13;

  public static final BuildStatus ACTIVE = new BuildStatus(ACTIVE_VALUE, "active");
  public static final BuildStatus BUILDING = new BuildStatus(BUILDING_VALUE, "building");
  public static final BuildStatus CHECKING_OUT = new BuildStatus(CHECKING_OUT_VALUE, "checkout");
  public static final BuildStatus GETTING_CHANGES = new BuildStatus(GETTING_CHANGES_VALUE, "getting changes");
  public static final BuildStatus IDLE = new BuildStatus(IDLE_VALUE, "idle");
  public static final BuildStatus INACTIVE = new BuildStatus(INACTIVE_VALUE, "inactive");
  public static final BuildStatus INITIALIZING = new BuildStatus(INITIALIZING_VALUE, "initializing");
  public static final BuildStatus INVALID_USER = new BuildStatus(INVALID_USER_VALUE, "invalid user");
  public static final BuildStatus PAUSED = new BuildStatus(PAUSED_VALUE, "paused");

  /**
   * Means that a build is ready to start but it was serialized and cannot proceed while another build is building.
   */
  public static final BuildStatus PENDING_BUILD = new BuildStatus(PENDING_VALUE, "pending");

  public static final BuildStatus STOPPING = new BuildStatus(STOPPING_VALUE, "stopping");
  public static final BuildStatus UNDEFINED = new BuildStatus(UNDEFINED_VALUE, "undefined");

  /**
   * Build starting
   */
  public static final BuildStatus STARTING = new BuildStatus(STARTING_VALUE, "starting");

  public static final Collection VALUES = getInstances(BuildStatus.class);


  /**
   * Constructor
   */
  private BuildStatus(final long l, final String s) { // NOPMD
    super(l, s);
  }


  public String toString() {
    return super.getName();
  }
}
