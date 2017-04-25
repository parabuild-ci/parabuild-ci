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
package org.parabuild.ci.build;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.XMLUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 */
public class SATestPMDLogReportXSL extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestPMDLogReportXSL.class);

  private static final File TEST_PMD_LOGS_FILE = new File(TestHelper.getTestDataDir(), "test_pmd_report.xml");


  /**
   * Tests that makeStringBuffer returns usable XSL.
   */
  public void test_makeStringBuffer() throws ParserConfigurationException, IOException, SAXException, TransformerException {
    StringReader stringReader = null;
    try {

      // create test XML
      final Document mergedDocument = XMLUtils.parseDom(TEST_PMD_LOGS_FILE, false);
      assertNotNull(mergedDocument);

      // transform
      stringReader = new StringReader(IoUtils.getResourceAsString("pmd-report.xsl"));
      final Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(stringReader));
      final StringWriter stringWriter = new StringWriter(500);
      transformer.transform(new DOMSource(mergedDocument), new StreamResult(stringWriter));
      final String output = stringWriter.toString().trim();
      assertTrue(output.length() > 0);
      assertTrue(output.indexOf("AvoidReassigningParametersRule") > 0);
      assertTrue("Substring should be present", output.indexOf("72") > 0);
    } finally {
      IoUtils.closeHard(stringReader);
    }
  }


  /**
   *
   */
  public SATestPMDLogReportXSL(final String arg0) {
    super(arg0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestPMDLogReportXSL.class);
  }


  /**
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    assertTrue(TEST_PMD_LOGS_FILE.exists());
  }
}
