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
package org.parabuild.ci.services;

import org.parabuild.ci.ServersideTestCase;

/**
 * A tester for ListenPortConfig.
 */
public final class SSTestListenPortConfig extends ServersideTestCase {

  /**
   * Object under test.
   */
  private ListenPortConfig listenPortConfig;


  public SSTestListenPortConfig(final String s) {
    super(s);
  }


  public void setUp() throws Exception {

    super.setUp();

    listenPortConfig = new ListenPortConfig();
  }


  public void tearDown() throws Exception {

    listenPortConfig = null;

    super.tearDown();
  }


  public void testGetListenPort() throws Exception {

    assertEquals(8080, listenPortConfig.getListenPort());
  }


  @Override
  public String toString() {
    return "SSTestListenPortConfig{" +
            "listenPortConfig=" + listenPortConfig +
            "} " + super.toString();
  }
}