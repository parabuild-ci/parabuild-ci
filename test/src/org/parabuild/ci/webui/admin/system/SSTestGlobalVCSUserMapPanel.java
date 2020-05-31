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
package org.parabuild.ci.webui.admin.system;

import junit.framework.TestCase;
import org.parabuild.ci.configuration.GlobalVCSUserMapManager;

/**
 * GlobalVCSUserMapPanel Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>12/28/2008</pre>
 */
public final class SSTestGlobalVCSUserMapPanel extends TestCase {

  private static final int MAPPING_ID = 0;

  private GlobalVCSUserMapPanel panel = null;


  public SSTestGlobalVCSUserMapPanel(String s) {
    super(s);
  }


  public void testToString() {
    assertNotNull(panel.toString());
  }


  public void testLoad() {
    panel.load(GlobalVCSUserMapManager.getInstance().getMapping(Integer.valueOf(MAPPING_ID)));
    assertEquals(MAPPING_ID, panel.getMappingID());
  }


  protected void setUp() throws Exception {
    super.setUp();
    panel = new GlobalVCSUserMapPanel();
  }
}
