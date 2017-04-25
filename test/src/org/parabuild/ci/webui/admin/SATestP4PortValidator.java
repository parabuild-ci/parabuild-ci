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

import junit.framework.TestCase;

/**
 * P4PortValidator Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>06/06/2008</pre>
 */
public final class SATestP4PortValidator extends TestCase {

  private P4PortValidator validator = null;

  public SATestP4PortValidator(String s) {
    super(s);
  }


  public void testToString() {
    assertNotNull(validator.toString());
  }

  public void testValidate() {
    assertTrue(validator.validate("host:9999"));
    assertTrue(!validator.validate("host"));
    assertTrue(!validator.validate("9999"));
    assertTrue(!validator.validate(":"));
    assertTrue(!validator.validate(":9999"));
    assertTrue(!validator.validate("host:"));
  }

  protected void setUp() throws Exception {
    super.setUp();
    validator = new P4PortValidator();
  }
}
