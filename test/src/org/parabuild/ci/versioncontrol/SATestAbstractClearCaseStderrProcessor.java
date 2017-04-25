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

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;

/**
 * Tests AbstractClearCaseStderrProcessor
 */
public final class SATestAbstractClearCaseStderrProcessor extends TestCase {

  public void test_ProcessLine() throws Exception {
    final AbstractClearCaseStderrProcessor processor = new AbstractClearCaseStderrProcessor("Cleartool: Error: Unable to load") {
      protected int doProcessLine(final int index, final String line) {
        return RESULT_ADD_TO_ERRORS;
      }
    };
    assertEquals(AbstractClearCaseStderrProcessor.RESULT_IGNORE, processor.processLine(0, "Cleartool: Error: Unable to load"));
    assertEquals(AbstractClearCaseStderrProcessor.RESULT_ADD_TO_ERRORS, processor.processLine(0, "blah"));
  }


  public void test_ProcessTwoLines() throws Exception {
    final AbstractClearCaseStderrProcessor processor = new AbstractClearCaseStderrProcessor("cleartool: Warning: Unable to resolve symlink") {
      protected int doProcessLine(final int index, final String line) {
        return RESULT_ADD_TO_ERRORS;
      }
    };
    assertEquals(AbstractClearCaseStderrProcessor.RESULT_IGNORE, processor.processLine(0, "cleartool: Warning: Unable to resolve symlink \"lis.h\". The symlink target will not be loaded."));
  }


  public SATestAbstractClearCaseStderrProcessor(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestAbstractClearCaseStderrProcessor.class, new String[]{
    });
  }
}
