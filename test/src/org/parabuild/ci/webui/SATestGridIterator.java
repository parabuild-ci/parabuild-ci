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
package org.parabuild.ci.webui;

import junit.framework.*;

import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Tests home page
 */
public class SATestGridIterator extends TestCase {

  public SATestGridIterator(final String s) {
    super(s);
  }


  /**
   * Makes sure that home page responds
   */
  public void testadd() throws Exception {
    final Panel panel = new Panel();
    final Label comp1 = new Label();
    final Label comp2 = new Label();
    final Label comp3 = new Label();
    final GridIterator gridIterator = new GridIterator(panel, 2);
    gridIterator.add(comp1);
    gridIterator.add(comp2);
    gridIterator.add(comp3);
    final Layout currentLayout = gridIterator.getCumulativeLayout();
    assertEquals(1, currentLayout.positionX);
    assertEquals(1, currentLayout.positionY);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestGridIterator.class);
  }
}
