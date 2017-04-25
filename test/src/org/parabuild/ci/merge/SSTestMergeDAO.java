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

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BranchChangeList;
import org.parabuild.ci.object.MergeConfigurationAttribute;
import org.parabuild.ci.object.BranchBuildRunParticipant;

/**
 * Tests MergeManager
 */
public class SSTestMergeDAO extends ServersideTestCase {


  private MergeDAO mergeDAO;


  public void test_findLatestBranchChangeList() {
    mergeDAO.findLatestBranchChangeList(0);
  }


  public void test_findBrachChangeList() {
    mergeDAO.getBrachChangeList(0, 0);
  }


  public void test_findChangeList() {
    mergeDAO.findChangeList("1", new Date(), "user");
  }


  public void test_POC() {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.createQuery(
          "select rmc from ActiveMergeConfiguration rmc ")
          .list();
        final List list2 = session.createQuery(
          "select rmc from BranchMergeConfiguration rmc ")
          .list();
        return list2.isEmpty() ? null : list2.get(0);
      }
    });

  }


  public void test_findSameRuntime() {
    mergeDAO.findSameRuntime(MergeManager.getInstance().getActiveMergeConfiguration(0));
  }


  public void test_getMergeConfigurationAttribute() {
    mergeDAO.getMergeConfigurationAttribute(0, MergeConfigurationAttribute.NAG_DAY_SENT_LAST_TIME);
  }


  public void test_getUmergedChangeLists() {
    mergeDAO.getUnmergedChangeLists(0, 0, 100);
  }


  public void test_saveMergeConfiguration() {
    mergeDAO.save(MergeManager.getInstance().getActiveMergeConfiguration(0));
  }


  public void test_branchChangeListIsInQueue() {
    mergeDAO.branchChangeListIsInQueue(0);
  }


  public void test_findBranchChangeList() {
    mergeDAO.findBranchChangeList(0, "1", new Date(), "test_user");
  }


  public void test_getBranchBuildRunChangeList() {
    mergeDAO.getBranchBuildRunChangeList(0, 1);
  }


  public void test_deleteMergeQueueMember() {
    mergeDAO.deleteMergeQueueMember(0);
  }


  public void test_getValidatedMergeQueueIDs() {
    assertNotNull(mergeDAO.getValidatedMergeIDs(0));
  }


  public void test_getMergeQueueChangeLists() {
    assertNotNull(mergeDAO.getAllMergeChangeLists(0));
  }


  public void test_getTargetBuildIDForMergeQueue() {
    assertNull(mergeDAO.getTargetBuildConfigurationIDForMergeQueue(99999));
  }


  public void test_saveBranchBuildRunParticipant() {
    // prepare
    final BranchChangeList branchChangeList = new BranchChangeList();
    branchChangeList.setChangeListID(1);
    branchChangeList.setMergeConfigurationID(0);
    mergeDAO.save(branchChangeList);

    final BranchBuildRunParticipant branchBuildRunParticipant = new BranchBuildRunParticipant();
    branchBuildRunParticipant.setBranchChangeListID(branchChangeList.getID());
    branchBuildRunParticipant.setBuildRunParticipantID(1);
    mergeDAO.save(branchBuildRunParticipant);
  }


  public void test_addToUnvalidatedMergeQueue() {
    final BranchChangeList branchChangeList = new BranchChangeList();
    branchChangeList.setMergeConfigurationID(0);
    branchChangeList.setChangeListID(1);
    ConfigurationManager.getInstance().saveObject(branchChangeList);
    mergeDAO.addToUnvalidatedMergeQueue(branchChangeList);
  }


  public void test_getMergeQueueConfiguration() {
    assertNull(mergeDAO.getMergeQueueConfiguration(9999));
  }


  public void test_findUnvalidatedMergeQueueMember() {
    assertNull(mergeDAO.findUnvalidatedMergeQueueMember(9999));
  }


  public SSTestMergeDAO(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestMergeDAO.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    mergeDAO = MergeDAO.getInstance();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
  }
}
