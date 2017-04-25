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
 * Avaiable types of build start parameters.
 */
public final class StartParameterType extends Enum {

  private static final long serialVersionUID = -1516219241786320963L; // NOPMD

  /**
   * Enumeration index for build mode parameter. <b>Do not change</b> - persistence depends on this.
   */
  private static final byte BUILD_VALUE = 0;

  /**
   * Enumeration index for publish mode parameter.  <b>Do not change</b> - persistence depends on this.
   */
  private static final byte PUBLISH_VALUE = 1;

  private static final byte SYSTEM_VALUE = 2;

  private static final byte AGENT_VALUE = 3;

  private static final byte PROJECT_VALUE = 4;

  /**
   * Build start parameter.
   */
  public static final StartParameterType BUILD = new StartParameterType(BUILD_VALUE, "build");
  /**
   * Global start parameter.
   */
  public static final StartParameterType SYSTEM = new StartParameterType(SYSTEM_VALUE, "system");

  /**
   * Project-level start parameter.
   */
  public static final StartParameterType PROJECT = new StartParameterType(PROJECT_VALUE, "project");
  /**
   * Agent-level start parameter.
   */
  public static final StartParameterType AGENT = new StartParameterType(AGENT_VALUE, "agent");

  /**
   * Publishing parameter
   */
  public static final StartParameterType PUBLISH = new StartParameterType(PUBLISH_VALUE, "publish");


  /**
   * Constructor.
   */
  private StartParameterType(final long l, final String s) { // NOPMD
    super(l, s);
  }


  public String toString() {
    return super.getName();
  }


  public static StartParameterType byteToType(final byte byteType) {
    switch (byteType) {
      case AGENT_VALUE:
        return AGENT;
      case BUILD_VALUE:
        return BUILD;
      case PROJECT_VALUE:
        return PROJECT;
      case PUBLISH_VALUE:
        return PUBLISH;
      case SYSTEM_VALUE:
        return SYSTEM;
      default:
        throw new IllegalArgumentException("Unknown type: " + byteType);
    }
  }
}
