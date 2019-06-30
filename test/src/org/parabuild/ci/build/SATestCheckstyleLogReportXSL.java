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
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.IoUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 */
public class SATestCheckstyleLogReportXSL extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestCheckstyleLogReportXSL.class);

  private static final File TEST_CHECKSTYLE_ERRORS_FILE = new File(TestHelper.getTestDataDir(), "test_checkstyle_errors.xml");


  /**
   * Tests that makeStringBuffer returns usable XSL.
   */
  public void test_makeStringBuffer() throws IOException, TransformerException {
    StringReader stringReader = null;
    try {

      // create test XML
      final StreamSource streamSource = new StreamSource(TEST_CHECKSTYLE_ERRORS_FILE);

      // transform
      stringReader = new StringReader(IoUtils.getResourceAsString("checkstyle-report.xsl"));
      final Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(stringReader));
      final StringWriter stringWriter = new StringWriter(2000);
      transformer.transform(streamSource, new StreamResult(stringWriter));
      final String output = stringWriter.toString().trim();
      writeResultForReview(output);
      assertTrue(!output.isEmpty());
      assertTrue(output.indexOf("C:\\WORK\\mor2\\dev\\bt\\src\\viewtier\\autobuild\\object\\PublishedStepResult.java") > 0);
      assertTrue("Substring should be present", output.indexOf("145") > 0);
      assertTrue("Substring should be present", output.indexOf("42") > 0);
    } finally {
      IoUtils.closeHard(stringReader);
    }
  }


  private static void writeResultForReview(final String result) throws IOException {
    final FileWriter fw = new FileWriter(TestHelper.getTestTempDir() + "/" + SATestCheckstyleLogReportXSL.class.getName() + ".html");
    fw.write(result);
    IoUtils.closeHard(fw);
  }


  /**
   *
   */
  public SATestCheckstyleLogReportXSL(final String arg0) {
    super(arg0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestCheckstyleLogReportXSL.class);
  }


  /**
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    assertTrue(TEST_CHECKSTYLE_ERRORS_FILE.exists());
  }
}
