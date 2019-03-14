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
import java.net.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.archive.internal.ArchiveCompressor;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.services.ServiceManager;

/**
 * Tests ResultDownloadServlet and ResultsPage
 *
 * @see org.parabuild.ci.webui.ResultDownloadServlet
 * @see org.parabuild.ci.webui.ResultsPage
 */
public class SSTestResultDownloadServlet extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestResultDownloadServlet.class);
  public static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;

  protected ArchiveManager am = null;
  private ConfigurationManager cm = null;
  private ErrorManager em;
  private static final String TEST__BUILD__RESULT = "test_html_log.html";


  public SSTestResultDownloadServlet(final String s) {
    super(s);
  }


  public void test_singleFileResultAvailable() throws Exception {
    createFileInArchive();
    validate_singleFileResultAvailable();
  }


  public void test_compressedSingleFileResultAvailable() throws Exception {
    createDirectoryInArchive();
    final ArchiveCompressor compressor = new ArchiveCompressor(TEST_BUILD_ID, am.getResultDir(), "");
    compressor.forceCutOffTimeMillis(System.currentTimeMillis());
    compressor.compressExpiredArchiveEntities();
    validate_singleFileResultAvailable();
  }


  public void test_returnsDirResultAvaibale() throws Exception {
    createDirectoryInArchive();
    validate_returnsDirResultAvaibale();
  }


  public void test_compressedDirResultAvaibale() throws Exception {
    final ArchiveCompressor compressor = new ArchiveCompressor(TEST_BUILD_ID, am.getResultDir(), "");
    compressor.forceCutOffTimeMillis(System.currentTimeMillis());
    compressor.compressExpiredArchiveEntities();
    validate_returnsDirResultAvaibale();
  }


  /**
   * @see org.parabuild.ci.webui.ResultDownloadServlet
   * @see org.parabuild.ci.webui.ResultsPage
   */
  private void validate_singleFileResultAvailable() throws Exception {
    createFileInArchive();

    // assert - check if returns from  file
    final String url = "http://localhost:" + managerListenPort() + "/parabuild/build/result/1/0/" + TEST__BUILD__RESULT;
    TestHelper.assertPageSmokes(new URL(url), "PMD", true);

    // also check the results list page
    TestHelper.assertPageSmokes("/parabuild/build/results.htm?buildrunid=1", "test_html_log.html");
  }


  private void createFileInArchive() throws IOException {
    final StepResult stepResult = cm.getStepResult(0);
    final File archiveFileDir = am.getArchivedResultHome(stepResult);
    final File fullPath = new File(archiveFileDir, TEST__BUILD__RESULT);
    IoUtils.deleteFileHard(fullPath.getParentFile());
    fullPath.getParentFile().mkdirs();
    fullPath.getParentFile().setLastModified(System.currentTimeMillis() - 10000L);
    // NOTE: 01/16/2004 - vimeshev - we use the test data file below
    // because we know it's content.
    IoUtils.copyFile(TestHelper.getTestFile(TEST__BUILD__RESULT), fullPath);
    TestHelper.assertExists(fullPath);
  }


  private int managerListenPort() {
    return ServiceManager.getInstance().getListenPort();
  }


  /**
   * @see org.parabuild.ci.webui.ResultDownloadServlet
   * @see org.parabuild.ci.webui.ResultsPage
   */
  private void validate_returnsDirResultAvaibale() throws Exception {
    // assert
    final String url = "http://localhost:" + managerListenPort() + "/parabuild/build/result/1/1/help-doc.html";
    TestHelper.assertPageSmokes(new URL(url), "and nested interface has its own separate page", true);

    // also check the results list page
    TestHelper.assertPageSmokes("/parabuild/build/results.htm?buildrunid=1", "overview-tree.html");

    // REVIEWME: simeshev@parabuilci.org -> add test that can not read file outside of result home.
  }


  private void createDirectoryInArchive() throws IOException {
    final StepResult stepResult = cm.getStepResult(1);
    final File archiveFileDir = am.getArchivedResultHome(stepResult);
    archiveFileDir.mkdirs();
    IoUtils.deleteFileHard(archiveFileDir);
    IoUtils.copyDirectory(TestHelper.getTestFile("html_log"), archiveFileDir);
    archiveFileDir.setLastModified(System.currentTimeMillis() - 10000L);
    TestHelper.assertExists(archiveFileDir);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    am = ArchiveManagerFactory.getArchiveManager(TEST_BUILD_ID);
    cm = ConfigurationManager.getInstance();
    em = ErrorManagerFactory.getErrorManager();
    em.clearAllActiveErrors();
  }


  protected void tearDown() throws Exception {
    assertEquals(0, em.errorCount());
    super.tearDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestResultDownloadServlet.class);
  }
}
