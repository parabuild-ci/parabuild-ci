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
package org.parabuild.ci.merge;

import java.util.*;

import junit.framework.TestCase;

/**
 */
public final class SATestMergeReportImpl extends TestCase {

  private static final Byte TEST_STATUS = new Byte((byte)1);
  private static final String TEST_NUMBER = "2";
  private static final String TEST_USER = "test user";
  private static final Date TEST_DATE = new Date();
  private static final String TEST_DESCRIPTION = "test description";
  private static final Integer BRANCH_CHANGE_LIST_ID = new Integer(777);
  private static final int CHANGE_LIST_ID = 888;


  public void test_create() {
    MergeReportImpl mergeReport = new MergeReportImpl(TEST_STATUS, BRANCH_CHANGE_LIST_ID, CHANGE_LIST_ID, TEST_NUMBER, TEST_USER, TEST_DATE, TEST_DESCRIPTION);
    assertEquals(TEST_DATE, mergeReport.getDate());
    assertEquals(TEST_DESCRIPTION, mergeReport.getDescription());
    assertEquals(TEST_NUMBER, mergeReport.getNumber());
    assertEquals(TEST_STATUS.byteValue(), mergeReport.getStatus());
    assertEquals(MergeReportImpl.STRING_STATUS_INTEGRATED, mergeReport.getStringStatus());
    assertEquals(TEST_USER, mergeReport.getUser());
  }


  public SATestMergeReportImpl(final String s) {
    super(s);
  }
}
