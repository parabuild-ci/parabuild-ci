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

import junit.framework.*;

import org.parabuild.ci.object.*;

/**
 *
 */
public final class SATestStarTeamEndOfLineDropDown extends TestCase {

  private StarTeamEndOfLineDropDown dropDown = null;


  public void test_defaultValue() {
    assertEquals(SourceControlSetting.STARTEAM_EOL_ON, dropDown.getCode());
  }


  /**
   * Required by JUnit
   */
  public SATestStarTeamEndOfLineDropDown(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    dropDown = new StarTeamEndOfLineDropDown();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestStarTeamEndOfLineDropDown.class);
  }
}
