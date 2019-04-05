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
package org.parabuild.ci.statistics;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.PersistentDistribution;

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

abstract class AbstractBuildDistributionRetriever {

  private final int activeBuildID;


  AbstractBuildDistributionRetriever(final int activeBuildID) {
    this.activeBuildID = ArgumentValidator.validateBuildIDInitialized(activeBuildID);
  }


  final int getActiveBuildID() {
    return activeBuildID;
  }


  /**
   * @return SortedMap containing Integer target as a key and
   *         build statistics as a value.
   */
  public final SortedMap getStatistics() {

    // create initial empty 24 hours distribution
    final int distStart = distributionStart();
    final int distSize = distributionSize();
    final SortedMap result = new TreeMap(StatisticsUtils.INTEGER_COMPARATOR);
    for (int i = 0; i < distSize; i++) {
      result.put(new Integer(distStart + i), new BuildStatistics());
    }

    // get distribution
    final List distributionList = (List) ConfigurationManager
            .runInHibernate(new TransactionCallback() {
              public Object runInTransaction() throws Exception {
                return getDistributionFromDB(session);
              }
            });

    // put to result
    for (final Iterator i = distributionList.iterator(); i.hasNext();) {
      final PersistentDistribution pd = (PersistentDistribution) i.next();
      final BuildStatistics buildStats = new BuildStatistics(pd.getSuccessfulBuildCount(),
              pd.getFailedBuildCount(),
              pd.getChangeListCount(),
              pd.getIssueCount());
      // NOTE: we just put to the result - we dont' expect that the
      // given target hour will be met more then once.
      result.put(new Integer(pd.getTarget()), buildStats);
    }

    // return result
    return result;
  }


  /**
   * Returns a list of {@link PersistentDistribution} objects.
   *
   * @return a list of {@link PersistentDistribution} objects.
   */
  public final List getDistribution() {
    return (List) ConfigurationManager
            .runInHibernate(new TransactionCallback() {
              public Object runInTransaction() throws Exception {
                return getDistributionFromDB(session);
              }
            });
  }


  protected abstract List getDistributionFromDB(Session session) throws HibernateException;


  /**
   * @return distribution size.
   */
  protected abstract int distributionSize();


  protected abstract int distributionStart();

}
