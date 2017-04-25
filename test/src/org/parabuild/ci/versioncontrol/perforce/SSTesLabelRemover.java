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
package org.parabuild.ci.versioncontrol.perforce;

import java.util.*;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.versioncontrol.LabelRemover;

/**
 */
public final class SSTesLabelRemover extends ServersideTestCase {

  private static final int TEST_BUILD_RUN = 4;

  private ErrorManager errorManager = null;
  private LabelRemover labelRemover = null;


  public void test_parse() throws Exception {
    // first run
    assertEquals("Number of removed labels", 1, labelRemover.removeOldLabels(TestHelper.TEST_P4_VALID_BUILD_ID));
    // second run
    assertEquals("Number of removed labels", 0, labelRemover.removeOldLabels(TestHelper.TEST_P4_VALID_BUILD_ID));
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();

    // alter build run
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.YEAR, -1);
    final BuildRun buildRun = cm.getBuildRun(TEST_BUILD_RUN);
    buildRun.setLabel("test_label_blah_blah"); // label
    buildRun.setLabelStatus(BuildRun.LABEL_SET); // label
    buildRun.setFinishedAt(calendar.getTime()); // finished a year ago
    cm.save(buildRun);

    // create remover
    labelRemover = new LabelRemover(new P4SourceControl(cm.getBuildConfiguration(TestHelper.TEST_P4_VALID_BUILD_ID)));
  }


  protected void tearDown() throws Exception {
    assertEquals("Number of errors", 0, errorManager.errorCount());
    super.tearDown();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTesLabelRemover.class);
  }


  public SSTesLabelRemover(final String s) {
    super(s);
  }
}
