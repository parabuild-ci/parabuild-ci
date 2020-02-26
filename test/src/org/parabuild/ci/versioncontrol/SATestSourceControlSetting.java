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
package org.parabuild.ci.versioncontrol;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.object.SourceControlSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class SATestSourceControlSetting extends TestCase {

  public void test_nameComparator() {
    final String name1 = VersionControlSystem.P4_DEPOT_PATH;
    final String name2 = VersionControlSystem.P4_DEPOT_PATH_PART_PREFIX + ".0001";
    final String name3 = VersionControlSystem.P4_DEPOT_PATH_PART_PREFIX + ".0050";
    final SourceControlSetting scs1 = makeSourceControlSetting(name1, "//test/path/...");
    final SourceControlSetting scs2 = makeSourceControlSetting(name2, "//test/path/...");
    final SourceControlSetting scs3 = makeSourceControlSetting(name3, "//test/path/...");

    final List list = new ArrayList(3);
    list.add(scs2);
    list.add(scs1);
    list.add(scs3);
    Collections.sort(list, SourceControlSetting.PROPERTY_NAME_COMPARATOR);
    assertEquals(name1, ((SourceControlSetting)list.get(0)).getPropertyName());
    assertEquals(name2, ((SourceControlSetting)list.get(1)).getPropertyName());
    assertEquals(name3, ((SourceControlSetting)list.get(2)).getPropertyName());
  }


  private SourceControlSetting makeSourceControlSetting(final String name, final String value) {
    return new SourceControlSetting(TestHelper.TEST_P4_VALID_BUILD_ID, name, value);
  }


  protected void setUp() throws Exception {
  }


  public static TestSuite suite() {
    return new TestSuite(SATestSourceControlSetting.class);
  }


  public SATestSourceControlSetting(final String s) {
    super(s);
  }
}
