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

import org.apache.commons.logging.*;

import junit.framework.*;

/**
 *
 */
public class SATestIssueURLGenerator extends TestCase {

  private static final Log log = LogFactory.getLog(SATestIssueURLGenerator.class);

  private IssueURLGenerator issueURLGenerator = null;


  public SATestIssueURLGenerator(final String s) {
    super(s);
  }


  public void test_isTemplateValid() throws Exception {
    issueURLGenerator.setURLTemplate("http://bugzilla/bugs/bug_id=${issue.key}");
    assertTrue(issueURLGenerator.isTemplateValid());

    issueURLGenerator.setURLTemplate("http://bugzilla/bugs/bug_id=${issue.key}&test=a");
    assertTrue(issueURLGenerator.isTemplateValid());
  }


  public void test_isTemplateValidInvalidatesMisformedTemplates() throws Exception {
    issueURLGenerator.setURLTemplate("http://bugzilla/bugs/bug_id=${");
    assertTrue(!issueURLGenerator.isTemplateValid());

    issueURLGenerator.setURLTemplate("http://bugzilla/bugs/bug_id=${blah");
    assertTrue(!issueURLGenerator.isTemplateValid());
  }


  public void test_isTemplateValidInvalidatesNonExistingProperties() throws Exception {
    issueURLGenerator.setURLTemplate("http://bugzilla/bugs/bug_id=${isssssue.key}");
    assertTrue(!issueURLGenerator.isTemplateValid());
  }


  public void test_isTemplateValidInvalidatesMalformedURL() throws Exception {
    issueURLGenerator.setURLTemplate("://bugzilla/bugs/bug_id=${issue.key}");
    assertTrue(!issueURLGenerator.isTemplateValid());
  }


  public void test_generateIssueURL() throws Exception {

    issueURLGenerator.setURLTemplate("http://bugzilla/bugs/bug_id=${issue.key}");
    issueURLGenerator.setIssueKey("101");
    assertEquals("http://bugzilla/bugs/bug_id=101", issueURLGenerator.generateIssueURL());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestIssueURLGenerator.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    issueURLGenerator = new IssueURLGenerator();
  }
}
