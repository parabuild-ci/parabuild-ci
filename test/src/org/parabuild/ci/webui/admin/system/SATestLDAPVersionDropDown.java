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
import org.parabuild.ci.configuration.ConfigurationConstants;

/**
 *
 */
public final class SATestLDAPVersionDropDown extends TestCase {

  private LDAPVersionDropDown dropDown = null;


  public void test_defaultValue() {
    assertEquals(ConfigurationConstants.LDAP_VERSION_DEFAULT, dropDown.getCode());
  }


  /**
   * Required by JUnit
   */
  public SATestLDAPVersionDropDown(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    dropDown = new LDAPVersionDropDown();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestLDAPVersionDropDown.class);
  }
}
