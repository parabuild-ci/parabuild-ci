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
package org.parabuild.ci.remote;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SATestAgentVersionParserRegex
 * <p/>
 *
 * @author Slava Imeshev
 * @since May 26, 2009 11:16:25 AM
 */
public final class SATestAgentVersionParserRegex extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Logger LOG = Logger.getLogger(SATestAgentVersionParserRegex.class); // NOPMD


  public SATestAgentVersionParserRegex(String s) {
    super(s);
  }


  public void testRegex() {
    final Matcher matcher = Pattern.compile(AgentManager.AGENT_VERSION_PARSER_REGEX).matcher("Parabuild 4.0.0  build 1658");
    assertTrue(matcher.find());
    assertEquals(2, matcher.groupCount());
    final String stringMajor = matcher.group(1);
    assertEquals("4", stringMajor);
    final String build = matcher.group(2);
    assertEquals("1658", build);
  }
}
