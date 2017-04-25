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

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;

/**
 * Tests ResetPasswordPanel
 */
public final class SSTestResetPasswordPanel extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private ResetPasswordPanel panel;
  private static final String TEST_NAME = "test_name";


  public SSTestResetPasswordPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_create() throws Exception {
    // created in setUp method
  }


  /**
   *
   */
  public void test_setName() throws Exception {
    panel.setName(TEST_NAME);
    assertEquals(TEST_NAME, panel.getName());
  }


  protected void setUp() throws Exception {
    super.setUp();
    panel = new ResetPasswordPanel();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestResetPasswordPanel.class);
  }
}
