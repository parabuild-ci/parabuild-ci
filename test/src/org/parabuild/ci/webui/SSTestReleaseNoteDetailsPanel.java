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
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests ReleaseNoteDetailsPanel
 */
public final class SSTestReleaseNoteDetailsPanel extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private ReleaseNoteDetailsPanel panel;


  public SSTestReleaseNoteDetailsPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_create() throws Exception {
    // created in setUp method
  }


  protected void setUp() throws Exception {
    super.setUp();
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    panel = new ReleaseNoteDetailsPanel((Issue)cm.getObject(Issue.class, 1));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestReleaseNoteDetailsPanel.class);
  }
}
