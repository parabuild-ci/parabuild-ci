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
package org.parabuild.ci.statistics;

import java.util.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SATestMovingAverager extends TestCase {

  private static final int WINDOW_SIZE = 3;

  private MovingAverager movingAverager;


  public void test_getWindowSize() {
    assertEquals(WINDOW_SIZE, movingAverager.getWindowSize());
  }


  public void test_add() {
    assertEquals(1, movingAverager.add(1));
    assertEquals(1, movingAverager.add(2));
    assertEquals(2, movingAverager.add(3));
    // here it should begin to shift out old values;
    assertEquals(3, movingAverager.add(4));
    assertEquals(4, movingAverager.add(5));
    assertEquals(5, movingAverager.add(6));
    assertEquals(6, movingAverager.add(7));
    assertEquals(6, movingAverager.add(5));
    assertEquals(5, movingAverager.add(5));
  }


  public void test_addWindow() {
    final List window = new ArrayList(5);
    window.add(new Long(1));
    window.add(new Long(2));
    window.add(new Long(3));
    window.add(new Long(4));
    window.add(new Long(5));
    assertEquals(4, movingAverager.addWindow(window));
    assertEquals(4, movingAverager.getAverage());
  }


  protected void setUp() throws Exception {
    super.setUp();
    movingAverager = new MovingAverager(WINDOW_SIZE);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestMovingAverager.class);
  }


  public SATestMovingAverager(final String s) {
    super(s);
  }
}
