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
package org.parabuild.ci.object;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * SATestStartParameter
 * <p/>
 *
 * @author Slava Imeshev
 * @since May 12, 2009 6:34:59 PM
 */
public final class SATestStartParameter extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Logger LOG = Logger.getLogger(SATestStartParameter.class); // NOPMD
  private StartParameter parameter;


  public SATestStartParameter(String s) {
    super(s);
  }


  public void setUp() {
    parameter = new StartParameter();

  }


  public void testGetFirstValue() {
    parameter.setValue("test1, test2");
    assertEquals("test1", parameter.getFirstValue());
  }
}
