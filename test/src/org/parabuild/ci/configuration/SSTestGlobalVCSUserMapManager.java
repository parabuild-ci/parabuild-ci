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

import junit.framework.TestCase;
import org.parabuild.ci.object.GlobalVCSUserMap;

import java.util.List;

/**
 * GlobalVCSUserMapManager Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>12/28/2008</pre>
 */
public final class SSTestGlobalVCSUserMapManager extends TestCase {

  private GlobalVCSUserMapManager manager = null;


  public SSTestGlobalVCSUserMapManager(String s) {
    super(s);
  }


  public void testGetInstance() throws Exception {
    assertNotNull(manager);
  }


  public void testGetMapping() throws Exception {
    assertNotNull(manager.getMapping(new Integer(0)));
  }


  public void testDeleteMapping() throws Exception {
    manager.deleteMapping(manager.getMapping(new Integer(0)));
    assertNull(manager.getMapping(new Integer(0)));
  }


  public void testGetAllMappings() throws Exception {
    final List allMappings = manager.getAllMappings();
    assertNotNull(allMappings);
    assertTrue(!allMappings.isEmpty());
  }


  public void testToString() {
    assertNotNull(manager.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    manager = new GlobalVCSUserMapManager();
  }
}
