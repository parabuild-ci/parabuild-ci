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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebResponse;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.NullOutputStream;
import org.parabuild.ci.webui.common.WebuiUtils;

/**
 * Tests StatisticsImageServlet
 *
 * @see org.parabuild.ci.webui.StatisticsImageServlet
 * @see org.parabuild.ci.webui.common.MonthToDateImageBuildStatisticsPanel
 */
public final class SSTestStatisticsImageServlet extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestStatisticsImageServlet.class);
  public static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;


  public void test_imageBuildMonthToDateResponceOK() throws Exception {
    InputStream inputStream = null;
    try {
      final WebResponse webResponse = TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
        StatisticsImageServlet.STATISTICS_BUILD_IMAGE_MONTH_TO_DATE), null);
      inputStream = webResponse.getInputStream();
      IoUtils.copyInputToOuputStream(inputStream, new NullOutputStream());
    } finally {
      IoUtils.closeHard(inputStream);
    }
  }


  public void test_imageBuildYearToDateResponceOK() throws Exception {
    InputStream inputStream = null;
    final WebResponse webResponse = TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
      StatisticsImageServlet.STATISTICS_BUILD_IMAGE_YEAR_TO_DATE), null);
    inputStream = webResponse.getInputStream();
    IoUtils.copyInputToOuputStream(inputStream, new NullOutputStream());
  }


  public void test_imageFailsOnUnknownStatsType() throws Exception {
    try {
      TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
        99999), null);
      TestHelper.failNoExceptionThrown();
    } catch (HttpException e) {
    }
  }


  public void test_imageTestMonthToDateResponceOK() throws Exception {
    InputStream inputStream = null;
    try {
      final WebResponse webResponse = TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
        StatisticsImageServlet.STATISTICS_TESTS_IMAGE_MONTH_TO_DATE), null);
      inputStream = webResponse.getInputStream();
      IoUtils.copyInputToOuputStream(inputStream, new NullOutputStream());
    } finally {
      IoUtils.closeHard(inputStream);
    }
  }


  public void test_imageTestYearToDateResponceOK() throws Exception {
    InputStream inputStream = null;
    try {
      final WebResponse webResponse = TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
        StatisticsImageServlet.STATISTICS_TESTS_IMAGE_YEAR_TO_DATE), null);
      inputStream = webResponse.getInputStream();
      IoUtils.copyInputToOuputStream(inputStream, new NullOutputStream());
    } finally {
      IoUtils.closeHard(inputStream);
    }
  }


  public void test_recentBuildTimesImageResponceOK() throws Exception {
    InputStream inputStream = null;
    try {
      final WebResponse webResponse = TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
        StatisticsImageServlet.STATISTICS_RECENT_BUILD_TIMES_IMAGE), null);
      inputStream = webResponse.getInputStream();
      IoUtils.copyInputToOuputStream(inputStream, new NullOutputStream());
    } finally {
      IoUtils.closeHard(inputStream);
    }
  }


  public void test_recentPMDViolationsImageResponceOK() throws Exception {
    InputStream inputStream = null;
    try {
      final WebResponse webResponse = TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
        StatisticsImageServlet.STATISTICS_PMD_IMAGE_RECENT_BUILDS), null);
      inputStream = webResponse.getInputStream();
      IoUtils.copyInputToOuputStream(inputStream, new NullOutputStream());
    } finally {
      IoUtils.closeHard(inputStream);
    }
  }


  public void test_recentCheckstyleViolationsImageResponceOK() throws Exception {
    InputStream inputStream = null;
    try {
      final WebResponse webResponse = TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
        StatisticsImageServlet.STATISTICS_CHECKSTYLE_IMAGE_RECENT_BUILDS), null);
      inputStream = webResponse.getInputStream();
      IoUtils.copyInputToOuputStream(inputStream, new NullOutputStream());
    } finally {
      IoUtils.closeHard(inputStream);
    }
  }


  public void test_recentFindbugsViolationsImageResponceOK() throws Exception {
    InputStream inputStream = null;
    try {
      final WebResponse webResponse = TestHelper.assertPageSmokes(WebuiUtils.makeStatisticsChartURL(TEST_BUILD_ID,
        StatisticsImageServlet.STATISTICS_FINDBUGS_IMAGE_RECENT_BUILDS), null);
      inputStream = webResponse.getInputStream();
      IoUtils.copyInputToOuputStream(inputStream, new NullOutputStream());
    } finally {
      IoUtils.closeHard(inputStream);
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestStatisticsImageServlet.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
  }


  protected void tearDown() throws Exception {
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
    super.tearDown();
  }


  public SSTestStatisticsImageServlet(final String s) {
    super(s);
  }
}
