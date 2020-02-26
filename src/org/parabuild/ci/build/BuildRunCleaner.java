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
package org.parabuild.ci.build;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.Type;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * This class is responsible for cleaning up a given build run in
 * preparation for build re-run.
 */
public final class BuildRunCleaner {

  private final int buildRunID;
  private final ConfigurationManager cm;
  private final ArchiveManager archiveManager;


  /**
   * Constructor.
   *
   * @param buildRunID build run ID to clean up.
   */
  public BuildRunCleaner(final int buildRunID) {
    ArgumentValidator.validateArgumentGTZero(buildRunID, "build run ID");
    this.buildRunID = buildRunID;
    this.cm = ConfigurationManager.getInstance();
    this.archiveManager = ArchiveManagerFactory.getArchiveManager(cm.getBuildRun(buildRunID).getActiveBuildID());
  }


  /**
   * Cleans up build run in preparation for the build re-run.
   * This includes deleting build run results and logs, and
   * deleting certain attributes.
   */
  public void cleanUp() {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        cleanUpBuildRunSteps(session);
        cleanUpBuildRunAttributes(session);
        return null;
      }
    });
  }


  private void cleanUpBuildRunSteps(final Session session) throws HibernateException, IOException {
    final List stepRuns = cm.getStepRuns(buildRunID);
    for (final Iterator i = stepRuns.iterator(); i.hasNext();) {
      final StepRun stepRun = (StepRun) i.next();
      deleteStepRunAttributes(stepRun, session);
      deleteStepRunLogs(stepRun, session);
      deleteStepRunResults(stepRun);
      deleteStepRun(stepRun, session);
    }
  }


  private static void deleteStepRunAttributes(final StepRun stepRun, final Session session) throws HibernateException {
    session.delete("from StepRunAttribute sra where sra.stepRunID = ?",
            new Object[]{new Integer(stepRun.getID())},
            new Type[]{Hibernate.INTEGER});
  }


  private void deleteStepRunLogs(final StepRun stepRun, final Session session) throws HibernateException, IOException {
    final List allStepLogs = cm.getAllStepLogs(stepRun.getID());
    for (final Iterator j = allStepLogs.iterator(); j.hasNext();) {
      final StepLog stepLog = (StepLog) j.next();
      archiveManager.deleteLog(stepLog);
      session.delete(stepLog);
    }
  }


  private void deleteStepRunResults(final StepRun stepRun) throws IOException {
    final List allStepResults = cm.getAllStepResults(stepRun);
    for (final Iterator j = allStepResults.iterator(); j.hasNext();) {
      archiveManager.deleteResult((StepResult) j.next());
    }
  }


  private static void deleteStepRun(final StepRun stepRun, final Session session) throws HibernateException {
    session.delete(stepRun);
  }


  /**
   * Cleans up relevant build run attributes.
   *
   * @param session
   * @throws HibernateException
   */
  private void cleanUpBuildRunAttributes(final Session session) throws HibernateException {
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_CLEAN_CHECKOUT);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_FAILURES);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_SUCCESSES);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_ERRORS);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_TESTS);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_NOTRUN);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_FATALS);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_EXPECTED_FAILS);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_UNEXPECTED_PASSES);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_JUNIT_WARNINGS);
    deleteBuildRunAttr(session, BuildRunAttribute.ATTR_STARTED_USER_ID);
  }


  /**
   * Helper method to delete build run attribute.
   *
   * @param session
   * @param attrName
   * @throws HibernateException
   */
  private void deleteBuildRunAttr(final Session session, final String attrName) throws HibernateException {
    session.delete("from BuildRunAttribute bra where bra.buildRunID = ? and bra.name = ?",
            new Object[]{new Integer(buildRunID), attrName},
            new Type[]{Hibernate.INTEGER, Hibernate.STRING});
  }


  public String toString() {
    return "BuildRunCleaner{" +
            "buildRunID=" + buildRunID +
            ", cm=" + cm +
            ", archiveManager=" + archiveManager +
            '}';
  }
}
