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
package org.parabuild.ci.object;

import junit.framework.*;


public class SATestBuildSequence extends TestCase {


  private static final String TEST_EMPTY_PATTERN_1 = "";
  private static final String TEST_EMPTY_PATTERN_2 = "\n";
  private static final String TEST_EMPTY_PATTERN_3 = "\n\n";
  private static final String TEST_EMPTY_PATTERN_4 = "\n\r";
  private static final String TEST_EMPTY_PATTERN_5 = "\n \r";

  private BuildSequence sequence = null;


  public void test_successPatternIsEmpty() {
    assertSuccessPattern(TEST_EMPTY_PATTERN_1);
    assertSuccessPattern(TEST_EMPTY_PATTERN_2);
    assertSuccessPattern(TEST_EMPTY_PATTERN_3);
    assertSuccessPattern(TEST_EMPTY_PATTERN_4);
    assertSuccessPattern(TEST_EMPTY_PATTERN_5);
  }


  public void test_failurePatternIsEmpty() {
    assertFailurePattern(TEST_EMPTY_PATTERN_1);
    assertFailurePattern(TEST_EMPTY_PATTERN_2);
    assertFailurePattern(TEST_EMPTY_PATTERN_3);
    assertFailurePattern(TEST_EMPTY_PATTERN_4);
    assertFailurePattern(TEST_EMPTY_PATTERN_5);
  }


  private void assertSuccessPattern(final String pattern) {
    sequence.setSuccessPatterns(pattern);
    assertTrue(sequence.successPatternIsEmpty());
  }


  private void assertFailurePattern(final String pattern) {
    sequence.setFailurePatterns(pattern);
    assertTrue(sequence.failurePatternIsEmpty());
  }


  protected void setUp() throws Exception {
    super.setUp();
    sequence = new BuildSequence();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestBuildSequence.class);
  }


  public SATestBuildSequence(final String s) {
    super(s);
  }
}
