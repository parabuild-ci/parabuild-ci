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

import java.text.*;
import java.util.*;
import org.apache.commons.logging.*;

import junit.framework.*;

/**
 *
 */
public class SATestBuildRunActionVO extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestBuildRunActionVO.class);

  private final Date TEST_DATE = new Date();
  private final SimpleDateFormat TEST_DATE_TIME_FORMAT = new SimpleDateFormat("MM/DD/yyyy");
  private BuildRunActionVO braVO;


  public void test_getDateAsString() {
    braVO.setDate(TEST_DATE);
    final String dateAsString = braVO.getDateAsString(TEST_DATE_TIME_FORMAT);
    if (log.isDebugEnabled()) log.debug("dateAsString: " + dateAsString);
  }


  protected void setUp() throws Exception {
    super.setUp();
    braVO = new BuildRunActionVO();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestBuildRunActionVO.class);
  }


  public SATestBuildRunActionVO(final String s) {
    super(s);
  }
}
