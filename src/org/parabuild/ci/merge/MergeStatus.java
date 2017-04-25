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
package org.parabuild.ci.merge;

import com.dautelle.util.Enum;

import java.util.Collection;

/**
 * Merge status enumeration. Merge status describes consolidated
 * build status.
 */
public final class MergeStatus extends Enum {

  private static final long serialVersionUID = -1516219241786320963L; // NOPMD

  private static final byte CHECKING_OUT_VALUE = 1;
  private static final byte GETTING_CHANGES_VALUE = 2;
  private static final byte IDLE_VALUE = 3;
  private static final byte INITIALIZING_VALUE = 5;
  private static final byte MERGING_VALUE = 6;
  private static final byte PAUSED_VALUE = 7;
  private static final byte STARTING_VALUE = 8;
  private static final byte STOPPING_VALUE = 9;
  private static final byte UNDEFINED_VALUE = 10;
  private static final byte VALIDATING_VALUE = 11;

  public static final MergeStatus VALIDATING = new MergeStatus(VALIDATING_VALUE, "Validating");
  public static final MergeStatus CHECKING_OUT = new MergeStatus(CHECKING_OUT_VALUE, "Checkout");
  public static final MergeStatus GETTING_CHANGES = new MergeStatus(GETTING_CHANGES_VALUE, "Getting changes");
  public static final MergeStatus IDLE = new MergeStatus(IDLE_VALUE, "Idle");
  public static final MergeStatus INITIALIZING = new MergeStatus(INITIALIZING_VALUE, "Initializing");
  public static final MergeStatus MERGING = new MergeStatus(MERGING_VALUE, "Merging");
  public static final MergeStatus PAUSED = new MergeStatus(PAUSED_VALUE, "Paused");
  public static final MergeStatus STARTING = new MergeStatus(STARTING_VALUE, "Starting");
  public static final MergeStatus STOPPING = new MergeStatus(STOPPING_VALUE, "Stopping");
  public static final MergeStatus UNDEFINED = new MergeStatus(UNDEFINED_VALUE, "Undefined");

  public static final Collection VALUES = getInstances(MergeStatus.class);


  /**
   * Constructor
   */
  private MergeStatus(final long l, final String s) { // NOPMD
    super(l, s);
  }


  public String toString() {
    return super.getName();
  }
}
