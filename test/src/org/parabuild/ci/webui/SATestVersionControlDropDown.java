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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.webui.admin.VersionControlDropDown;

/**
 */
public class SATestVersionControlDropDown extends TestCase {

  private VersionControlDropDown vcdd = null;


  public SATestVersionControlDropDown(final String s) {
    super(s);
  }


  /**
   */
  public void test_failsOnUnknownSCMCode() throws Exception {
    try {
      vcdd.setCode(Integer.MAX_VALUE);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  /**
   */
  public void test_acceptsReferenceType() throws Exception {
    vcdd.setCode(VersionControlSystem.SCM_REFERENCE);
    assertEquals(vcdd.getCode(), VersionControlSystem.SCM_REFERENCE);
  }


  /**
   */
  public void test_acceptsVSSType() throws Exception {
    vcdd.setCode(VersionControlSystem.SCM_VSS);
    assertEquals(vcdd.getCode(), VersionControlSystem.SCM_VSS);
  }


  /**
   */
  public void test_acceptsSVNType() throws Exception {
    vcdd.setCode(VersionControlSystem.SCM_SVN);
    assertEquals(vcdd.getCode(), VersionControlSystem.SCM_SVN);
  }


  protected void setUp() throws Exception {
    super.setUp();
    vcdd = new VersionControlDropDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestVersionControlDropDown.class);
  }
}
