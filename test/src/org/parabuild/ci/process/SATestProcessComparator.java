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
package org.parabuild.ci.process;

import junit.framework.*;

/**
 * Tests ProcessManager functionality
 */
public class SATestProcessComparator extends TestCase {

  private ProcessComparator c1;
  private ProcessComparator c2;
  private ProcessComparator c3;

  private OSProcess p1;
  private OSProcess p2;

  public SATestProcessComparator(final String s) {
    super(s);
  }


  /**
   * Test comparison
   */
  public void test_compare() throws Exception {
    assertTrue(c1.compare(p1, p2) > 0);
    assertTrue(c2.compare(p1, p2) > 0);
    assertTrue(c3.compare(p1, p2) > 0);
  }


  protected void setUp() throws Exception {
    super.setUp();
    p2 = new OSProcess(1, 
                       2,
                       "A",
                       "B",
                       "C",
                       "D");
                                                
    p1 = new OSProcess(3, 
                       4,
                       "B",
                       "C",
                       "D",
                       "E");
                                                
    c1 = new ProcessComparator(ProcessManager.SORT_BY_PID);                                                
    c2 = new ProcessComparator(ProcessManager.SORT_BY_PPID);                                                
    c3 = new ProcessComparator(ProcessManager.SORT_BY_NAME);                                                
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestProcessComparator.class);
  }
}
