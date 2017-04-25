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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * LogContentTypeDropDown shows a list of log content types
 */
public final class LogTypeDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 7210592889010278271L; // NOPMD


  public LogTypeDropDown() {
    addCodeNamePair(LogConfig.LOG_TYPE_BOOST_TEST_XML_DIR, "Boost Test XML Log Dir");
    addCodeNamePair(LogConfig.LOG_TYPE_CHECKSTYLE_XML_FILE, "Checkstyle XML report");
    addCodeNamePair(LogConfig.LOG_TYPE_CPPUNIT_XML_DIR, "CPPUnit XML log directory");
    addCodeNamePair(LogConfig.LOG_TYPE_HTML_DIR, "Directory with HTML files");
    addCodeNamePair(LogConfig.LOG_TYPE_TEXT_DIR, "Directory with text files");
    addCodeNamePair(LogConfig.LOG_TYPE_FINDBUGS_XML_FILE, "Findbugs XML report");
    addCodeNamePair(LogConfig.LOG_TYPE_GENERIC_TEST_RESULT, "Generic test result");
    addCodeNamePair(LogConfig.LOG_TYPE_GOOGLETEST_XML_FILE, "GoogleTest XML report");
    addCodeNamePair(LogConfig.LOG_TYPE_JNUIT_XML_DIR, "JUnit XML log directory");
    addCodeNamePair(LogConfig.LOG_TYPE_NUNIT_XML_DIR, "NUnit XML log directory");
    addCodeNamePair(LogConfig.LOG_TYPE_PHPUNIT_XML_DIR, "PHPUnit XML log directory");
    addCodeNamePair(LogConfig.LOG_TYPE_PMD_XML_FILE, "PMD XML log");
    addCodeNamePair(LogConfig.LOG_TYPE_SQUISH_TESTER_XML_FILE, "Squish XML report");
    addCodeNamePair(LogConfig.LOG_TYPE_HTML_FILE, "Single HTML file");
    addCodeNamePair(LogConfig.LOG_TYPE_TEXT_FILE, "Single text file");
    addCodeNamePair(LogConfig.LOG_TYPE_UNITTESTPP_XML_DIR, "UnitTest++ XML log directory");
    setCode(LogConfig.LOG_TYPE_TEXT_FILE);
  }
}
