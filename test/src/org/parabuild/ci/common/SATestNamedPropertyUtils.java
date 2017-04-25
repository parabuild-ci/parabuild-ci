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

import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.util.Collections;

/**
 * SATestNamedPropertyUtils
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jan 26, 2010 3:12:25 AM
 */
public final class SATestNamedPropertyUtils extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(SATestNamedPropertyUtils.class); // NOPMD
  private static final String SHOULD_NOT_CHANGE = "$/test/sourceline/alwaysvalid";


  public SATestNamedPropertyUtils(final String name) {
    super(name);
  }


  public void testReplaceProperties() throws Exception {
    assertEquals(SHOULD_NOT_CHANGE, NamedPropertyUtils.replaceProperties(SHOULD_NOT_CHANGE, Collections.EMPTY_MAP));
  }
}
