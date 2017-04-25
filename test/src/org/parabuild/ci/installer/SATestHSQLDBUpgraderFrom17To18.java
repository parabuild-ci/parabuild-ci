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
package org.parabuild.ci.installer;

import junit.framework.TestCase;

/**
 * HSQLDBUpgraderFrom17To18 Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>12/26/2008</pre>
 */
public final class SATestHSQLDBUpgraderFrom17To18 extends TestCase {

  private HSQLDBUpgraderFrom17To18 upgrader = null;


  public SATestHSQLDBUpgraderFrom17To18(String s) {
    super(s);
  }


  public void testToString() {
    assertNotNull(upgrader.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    upgrader = new HSQLDBUpgraderFrom17To18();
  }
}
