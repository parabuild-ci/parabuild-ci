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

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 */
public class SATestTailLineImpl extends TestCase {

  private static final int LINE_NUMBER = 1;
  private static final int TIME_STAMP = 2;
  private static final String TEST_LINE = "test line";


  public void test_create() {
    final TailLineImpl tailLine = new TailLineImpl(LINE_NUMBER, TIME_STAMP, TEST_LINE);
    assertEquals(LINE_NUMBER, tailLine.getLineNumber());
    assertEquals(TIME_STAMP, tailLine.getTimeStamp());
    assertEquals(TEST_LINE, tailLine.getLine());
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestTailLineImpl.class);
  }


  public SATestTailLineImpl(final String s) {
    super(s);
  }
}
