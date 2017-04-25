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
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.object.*;


/**
 * Tests ChangeListWindowMerger
 */
public class SATestCVSChangeListWindowMerger extends TestCase {

  private static final Log log = LogFactory.getLog(SATestCVSChangeListWindowMerger.class);

  protected ChangeListWindowMerger merger;

  protected List firstRun;
  protected ChangeList changeList1;
  protected ChangeList changeList2;

  protected List secondRun;
  protected ChangeList changeList3;
  protected ChangeList changeList4;
  protected ChangeList changeList5;


  public SATestCVSChangeListWindowMerger(final String s) {
    super(s);
  }


  public void test_dontMergeUnmergable() {
    merger.mergeInChangesLeft(firstRun, secondRun);
    // test it has not changed
    assertEquals(2, firstRun.size());
    assertEquals(2, changeList1.getChanges().size());
    assertEquals(2, changeList2.getChanges().size());
  }


  public void test_mergeChangesLeft() {
    // alter set up
    changeList3.setCreatedAt(changeList1.getCreatedAt());
    changeList3.setUser(changeList1.getUser());
    changeList3.setDescription(changeList1.getDescription());

    // merge
    merger.mergeInChangesLeft(firstRun, secondRun);
    // assert increased number of changes in changeList1
    assertEquals(4, changeList1.getChanges().size());

    // test it has not changed
    assertEquals(2, firstRun.size());
    assertEquals(2, changeList2.getChanges().size());

    // test for bug #511 - content of all changes are of type Change
    for (Iterator i = firstRun.iterator(); i.hasNext();) {
      final ChangeList changeList = (ChangeList)i.next();
      for (Iterator j = changeList.getChanges().iterator(); j.hasNext();) {
        final Object object = j.next();
        assertTrue("Type of object should be Change, but it was: " + object.getClass().getName(),
          object instanceof Change);
      }
    }
  }


  public void test_dontSkipUnmergeable() {
    // alter set up - 1st - mergable, 2nd-unmergable, 3rd - mergable
    changeList3.setCreatedAt(new Date(changeList1.getCreatedAt().getTime() + 0 * 1000));
    changeList3.setUser(changeList1.getUser());
    changeList3.setDescription(changeList1.getDescription());

    changeList4.setCreatedAt(new Date(changeList1.getCreatedAt().getTime() + 50 * 1000));

    changeList5.setCreatedAt(new Date(changeList1.getCreatedAt().getTime() + 120 * 1000));
    changeList5.setUser(changeList1.getUser());
    changeList5.setDescription(changeList1.getDescription());

    // merge
    merger.mergeInChangesLeft(firstRun, secondRun);
    // assert increased number of changes in changeList1
    assertEquals(4, changeList1.getChanges().size());

    // test it has not changed
    assertEquals(2, firstRun.size());
    assertEquals(2, changeList2.getChanges().size());
  }


  protected void setUp() throws Exception {
    super.setUp();
    merger = new ChangeListWindowMerger();

    // ///////////////////////////////////////////////////////
    // set up test change lists, all are diffrent (unmergable)


    // init first run

    firstRun = new ArrayList(3);

    changeList1 = new ChangeList();
    {
      changeList1.setBranch("test_branch");
      changeList1.setCreatedAt(new Date());
      changeList1.setUser("test_user_1");
      changeList1.setDescription("test_descr_1");

      final Change change1 = new Change();
      change1.setChangeType(Change.TYPE_MODIFIED);
      change1.setFilePath("test/file1");
      changeList1.getChanges().add(change1);

      final Change change2 = new Change();
      change2.setChangeType(Change.TYPE_MODIFIED);
      change2.setFilePath("test/file2");
      changeList1.getChanges().add(change2);
    }
    firstRun.add(changeList1);

    changeList2 = new ChangeList();
    {
      changeList2.setBranch("test_branch");
      changeList2.setCreatedAt(new Date());
      changeList2.setUser("test_user_2");
      changeList2.setDescription("test_descr_2");

      final Change change3 = new Change();
      change3.setChangeType(Change.TYPE_MODIFIED);
      change3.setFilePath("test/file3");
      changeList2.getChanges().add(change3);

      final Change change4 = new Change();
      change4.setChangeType(Change.TYPE_MODIFIED);
      change4.setFilePath("test/file4");
      changeList2.getChanges().add(change4);
    }
    firstRun.add(changeList2);



    // init second run

    secondRun = new ArrayList(3);

    changeList3 = new ChangeList();
    {
      changeList3.setBranch("test_branch");
      changeList3.setCreatedAt(new Date());
      changeList3.setUser("test_user_1");
      changeList3.setDescription("test_descr_3");

      final Change change5 = new Change();
      change5.setChangeType(Change.TYPE_MODIFIED);
      change5.setFilePath("test/file5");
      changeList3.getChanges().add(change5);

      final Change change6 = new Change();
      change6.setChangeType(Change.TYPE_MODIFIED);
      change6.setFilePath("test/file6");
      changeList3.getChanges().add(change6);
    }
    secondRun.add(changeList3);

    changeList4 = new ChangeList();
    {
      changeList4.setBranch("test_branch");
      changeList4.setCreatedAt(new Date());
      changeList4.setUser("test_user_2");
      changeList4.setDescription("test_descr_4");

      final Change change7 = new Change();
      change7.setChangeType(Change.TYPE_MODIFIED);
      change7.setFilePath("test/file7");
      changeList4.getChanges().add(change7);

      final Change change8 = new Change();
      change8.setChangeType(Change.TYPE_MODIFIED);
      change8.setFilePath("test/file8");
      changeList4.getChanges().add(change8);
    }
    secondRun.add(changeList4);

    changeList5 = new ChangeList();
    {
      changeList5.setBranch("test_branch");
      changeList5.setCreatedAt(new Date());
      changeList5.setUser("test_user_2");
      changeList5.setDescription("test_descr_5");

      final Change change9 = new Change();
      change9.setChangeType(Change.TYPE_MODIFIED);
      change9.setFilePath("test/file8");
      changeList5.getChanges().add(change9);

      final Change change10 = new Change();
      change10.setChangeType(Change.TYPE_MODIFIED);
      change10.setFilePath("test/file9");
      changeList5.getChanges().add(change10);
    }
    secondRun.add(changeList5);

    // pretest assertions, just to make sure we composed test data alright.
    assertEquals(2, changeList1.getChanges().size());
    assertEquals(2, changeList2.getChanges().size());
    assertEquals(2, changeList3.getChanges().size());
    assertEquals(2, changeList4.getChanges().size());
    assertEquals(2, firstRun.size());
    assertEquals(3, secondRun.size());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestCVSChangeListWindowMerger.class);
  }
}
