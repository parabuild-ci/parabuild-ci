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
package org.parabuild.ci.build.result;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AbstractResultHandlerTest;
import org.parabuild.ci.object.ResultConfig;

import java.io.IOException;

/**
 * Tests SingleFileResultHandler
 *
 * @see org.parabuild.ci.build.AbstractResultHandlerTest
 */
public class SSTestURLResultHandler extends AbstractResultHandlerTest {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestURLResultHandler.class); // NOPMD
  private URLResultHandler resultHandler = null;


  /**
   * @see org.parabuild.ci.build.AbstractResultHandlerTest#processResults
   */
  protected void processResults() throws IOException {
    this.resultHandler.process();
  }


  /**
   * Should return ID of result config to be used to configure
   * result handler.
   */
  protected int resultConfigID() {
    return 9; // URL config ID
  }


  /**
   * @return result type handler being tested
   */
  protected byte resultTypeBeingTested() {
    return ResultConfig.RESULT_TYPE_URL;
  }


  /**
   * Return a string to be found in search after calling
   * processResults.
   *
   * @return
   * @see org.parabuild.ci.build.AbstractResultHandlerTest - parent class that will call
   *      this method after calling processResults().
   * @see #processResults
   */
  protected String stringToBeFoundBySearch() throws IOException {
    return ""; // do not search for anything
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();

    // create handler
    this.resultHandler = new URLResultHandler(super.agent, super.buildRunConfig,
            super.remoteCheckoutDir + '/' + super.relativeBuildDir,
            super.resultConfig, stepRunID());
  }


  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestURLResultHandler.class,
            new String[]{
                    "test_process"
            });
  }


  public SSTestURLResultHandler(final String s) {
    super(s);
  }
}
