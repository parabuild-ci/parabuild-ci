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
package org.parabuild.ci.versioncontrol;

import java.util.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.gargoylesoftware.base.testing.OrderedTestSuite;

/**
 * Tests ClearCaseSetcsCommandStderrProcessor
 */
public final class SATestClearCaseSetcsCommandStderrProcessor extends TestCase {

  private static final byte RESULT_IGNORE = ClearCaseSetcsCommandStderrProcessor.RESULT_IGNORE;


  public void test_ProcessLine() throws Exception {
    final ClearCaseSetcsCommandStderrProcessor processor = new ClearCaseSetcsCommandStderrProcessor("cleartool: Warning: Unable to resolve symlink", new ArrayList());
    assertEquals(RESULT_IGNORE, processor.processLine(0, "cleartool: Warning: Unable to resolve symlink \"lis.h\". The symlink target will not be loaded."));
  }


  public SATestClearCaseSetcsCommandStderrProcessor(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestClearCaseSetcsCommandStderrProcessor.class, new String[]{
    });
  }
}
