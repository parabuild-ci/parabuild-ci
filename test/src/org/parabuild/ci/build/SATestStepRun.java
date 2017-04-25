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

import java.util.*;
import junit.framework.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;

/**
 *
 */
public class SATestStepRun extends TestCase {

  private static final Log log = LogFactory.getLog(SATestStepRun.class);


  private StepRun stepRun = null;


  public SATestStepRun(final String s) {
    super(s);
  }


  public void test_getDuration() throws Exception {
    final long startedAt = System.currentTimeMillis();
    final long finishedAt = startedAt + 10 * 1000;
    stepRun.setStartedAt(new Date(startedAt));
    stepRun.setFinishedAt(new Date(finishedAt));
    assertEquals(10, stepRun.getDuration());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestStepRun.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    stepRun = new StepRun();
  }
}
