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
package org.parabuild.ci.webui.common;

import org.parabuild.ci.util.StringUtils;
import viewtier.ui.Parameters;

import java.io.Serializable;

/**
 * Attribute to keep return tierlet path and params. This class is used to keep
 * return paths in a session, particularly when accessing pages that need to go
 * via a system configuration before being accessed.
 */
public final class ReturnTierletAttribuite implements Serializable {

  private static final long serialVersionUID = 6623871401199469833L; // NOPMD

  private String tierlet = null;
  private Parameters parameters = null;


  /**
   * Constructor
   *
   * @param tierlet
   * @param parameters
   */
  public ReturnTierletAttribuite(final String tierlet, final Parameters parameters) {
    if (StringUtils.isBlank(tierlet)) throw new IllegalArgumentException("Tierlet is null");
    if (parameters == null) throw new IllegalArgumentException("Parameters are null");
    this.tierlet = tierlet;
    this.parameters = parameters;
  }


  public Parameters getParameters() {
    return parameters;
  }


  public String getTierlet() {
    return tierlet;
  }
}
