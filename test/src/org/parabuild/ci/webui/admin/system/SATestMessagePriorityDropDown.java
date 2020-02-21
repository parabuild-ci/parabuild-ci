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
package org.parabuild.ci.webui.admin.system;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.parabuild.ci.object.SystemProperty;

public final class SATestMessagePriorityDropDown extends TestCase {

  private MessagePriorityDropDown messagePriorityDropDown;


  public void test_createSetsCorrectDefault() {
    assertEquals(SystemProperty.MESSAGE_PRIORITY_NORMAL, messagePriorityDropDown.getCode());
  }


  protected void setUp() throws Exception {
    super.setUp();
    messagePriorityDropDown = new MessagePriorityDropDown();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestMessagePriorityDropDown.class);
  }


  public SATestMessagePriorityDropDown(final String s) {
    super(s);
  }
}
