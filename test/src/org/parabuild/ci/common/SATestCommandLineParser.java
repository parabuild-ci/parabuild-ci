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
package org.parabuild.ci.common;

import junit.framework.*;
import org.apache.commons.logging.*;

/**
 * Tests CommandLineParser
 */
public final class SATestCommandLineParser extends TestCase {

  private static final Log log = LogFactory.getLog(CommandLineParser.class);
  private static final String TEST_COMMAND_PATH_WITH_SPACES = "C:\\Program Files\\WinCVS\\cvs.exe";
  private static final String DOUBLE_QUOTE = "\"";
  private static final String DOUBLE_DOUBLE_QUOTE = "\"\"";
  private static final String SINGLE_QUOTE = "\'";
  private static final String TEST_COMMAND_PARAM = "test_commands_param";

  private CommandLineParser parser;


  public SATestCommandLineParser(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_parsesCommandInDoubleQuotes() throws Exception {
    assertEquals(TEST_COMMAND_PATH_WITH_SPACES, parser.parse(DOUBLE_QUOTE + TEST_COMMAND_PATH_WITH_SPACES + DOUBLE_QUOTE)[0]);
  }


  /**
   *
   */
  public void test_parsesCommandInDoubleDoubleQuotes() throws Exception {
    final String[] strings = parser.parse("sh /c \"\"test_test \"a\"\"\"  b");
    for (int i = 0; i < strings.length; i++) {
      final String string = strings[i];
      if (log.isDebugEnabled()) log.debug("string: " + string);
    }
  }


  /**
   *
   */
  public void test_pasesCommandInSingleQuotes() throws Exception {
    assertEquals(TEST_COMMAND_PATH_WITH_SPACES, parser.parse(SINGLE_QUOTE + TEST_COMMAND_PATH_WITH_SPACES + SINGLE_QUOTE)[0]);
    assertEquals(TEST_COMMAND_PARAM, parser.parse(SINGLE_QUOTE + TEST_COMMAND_PATH_WITH_SPACES + SINGLE_QUOTE + ' ' + TEST_COMMAND_PARAM)[1]);
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new CommandLineParser();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestCommandLineParser.class);
  }
}
