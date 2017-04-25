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
package org.parabuild.ci.versioncontrol.perforce;

import com.dautelle.util.Enum;

import java.util.Collection;

/**
 * Defines resolve mode
 */
public class ResolveMode extends Enum {

  private static final long serialVersionUID = -1516219241786320963L; // NOPMD

  public static final ResolveMode AM = new ResolveMode(1, "am");
  public static final ResolveMode AY = new ResolveMode(2, "ay");
  public static final ResolveMode AT = new ResolveMode(3, "at");

  public static final Collection VALUES = getInstances(ResolveMode.class);


  /**
   * Constructor
   */
  private ResolveMode(final long l, final String s) { // NOPMD
    super(l, s);
  }


  public String toString() {
    return super.getName();
  }
}

