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

import java.util.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 */
public class SATestCyclicBuffer extends TestCase {

  private static final int MAX_SIZE = 3;
  final CyclicBuffer buffer = new CyclicBuffer(MAX_SIZE);


  public void test_add() {
    buffer.add(new Integer(0));
    buffer.add(new Integer(1));
    buffer.add(new Integer(2));
    buffer.add(new Integer(3));
    assertEquals(MAX_SIZE, buffer.length());
    assertEquals(new Integer(1), buffer.get());
    assertEquals(new Integer(2), buffer.get());
    assertEquals(new Integer(3), buffer.get());
  }


  public void test_getAll() {
    buffer.add(new Integer(0));
    buffer.add(new Integer(1));
    buffer.add(new Integer(2));
    buffer.add(new Integer(3));
    final List all = buffer.getAll();
    assertEquals(new Integer(1), all.get(0));
    assertEquals(new Integer(2), all.get(1));
    assertEquals(new Integer(3), all.get(2));
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestCyclicBuffer.class);
  }


  public SATestCyclicBuffer(final String s) {
    super(s);
  }
}
