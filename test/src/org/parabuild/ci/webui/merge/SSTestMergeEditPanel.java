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
package org.parabuild.ci.webui.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.project.ProjectManager;

/**
 * Tests EditMergePanel
 */
public class SSTestMergeEditPanel extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestMergeEditPanel.class);

  private static final int TEST_PROJECT_ID = 1;
  private static final int TEST_MERGE_ID = 0;

  private EditMergePanel panel;


  public void test_loadProject() {
    panel.load(ProjectManager.getInstance().getProject(TEST_PROJECT_ID));
  }


  public void test_loadMerge() {
    final MergeConfiguration mergeConfiguration = MergeManager.getInstance().getMergeConfiguration(TEST_MERGE_ID);
    if (log.isDebugEnabled()) log.debug("merge: " + mergeConfiguration);
    panel.load(mergeConfiguration);
  }


  public SSTestMergeEditPanel(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestMergeEditPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    panel = new EditMergePanel();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
  }
}
