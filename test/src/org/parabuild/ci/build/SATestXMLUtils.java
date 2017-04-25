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

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.XMLUtils;

/**
 * @author Kostya
 *
 */
public class SATestXMLUtils extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestXMLUtils.class);

  private static final String TEST_RESULT = TestHelper.getTestTempDir() + File.separator + "SATestXMLUtils.xml";
  private static final File TEST_RESULT_FILE = new File(TEST_RESULT);
  private static final File TEST_JUNIT_LOGS_DIR = new File(TestHelper.getTestDataDir(), "junit_xml_logs");
  private static final File TEST_FILE = new File(TestHelper.getTestDataDir() + File.separator + "junit_xml_logs" + File.separator + "TEST-test.common.SATestIOUtils.xml");


  public void test_createDomDocument() throws ParserConfigurationException {
    final Document objDomDocument = XMLUtils.createDomDocument();
    assertNotNull(objDomDocument);
  }


  public void test_parseAndWriteDomDocument() throws SAXException, ParserConfigurationException, IOException {
    // make sure that returned result is not null
    final Document document = XMLUtils.parseDom(TEST_FILE, false);
    assertNotNull(document);

    final Element elem = document.createElement("unittest");
    document.appendChild(elem);
    XMLUtils.writeDom2File(document, TEST_RESULT);

    // make sure that file created by writeDom2File exists
    TestHelper.assertExists(TEST_RESULT);

    // and make sure that file created by writeDom2File has non-zero length
    if (log.isDebugEnabled()) log.debug("new File(TEST_RESULT): " + new File(TEST_RESULT));
    if (log.isDebugEnabled()) log.debug("new File(TEST_RESULT).length(): " + new File(TEST_RESULT).length());
    assertTrue(TEST_RESULT_FILE.length() > 0);
  }


  public void test_parseDomFailsIfFileDoesNotExist() throws SAXException, ParserConfigurationException, IOException {
    try {
      XMLUtils.parseDom(new File("blah-blah-never-existed"), false);
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
      // expected to be thrown
    }
  }


  public Document test_merge() throws ParserConfigurationException, IOException, SAXException {
    final List testFileList = Arrays.asList(TEST_JUNIT_LOGS_DIR.listFiles());
    final Document mergedDocument = XMLUtils.merge(testFileList, "testsuites");
    assertNotNull(mergedDocument);
    return mergedDocument;
  }


  public void test_writeDom2File() throws ParserConfigurationException, IOException, SAXException {
    // merge
    final Document mergedDocument = test_merge();
    // write
    XMLUtils.writeDom2File(mergedDocument, TEST_RESULT_FILE);
    assertTrue(TEST_RESULT_FILE.exists());
    assertTrue(TEST_RESULT_FILE.length() > 0);
  }


  public void test_intValueOf() throws ParserConfigurationException, IOException, SAXException, JaxenException {

    // get document
    final Document mergedDocument = test_merge();

    // analize
    final int errors = XMLUtils.intValueOf(mergedDocument, "sum(/testsuites/testsuite/@errors)");
    final int failures = XMLUtils.intValueOf(mergedDocument, "sum(/testsuites/testsuite/@failures)");
    final int tests = XMLUtils.intValueOf(mergedDocument, "sum(/testsuites/testsuite/@tests)");
    assertEquals(0, errors);
    assertEquals(2, failures);
    assertEquals(15, tests);
  }


  /**
   *
   */
  public SATestXMLUtils(final String arg0) {
    super(arg0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestXMLUtils.class);
  }


  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    IoUtils.deleteFileHard(new File(TEST_RESULT));
    IoUtils.deleteFileHard(TEST_RESULT_FILE);
    assertTrue(TEST_FILE.exists());
    assertTrue(TEST_JUNIT_LOGS_DIR.exists());
  }


  /**
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
