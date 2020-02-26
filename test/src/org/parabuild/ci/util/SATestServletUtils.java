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
package org.parabuild.ci.util;

import junit.framework.*;

/**
 * Tests BuildVersionGenerator
 */
public class SATestServletUtils extends TestCase {

  public SATestServletUtils(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_requestIsLocal() throws Exception {
    final String prefix = "172.";
    for (int i = 17; i <= 32; i++) {
      final String address = prefix + Integer.toString(i) + ".1.1";
      assertTrue("Request  should be marked local for address " + address, ServletUtils.requestIsLocal(address));
    }
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestServletUtils.class);

  }
}
