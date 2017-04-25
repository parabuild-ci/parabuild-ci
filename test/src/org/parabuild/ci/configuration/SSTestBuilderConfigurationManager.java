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

import org.parabuild.ci.ServersideTestCase;


public final class SSTestBuilderConfigurationManager extends ServersideTestCase {

  private BuilderConfigurationManager manager;


  public SSTestBuilderConfigurationManager(final String s) {
    super(s);
  }


  public void testFindAgentByHost() {
    assertNotNull(manager.findAgentByHost("always_offline:1212"));
  }


  public void testGetAgentBuilders() {
    assertNotNull(manager.getAgentBuilders(0));
  }


  protected void setUp() throws Exception {
    super.setUp();
    manager = BuilderConfigurationManager.getInstance();
  }
}
