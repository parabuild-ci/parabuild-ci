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
package org.parabuild.ci.configuration;

import java.io.Serializable;

/**
 * BuilderAgentVO
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 16, 2009 7:07:46 PM
 */
public final class BuilderAgentVO implements Serializable {

  private static final long serialVersionUID = 6101069810575014811L;

  private final boolean enabled;
  private final boolean local;
  private final int agentID;
  private final int builderAgentID;
  private final String host;
  private final String password;


  public BuilderAgentVO(final int builderAgentID, final String host, final boolean enabled,
                        final boolean local, final int agentID, final String password) {
    this.builderAgentID = builderAgentID;
    this.host = host;
    this.enabled = enabled;
    this.local = local;
    this.agentID = agentID;
    this.password = password;
  }


  public int getBuilderAgentID() {
    return builderAgentID;
  }


  public String getHost() {
    return host;
  }


  public boolean isEnabled() {
    return enabled;
  }


  public boolean isLocal() {
    return local;
  }


  public int getAgentID() {
    return agentID;
  }


  public String getPassword() {
    return password;
  }
}
