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
package org.parabuild.ci.build.log;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * SSTestJUnitStatisticsProcessor
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jul 7, 2008 5:53:02 PM
 */
public final class SSTestJUnitStatisticsProcessor extends ServersideTestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log LOG = LogFactory.getLog(SSTestJUnitStatisticsProcessor.class); // NOPMD
  private static final String[] ZERO_LENGTH_STRING_ARRAY = new String[]{};
  private static final int ACTIVE_BUILD_ID = 1;
  private static final int BUILD_RUN_ID = 1;
  private static final int STEP_RUN_ID = 1;
  private JUnitStatisticsProcessor statisticsProcessor;


  public void testProcessMergedLog() throws IOException, ParserConfigurationException, SAXException {
    final Document document = XMLUtils.parseDom(TestHelper.getTestFile("test_junit_merged_log.xml"), false);
    statisticsProcessor.processMergedLog(document);
  }


  protected void setUp() throws Exception {
    super.setUp();
    statisticsProcessor = new JUnitStatisticsProcessor(ACTIVE_BUILD_ID, BUILD_RUN_ID, STEP_RUN_ID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestJUnitStatisticsProcessor.class,
            ZERO_LENGTH_STRING_ARRAY);
  }


  public SSTestJUnitStatisticsProcessor(final String s) {
    super(s);
  }
}
