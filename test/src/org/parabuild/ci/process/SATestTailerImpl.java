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
public class SATestTailerImpl extends TestCase {

  private static final int MAX_LINE_COUNT = 2;
  private static final int MAX_LINE_LENGTH = 6;
  private static final TailBufferSizeImpl TAIL_BUFFER_SIZE = new TailBufferSizeImpl(MAX_LINE_COUNT, MAX_LINE_LENGTH);


  private static final String LINE_1 = "line 1";
  private static final String LINE_2 = "line 2";
  private static final String LINE_3 = "line 37";
  private static final String TRUNCATED_LINE_3 = "line 3";

  private TailerImpl tailer;


  public void test_create() {
    assertEquals(0, tailer.getLineNumber());
    assertEquals(0, tailer.getLines().size());
  }


  public void test_addLine() {
    tailer.addLine(LINE_1);
    tailer.addLine(LINE_2);
    tailer.addLine(LINE_3);

    assertEquals(3, tailer.getLineNumber());
    final List lines = tailer.getLines();
    assertEquals(MAX_LINE_COUNT, lines.size());
    assertEquals(LINE_2, ((TailLine)lines.get(0)).getLine());
    assertEquals(TRUNCATED_LINE_3, ((TailLine)lines.get(1)).getLine());
  }


  protected void setUp() throws Exception {
    super.setUp();
    tailer = new TailerImpl(TAIL_BUFFER_SIZE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestTailerImpl.class);
  }


  public SATestTailerImpl(final String s) {
    super(s);
  }
}
