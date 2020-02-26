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
package org.parabuild.ci.versioncontrol.perforce;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.SystemProperty;

import java.util.List;
import java.util.regex.Pattern;

/**
 */
public final class SATestP4IgnorePattern extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log LOG = LogFactory.getLog(SATestP4IgnorePattern.class); // NOPMD


  public void testIgnorePattern() {
    final List list = StringUtils.multilineStringToList(SystemProperty.DEFAULT_RETRY_VCS_COMMAND_PATTERNS);
    boolean found = false;
    for (int i = 0; i < list.size() && !found; i++) {
      final String pattern = (String) list.get(i);
      found = Pattern.compile(pattern).matcher(" chmod: C:\\Documents and Settings\\srvParabuildHome\\p4tickets.txt: Access is denied ").find();
    }
    assertTrue(found);
  }


  public SATestP4IgnorePattern(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}