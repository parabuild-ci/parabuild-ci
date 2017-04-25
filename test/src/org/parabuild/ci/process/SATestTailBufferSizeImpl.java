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
public class SATestTailBufferSizeImpl extends TestCase {

  private static final int MAX_LINE_COUNT = 1;
  private static final int MAX_LINE_LENGTH = 2;


  public void test_create() {
    final TailBufferSizeImpl size = new TailBufferSizeImpl(MAX_LINE_COUNT, MAX_LINE_LENGTH);
    assertEquals(MAX_LINE_COUNT, size.getMaxLineCount());
    assertEquals(MAX_LINE_LENGTH, size.getMaxLineLength());
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestTailBufferSizeImpl.class);
  }


  public SATestTailBufferSizeImpl(final String s) {
    super(s);
  }
}
