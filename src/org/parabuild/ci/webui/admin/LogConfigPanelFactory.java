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

/**
 * Factory class to create log config panels.
 */
public final class LogConfigPanelFactory {

  /**
   * Constructor. This constructor is private to forbid instantiation
   * LogConfigPanelFactory.
   *
   * @see #makeLogConfigPanel
   */
  private LogConfigPanelFactory() {
  }


  /**
   * Creates an instance impelemntion abstract log config panel
   * according to the type of log presented in logConfig.
   *
   * @see AbstractLogConfigPanel
   */
  public static AbstractLogConfigPanel makeLogConfigPanel(final int logType) {
    AbstractLogConfigPanel result = null;
    switch (logType) {
      case LogConfig.LOG_TYPE_TEXT_DIR:
        result = new TextDirLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_TEXT_FILE:
        result = new TextFileLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_JNUIT_XML_DIR:
        result = new JUnitLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_NUNIT_XML_DIR:
        result = new NUnitLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_PMD_XML_FILE:
        result = new PMDLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_FINDBUGS_XML_FILE:
        result = new FindbugsLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_HTML_FILE:
        result = new HTMLFileLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_HTML_DIR:
        result = new HTMLDirLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_CPPUNIT_XML_DIR:
        result = new CppUnitLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_CHECKSTYLE_XML_FILE:
        result = new CheckstyleLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_PHPUNIT_XML_DIR:
        result = new PHPUnitLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_UNITTESTPP_XML_DIR:
        result = new UnitTestPpLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_BOOST_TEST_XML_DIR:
        result = new BoostTestConfigPanel();
        break;
      case LogConfig.LOG_TYPE_GOOGLETEST_XML_FILE:
        result = new GoogleTestLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_SQUISH_TESTER_XML_FILE:
        result = new SquishLogConfigPanel();
        break;
      case LogConfig.LOG_TYPE_GENERIC_TEST_RESULT:
        result = new GenericTestResultConfigPanel();
        break;
      default:
        throw new IllegalArgumentException("Unknown log type code: " + logType);
    }
    return result;
  }
}
