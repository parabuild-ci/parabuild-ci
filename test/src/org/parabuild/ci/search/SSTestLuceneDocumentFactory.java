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
package org.parabuild.ci.search;

import junit.framework.*;
import org.apache.lucene.document.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.*;

/**
 * Tests SSTestLuceneDocumentFactory
 */
public class SSTestLuceneDocumentFactory extends ServersideTestCase {

  public static final int TEST_CHANGE_LIST_ID = 1;
  public static final int TEST_BUILD_RUN_ID = 1;

  private ConfigurationManager cm;


  public SSTestLuceneDocumentFactory(final String s) {
    super(s);
  }


  /**
   * Tests getSearchQuery
   */
  public void test_makeChangeListDocument() throws Exception {
    // make document
    final Document document = LuceneDocumentFactory.makeDocument(cm.getBuildRun(TEST_BUILD_RUN_ID),
      cm.getChangeList(TEST_CHANGE_LIST_ID),
      cm.getChanges(TEST_CHANGE_LIST_ID));

    // asserts the document of the correct type
    final Field field = document.getField(LuceneDocumentFactory.FIELD_DOCUMENT_TYPE);
    assertEquals(field.stringValue(), LuceneDocumentFactory.TYPE_CHANGE_LIST);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestLuceneDocumentFactory.class);
  }
}
