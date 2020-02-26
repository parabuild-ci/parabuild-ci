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
package org.parabuild.ci.webui;

import java.io.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import com.meterware.httpunit.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.archive.*;
import org.parabuild.ci.archive.internal.*;
import org.parabuild.ci.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.services.*;

/**
 * Tests HTMLLogServlet
 */
public class SSTestHTMLLogServlet extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestHTMLLogServlet.class);
  public static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;

  protected ArchiveManager archiveManager;
  public static final String TEST_LOG_PATH = "com/meterware/servletunit/InvocationContext.html";


  public SSTestHTMLLogServlet(final String s) {
    super(s);
  }


  public void test_returnsIndexForHTMLFileLog() throws Exception {
    validate_returnsIndexForHTMLFileLog();
  }


  public void test_returnsArchivedIndexForHTMLFileLog() throws Exception {
    final ArchiveCompressor packingHandler = new ArchiveCompressor(TEST_BUILD_ID, archiveManager.getBuildLogDir(), "");
    packingHandler.forceCutOffTimeMillis(System.currentTimeMillis());
    packingHandler.compressExpiredArchiveEntities();
    validate_returnsIndexForHTMLFileLog();
  }


  public void test_returnsIndexForHTMLDir() throws Exception {
    validate_returnsIndexForHTMLDir();
  }


  public void test_returnsArchivedIndexForHTMLDir() throws Exception {
    final ArchiveCompressor packingHandler = new ArchiveCompressor(TEST_BUILD_ID, archiveManager.getBuildLogDir(), "");
    packingHandler.forceCutOffTimeMillis(System.currentTimeMillis());
    packingHandler.compressExpiredArchiveEntities();
    validate_returnsIndexForHTMLDir();
  }


  /**
   */
  private void validate_returnsIndexForHTMLFileLog() throws Exception {
    // preExecute - create a file in achive
    final StepLog stepLog = ConfigurationManager.getInstance().getStepLog(5);
    final File archiveFileDir = archiveManager.getArchivedLogHome(stepLog);
    final File fullPath = new File(archiveFileDir, stepLog.getPath());
    IoUtils.deleteFileHard(fullPath);
    fullPath.getParentFile().mkdirs();
    fullPath.getParentFile().setLastModified(System.currentTimeMillis() - 10000L);
    IoUtils.copyFile(TestHelper.getTestFile("test_html_log.html"), fullPath);
    TestHelper.assertExists(fullPath);

    // assert - check if returns from  file
    final String url = "http://localhost:" + ServiceManager.getInstance().getListenPort() + "/parabuild/build/log/html/1/5/test/log/test_html_log.html";
    final WebConversation wc = new WebConversation();
    final WebResponse resp = wc.getResponse(url);
    assertTrue(resp.getText().indexOf("PMD") >= 0);
  }


  /**
   */
  private void validate_returnsIndexForHTMLDir() throws Exception {
    // preExecute - create a directory in achive
    final StepLog stepLog = ConfigurationManager.getInstance().getStepLog(6);
    final File archiveFileDir = archiveManager.getArchivedLogHome(stepLog);
    archiveFileDir.mkdirs();
    IoUtils.deleteFileHard(archiveFileDir);
    IoUtils.copyDirectory(TestHelper.getTestFile("html_log"), archiveFileDir);
    archiveFileDir.setLastModified(System.currentTimeMillis() - 10000L);
    TestHelper.assertExists(archiveFileDir);
    TestHelper.assertExists(new File(archiveFileDir, TEST_LOG_PATH));

    // assert
    final String url = "http://localhost:" + ServiceManager.getInstance().getListenPort() + "/parabuild/build/log/html/1/6/" + TEST_LOG_PATH;
    final WebConversation wc = new WebConversation();
    final WebResponse resp = wc.getResponse(url);
    assertTrue(resp.getText().indexOf("pushForwardRequest") >= 0);

    // REVIEWME: simeshev@parabuilci.org -> add test that can not read file outside of log home.
  }


  protected void setUp() throws Exception {
    super.setUp();
    archiveManager = ArchiveManagerFactory.getArchiveManager(TEST_BUILD_ID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestHTMLLogServlet.class);
  }
}
