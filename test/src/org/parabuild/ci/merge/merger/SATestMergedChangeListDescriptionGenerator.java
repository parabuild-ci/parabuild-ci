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
package org.parabuild.ci.merge.merger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * Tests MergedChangeListDescriptionGenerator
 */
public final class SATestMergedChangeListDescriptionGenerator extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestMergedChangeListDescriptionGenerator.class);

  private static final String TEST_DESCRIPTION_1 = "Test description";
  private static final String TEST_MARKER1 = "BT30_TO_BT";
  private static final String TEST_MARKER2 = "[BT31-TO-BT]";
  private static final String TEST_DESCRIPTION_2 = "Test description " + TEST_MARKER1;
  private static final String TEST_DESCRIPTION_3 = "Test description " + TEST_MARKER2;
  private static final String SOURCE_NUMBER = "999";
  private static final String TEST_USER = "test_user";
  private static final String EMPTY_MARKER = "";
  private static final String TEST_BRANCH_VIEW = "test_branch_view";

  private final MergedChangeListDescriptionGenerator generator;


  public void test_generate() {
    assertEquals("Test description (Automerge: Integed change list # 999 by test_user using  branch view " + TEST_BRANCH_VIEW + ")",
      generator.generateDescription(TEST_DESCRIPTION_1, SOURCE_NUMBER, TEST_USER, EMPTY_MARKER, false, TEST_BRANCH_VIEW, false));

    assertEquals("Test description  (Automerge: Integed change list # 999 by test_user using  branch view " + TEST_BRANCH_VIEW + ")",
      generator.generateDescription(TEST_DESCRIPTION_2, SOURCE_NUMBER, TEST_USER, TEST_MARKER1, true, TEST_BRANCH_VIEW, false));

    assertEquals("Test description " + TEST_MARKER1 + " (Automerge: Integed change list # 999 by test_user using reverse branch view " + TEST_BRANCH_VIEW + ")",
      generator.generateDescription(TEST_DESCRIPTION_2, SOURCE_NUMBER, TEST_USER, TEST_MARKER1, false, TEST_BRANCH_VIEW, true));

    assertEquals("Test description " + "" + " (Automerge: Integed change list # 999 by test_user using reverse branch view " + TEST_BRANCH_VIEW + ")",
      generator.generateDescription(TEST_DESCRIPTION_3, SOURCE_NUMBER, TEST_USER, TEST_MARKER2, true, TEST_BRANCH_VIEW, true));
  }


  public SATestMergedChangeListDescriptionGenerator(final String s) {
    super(s);
    generator = new MergedChangeListDescriptionGenerator();
  }
}
