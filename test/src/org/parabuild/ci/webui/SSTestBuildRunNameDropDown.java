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

public final class SSTestBuildRunNameDropDown extends ServersideTestCase {

  public static final int LEADING_BUILD_RUN_ID = 10;
  public static final int DEPENDENT_BUILD_RUN_ID_1 = 11;
  public static final int DEPENDENT_BUILD_RUN_ID_2 = 12;


  public void test_createLeading() {
    final ParallelBuildRunListDropDown dd = new ParallelBuildRunListDropDown(ConfigurationManager.getInstance().getBuildRun(LEADING_BUILD_RUN_ID));
    assertEquals(3, dd.getItemCount());
  }


  public void test_createDependent() {
    final ParallelBuildRunListDropDown dd1 = new ParallelBuildRunListDropDown(ConfigurationManager.getInstance().getBuildRun(DEPENDENT_BUILD_RUN_ID_1));
    assertEquals(3, dd1.getItemCount());
    final ParallelBuildRunListDropDown dd2 = new ParallelBuildRunListDropDown(ConfigurationManager.getInstance().getBuildRun(DEPENDENT_BUILD_RUN_ID_2));
    assertEquals(3, dd2.getItemCount());
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestBuildRunNameDropDown.class);
  }


  public SSTestBuildRunNameDropDown(final String s) {
    super(s);
  }
}
