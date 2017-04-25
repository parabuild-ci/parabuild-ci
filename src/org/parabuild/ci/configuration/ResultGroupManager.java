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
package org.parabuild.ci.configuration;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAction;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.PublishedStepResult;
import org.parabuild.ci.object.ResultGroup;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.result.BuildRunResultVO;

import java.util.Date;
import java.util.List;

/**
 */
public final class ResultGroupManager {

  private static final Log log = LogFactory.getLog(ResultGroupManager.class);
  private static final ResultGroupManager instance = new ResultGroupManager();


  private ResultGroupManager() {
  }


  public static ResultGroupManager getInstance() {
    return instance;
  }


  public ResultGroup getResultGroup(final int resultGroupID) {
    return (ResultGroup) ConfigurationManager.getInstance().getObject(ResultGroup.class, resultGroupID);
  }


  public void deleteResultGroup(final ResultGroup resultGroup) {
    ConfigurationManager.getInstance().deleteObject(resultGroup);
  }


  public ResultGroup getResultGroupByName(final String resultGroupName) {
    return (ResultGroup) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select rg from ResultGroup rg where rg.name = ?");
        q.setString(0, resultGroupName);
        return q.uniqueResult();
      }
    });
  }


  public void saveResultGroup(final ResultGroup resultGroup) {
    ConfigurationManager.getInstance().saveObject(resultGroup);
  }


  public List getResultGroups() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select rg " +
                " from ResultGroup rg, ProjectResultGroup prg, Project p " +
                " where rg.ID = prg.resultGroupID and prg.projectID = p.ID and p.deleted=no");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public PublishedStepResult getPublishedStepResult(final int stepResultID, final int resultGroupID) {
    return (PublishedStepResult) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select psr from PublishedStepResult psr where psr.stepResultID = ? and psr.resultGroupID = ?");
        q.setInteger(0, stepResultID);
        q.setInteger(1, resultGroupID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  public void save(final PublishedStepResult publishedStepResult) {
    if (log.isDebugEnabled()) log.debug("publishedStepResult: " + publishedStepResult);
    ConfigurationManager.getInstance().saveObject(publishedStepResult);
  }


  /**
   * Logs an action on a build run by storing this {@link
   * BuildRunAction} object.
   */
  public void logBuildRunAction(final BuildRunAction bra) {
    ConfigurationManager.getInstance().saveObject(bra);
  }


  public List getPublishedStepResults(final int resultGroupID, final int maxNumber) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select psr from PublishedStepResult psr where psr.resultGroupID = ? order by psr.publishDate desc");
        q.setInteger(0, resultGroupID);
        q.setCacheable(true);
        q.setMaxResults(maxNumber);
        return q.list();
      }
    });
  }


  public void unpublishStepResult(final Integer publishedResultID) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete("from PublishedStepResult psr where psr.ID = ?", publishedResultID, Hibernate.INTEGER);
        return null;
      }
    });
  }


  public PublishedStepResult getPublishedStepResult(final Integer publishedResultID) {
    if (publishedResultID == null) return null;
    return (PublishedStepResult) ConfigurationManager.getInstance().getObject(PublishedStepResult.class, publishedResultID.intValue());
  }


  public void publish(final BuildRunResultVO resultVO, final int selectedResultGroupID, final StepResult stepResult, final String comment, final Date publishedOnDate, final String resultGroupName, final int userID) {
    final PublishedStepResult publishedStepResult = new PublishedStepResult();
    publishedStepResult.setActiveBuildID(resultVO.getActiveBuildID());
    publishedStepResult.setBuildName(resultVO.getBuildName());
    publishedStepResult.setBuildRunDate(resultVO.getBuildDate());
    publishedStepResult.setBuildRunID(resultVO.getBuildRunID());
    publishedStepResult.setBuildRunNumber(resultVO.getBuildRunNumber());
    publishedStepResult.setDescription(comment);
    publishedStepResult.setPublishDate(publishedOnDate);
    publishedStepResult.setPublisherBuildRunID(resultVO.getPublisherBuildRunID());
    publishedStepResult.setResultGroupID(selectedResultGroupID);
    publishedStepResult.setStepResultID(stepResult.getID());
    save(publishedStepResult);

    // log action

    final BuildRunAction bra = new BuildRunAction();
    bra.setAction("Published " + stepResult.getDescription() + " to " + resultGroupName);
    bra.setBuildRunID(resultVO.getPublisherBuildRunID()); // NOTE: we use the publisher build run ID here
    bra.setCode(BuildRunAction.CODE_PUBLISH_RESULT);
    bra.setDate(publishedOnDate);
    bra.setDescription(comment);
    bra.setUserID(userID);
    logBuildRunAction(bra);
  }


  public void publish(final int resultGroupID, final StepResult stepResult) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun buildRun = cm.getBuildRunFromStepRun(stepResult.getStepRunID());
    final int publisherBuildRunID;
    if (buildRun.getDependence() == BuildRun.DEPENDENCE_SUBORDINATE && cm.getBuildAttributeValue(buildRun.getBuildID(), BuildConfigAttribute.SHOW_RESULTS_ON_LEADER_PAGE, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED)) {
      // this is a parallel, and it has to use leader's buils run ID as publisher run ID
      publisherBuildRunID = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_LEAD_BUILD_RUN_ID, buildRun.getBuildRunID());
    } else {
      publisherBuildRunID = buildRun.getBuildRunID();
    }
    final ResultGroupManager rgm = getInstance();
    final ResultGroup rg = rgm.getResultGroup(resultGroupID);
    final BuildRunResultVO resultVO = new BuildRunResultVO();
    resultVO.setActiveBuildID(buildRun.getActiveBuildID());
    resultVO.setBuildDate(buildRun.getStartedAt());
    resultVO.setBuildName(buildRun.getBuildName());
    resultVO.setBuildRunID(buildRun.getBuildRunID());
    resultVO.setBuildRunNumber(buildRun.getBuildRunNumber());
    resultVO.setPublisherBuildRunID(publisherBuildRunID);
    resultVO.setStepResult(stepResult);

    // calculate the publish date
    final StepRun stepRun = cm.getStepRun(stepResult.getStepRunID());
    Date publishedOnDate = stepRun.getFinishedAt();
    if (publishedOnDate == null) {
      publishedOnDate = new Date();
    }
    rgm.publish(resultVO, resultGroupID, stepResult, "Published automatically", publishedOnDate, rg.getName(), SecurityManager.getInstance().getUserByName("admin").getUserID());
  }


  /**
   * Resturns as list of published step results for the given build run.
   *
   * @param buildRunID
   */
  public List getPublishedStepResults(final int buildRunID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select psr from PublishedStepResult psr where psr.buildRunID = ?");
        q.setInteger(0, buildRunID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public String toString() {
    return "ResultGroupManager{}";
  }
}
