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

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SATestAverager extends TestCase {

  private Averager averager;


  public void test_addZero() {
    averager.add(0);
    assertEquals(0, averager.getAverage());
  }


  public void test_addOne() {
    averager.add(1);
    assertEquals(1, averager.getAverage());

    averager.add(1);
    assertEquals(1, averager.getAverage());

    averager.add(1);
    assertEquals(1, averager.getAverage());
  }


  public void test_addTwo() {
    averager.add(2);
    assertEquals(2, averager.getAverage());

    averager.add(2);
    assertEquals(2, averager.getAverage());

    averager.add(2);
    assertEquals(2, averager.getAverage());
  }


  public void test_add() {
    averager.add(1);
    assertEquals(1, averager.getAverage());

    averager.add(2);
    averager.add(2);
    averager.add(3);
    assertEquals(2, averager.getAverage());
  }


  protected void setUp() throws Exception {
    super.setUp();
    averager = new Averager();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestAverager.class);
  }


  public SATestAverager(final String s) {
    super(s);
  }
}
