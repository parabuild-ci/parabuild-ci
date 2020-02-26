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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.XMLUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 */
public class SATesCppUnitLogReportXSL extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATesCppUnitLogReportXSL.class);

  private static final File TEST_CPPUNIT_LOGS_DIR = new File(TestHelper.getTestDataDir(), "cppunit_xml_logs");


  /**
   * Tests that makeStringBuffer returns usable XSL.
   */
  public void test_makeStringBuffer() throws ParserConfigurationException, IOException, SAXException, TransformerException {
    StringReader stringReader = null;
    try {

      // create test XML
      final List testFileList = Arrays.asList(TEST_CPPUNIT_LOGS_DIR.listFiles());
      final Document mergedDocument = XMLUtils.merge(testFileList, CppUnitLogHandler.ARCHIVE_XML_ROOT);
      assertNotNull(mergedDocument);

      // transform
      stringReader = new StringReader(IoUtils.getResourceAsString("cppunit-log-report.xsl"));
      final Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(stringReader));
      final StringWriter stringWriter = new StringWriter(500);
      transformer.transform(new DOMSource(mergedDocument), new StreamResult(stringWriter));
      final String result = stringWriter.toString().trim();
      writeResultForReview(result);
      assertTrue(result.length() > 0);
      assertTrue(result.indexOf("CPhoneBookTestSuite::PhoneBookGetPHSStates") > 0);
    } finally {
      IoUtils.closeHard(stringReader);
    }
  }


  private void writeResultForReview(final String result) throws IOException {
    FileWriter fw = new FileWriter(TestHelper.getTestTempDir() + "/" + SATesCppUnitLogReportXSL.class.getName() + ".html");
    fw.write(result);
    IoUtils.closeHard(fw);
  }


  /**
   *
   */
  public SATesCppUnitLogReportXSL(final String arg0) {
    super(arg0);
  }


  /**
   * Required by CppUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATesCppUnitLogReportXSL.class);
  }


  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    assertTrue(SATesCppUnitLogReportXSL.TEST_CPPUNIT_LOGS_DIR.exists());
  }
}
