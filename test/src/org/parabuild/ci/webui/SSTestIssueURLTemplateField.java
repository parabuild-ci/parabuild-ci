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
import org.parabuild.ci.webui.admin.*;

/**
 * Tests BuildHeaderPanel
 */
public class SSTestIssueURLTemplateField extends ServersideTestCase {

  private IssueURLTemplateField issueURLTemplateField;


  public SSTestIssueURLTemplateField(final String s) {
    super(s);
  }


  /**
   * Bug #534 - makes sure the field is big enough.
   */
  public void test_bug534() throws Exception {
    assertTrue(issueURLTemplateField.getMaxLength() >= 80);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestIssueURLTemplateField.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    issueURLTemplateField = new IssueURLTemplateField();
  }
}
