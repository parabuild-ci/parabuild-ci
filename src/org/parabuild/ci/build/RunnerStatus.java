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
 * BuildRunner status enumeration
 */
public final class RunnerStatus extends Enum {

  private static final long serialVersionUID = 6522735652534414950L; // NOPMD

  public static final byte WAITING_VALUE = 1;
  public static final byte BUILDING_VALUE = 2;
  public static final byte NOTIFYING_VALUE = 8;
  public static final byte STOPPING_VALUE = 16;
  public static final byte CHECKING_OUT_VALUE = 32;

  public static final RunnerStatus WAITING = new RunnerStatus(WAITING_VALUE, "waiting");
  public static final RunnerStatus BUILDING = new RunnerStatus(BUILDING_VALUE, "building");
  public static final RunnerStatus NOTIFYING = new RunnerStatus(NOTIFYING_VALUE, "notifying");
  public static final RunnerStatus STOPPING = new RunnerStatus(STOPPING_VALUE, "stopping");
  public static final RunnerStatus CHECKING_OUT = new RunnerStatus(CHECKING_OUT_VALUE, "checking out");

  public static final Collection VALUES = getInstances(RunnerStatus.class);


  /**
   * Constructor
   */
  private RunnerStatus(final long id, final String s) {
    super(id, s);
  }
}
