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
package org.parabuild.ci.build;

import junit.framework.*;

/**
 * Tests BuildRunnerStateImpl
 */
public class SATestBuildRunnerStateImpl extends TestCase {

  public void test_create() throws Exception {
    // nothing happens with null params
    new BuildRunnerStateImpl(null, null);
  }


  public void test_set() throws Exception {
    final BuildRunnerStateImpl buildRunnerState = new BuildRunnerStateImpl(null, null);
    // nothing happens with null running step
    buildRunnerState.setCurrentlyRunningStep(null);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestBuildRunnerStateImpl.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SATestBuildRunnerStateImpl(final String s) {
    super(s);
  }
}
