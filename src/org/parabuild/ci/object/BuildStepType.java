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
package org.parabuild.ci.object;

import com.dautelle.util.Enum;

/**
 * Build status enumeration. Build status describes consolidated
 * build status.
 */
public final class BuildStepType extends Enum {

  private static final long serialVersionUID = -1516219241786320963L; // NOPMD

  private static final byte BUILD_VALUE = 0;
  private static final byte PUBLISH_VALUE = 1;

  /**
   * Build step.
   */
  public static final BuildStepType BUILD = new BuildStepType(BUILD_VALUE, "build");

  /**
   * Publish step.
   */
  public static final BuildStepType PUBLISH = new BuildStepType(PUBLISH_VALUE, "publish");


  /**
   * Constructor
   */
  private BuildStepType(final long l, final String s) { // NOPMD
    super(l, s);
  }


  public String toString() {
    return super.getName();
  }
}
