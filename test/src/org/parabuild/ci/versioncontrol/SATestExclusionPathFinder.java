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

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;

/**
 *
 */
public class SATestExclusionPathFinder extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestExclusionPathFinder.class);
  private ExclusionPathFinder exclusionPathFinder;


  public SATestExclusionPathFinder(final String s) {
    super(s);
  }


  public void test_onlyExclusionPathsPresentInChangeLists() throws Exception {
    final Change ch1 = new Change("some/path/one", "1.1", Change.TYPE_ADDED);
    final Change ch2 = new Change("some\\path\\two", "1.1", Change.TYPE_ADDED);
    final Change ch3 = new Change("some\\path/three", "1.1", Change.TYPE_ADDED);
    final Change ch4 = new Change("SOME\\path/three", "1.1", Change.TYPE_ADDED);
    final Set changes = new HashSet(4);
    changes.add(ch1);
    changes.add(ch2);
    changes.add(ch3);
    changes.add(ch4);
    final ChangeList cl = new ChangeList();
    cl.setChanges(changes);
    final List chageLists = new ArrayList(1);
    chageLists.add(cl);
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, "some/path/one\nsome/path/two\nsome/path/three\n"));
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, "SOME/PATH/ONE\nsome/path/two\nsome/path/three\n"));
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, "some\\path\\one\nsome\\path\\two\nsome\\path\\three\n"));
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, "^.*path.*$"));
    assertTrue(!exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, "^.*notfound.*$"));
    assertTrue(!exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, "some/path/one"));
    assertTrue(!exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, "some/path/one\nsome/path/two"));
    assertTrue(!exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, "some/path/three\n"));
    assertTrue(!exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, ""));
    assertTrue(!exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(chageLists, null));
  }


  public void test_onlyExclusionPathsPresentInPathList() throws Exception {
    List l = new ArrayList(3);
    l.add(" prefix/some/path/one ");
    l.add("");
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInPathList(l, "some/path/one"));
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInPathList(l, "some/path/one "));
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInPathList(l, " some/path/one "));
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInPathList(l, "some/path/one\nsome/path/two\n"));
  }


  public void test_bug1200() throws Exception {
    List l = new ArrayList(3);
    l.add("/Cobra/Source/WinUI/Cobra.WinUI/AssemblyInfo.cs");
    assertTrue(exclusionPathFinder.onlyExclusionPathsPresentInPathList(l, "/Cobra/Source/WinUI/Cobra.WinUI/AssemblyInfo.cs\n" +
      "$/Cobra/Source/WinUI/Cobra.WinUI/AssemblyInfo.cs\n" +
      "$/Cobra/Source/CobraBase/Cobra.Business/Properties/AssemblyInfo.cs\n" +
      "/Cobra/Source/CobraBase/Cobra.Business/Properties/AssemblyInfo.cs\n"
    ));
  }


  protected void setUp() throws Exception {
    super.setUp();
    exclusionPathFinder = new ExclusionPathFinder();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestExclusionPathFinder.class,
      new String[]{
      });
  }
}
