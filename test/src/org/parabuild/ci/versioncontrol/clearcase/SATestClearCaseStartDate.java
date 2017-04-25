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
package org.parabuild.ci.versioncontrol.clearcase;

import junit.framework.TestCase;

/**
 * ClearCaseStartDate Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>09/16/2008</pre>
 */
public final class SATestClearCaseStartDate extends TestCase {

  private ClearCaseStartDate clearCaseStartDate = null;

  public SATestClearCaseStartDate(String s) {
    super(s);
  }

  public void testGetValue() throws Exception {
    assertNotNull(clearCaseStartDate.getValue());
    assertNotNull(ClearCaseStartDate.parse(clearCaseStartDate.getValue()));
  }


  public void testParse() throws Exception {
    assertNotNull(ClearCaseStartDate.parse("2008-12-31"));
  }


  public void testToString() {
    assertNotNull(clearCaseStartDate.toString());
  }

  protected void setUp() throws Exception {
    super.setUp();
    clearCaseStartDate = new ClearCaseStartDate();
  }
}
