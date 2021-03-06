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

import org.parabuild.ci.object.ChangeList;

/**
 */
public class SATestMergeNagImpl extends TestCase {

  private static final String TEST_NAME = "test_name";


  public void test_create() {
    final ArrayList pendingChangeLists = new ArrayList();
    ChangeList chl = new ChangeList();
    pendingChangeLists.add(chl);
    MergeNagImpl mergeNag = new MergeNagImpl(TEST_NAME, pendingChangeLists);
    assertEquals(TEST_NAME, mergeNag.getUserName());
    assertEquals(1, mergeNag.getPendingChangeLists().size());
  }


  public SATestMergeNagImpl(final String s) {
    super(s);
  }
}
