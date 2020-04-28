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

import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.LabelProperty;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * LabelRemover class removes label using given source control.
 */
public final class LabelRemover {

  private static final Log log = LogFactory.getLog(LabelRemover.class);

  private final SourceControl sourceControl;


  /**
   * Constructor.
   *
   * @param sourceControl SourceControl to use to remove label.
   */
  public LabelRemover(final SourceControl sourceControl) {
    this.sourceControl = sourceControl;
  }


  /**
   * Removes label using given source control. If there are any
   * exception, reports to {@link ErrorManager}
   *
   * @return int number of removed labels
   */
  public int removeOldLabels(final int activeBuildID) throws AgentFailureException {
    int removedLabels = 0;
    try {
      if (log.isDebugEnabled()) log.debug("DELETE OLD LABELS");
      // check if build should be labeled
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final LabelProperty lp = cm.getLabelSetting(activeBuildID, LabelProperty.LABEL_DELETE_ENABLED);
      if (lp == null || !lp.getPropertyValue().equals(LabelProperty.OPTION_CHECKED)) return 0;

      // get number of days
      final LabelProperty ltp = cm.getLabelSetting(activeBuildID, LabelProperty.LABEL_DELETE_OLD_DAYS);
      if (ltp == null || !StringUtils.isValidInteger(ltp.getPropertyValue())) return 0;
      final int days = Integer.parseInt(ltp.getPropertyValue());

      // get cutoff date
      final Calendar cutOffDate = Calendar.getInstance();
      cutOffDate.add(Calendar.HOUR_OF_DAY, -1 * days * 24);

      // get labels to delete
      final List list = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query query = session.createQuery("select br.buildRunID, br.label " +
                  " from BuildRun br " +
                  " where br.activeBuildID = ? " +
                  "   and br.type = ? " +
                  "   and br.finishedAt < ? " +
                  "   and br.labelStatus = ? ");
          query.setInteger(0, activeBuildID);
          query.setByte(1, BuildRun.TYPE_BUILD_RUN);
          query.setTimestamp(2, cutOffDate.getTime());
          query.setByte(3, BuildRun.LABEL_SET);
          return query.list();
        }
      });
      final String[] labels = new String[list.size()];
      int index = 0;
      for (final Iterator i = list.iterator(); i.hasNext();) {
        final Object[] objects = (Object[]) i.next();
        final String label = (String) objects[1];
        if (StringUtils.isBlank(label)) continue;
        labels[index++] = label;
      }

      // request version control to remove labels
      removedLabels = sourceControl.removeLabels(labels);

      // mark removed labels as deleted
      for (final Iterator i = list.iterator(); i.hasNext();) {
        final Object[] objects = (Object[]) i.next();
        final Integer runID = (Integer) objects[0];
        ConfigurationManager.runInHibernate(new TransactionCallback() {
          public Object runInTransaction() throws Exception {
            final BuildRun buildRun = (BuildRun) session.load(BuildRun.class, runID);
            buildRun.setLabelStatus(BuildRun.LABEL_DELETED);
            session.update(buildRun);
            return null;
          }
        });
      }
    } catch (final BuildException e) {
      final Error error = new Error(activeBuildID, "", Error.ERROR_SUBSYSTEM_SCM, e);
      error.setSendEmail(false);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    } catch (final CommandStoppedException e) {
      IoUtils.ignoreExpectedException(e);
    }

    return removedLabels;
  }
}
