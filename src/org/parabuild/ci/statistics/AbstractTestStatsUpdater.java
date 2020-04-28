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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.PersistentTestStats;
import org.parabuild.ci.object.StatisticsSample;

import java.util.Iterator;
import java.util.List;

/**
 */
abstract class AbstractTestStatsUpdater extends AbstractStatsUpdater { // NOPMD AbstractClassWithoutAbstractMethod

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(AbstractTestStatsUpdater.class); // NOPMD
  private final byte testCode;


  AbstractTestStatsUpdater(final byte testCode) {
    this.testCode = testCode;
  }


  /**
   * This method should calculate this build run's statistics
   * and add to the given statistics sample. Casting is required.
   */
  public final void addRunStatsToPersistentStats(final BuildRun buildRun, final StatisticsSample sampleToUpdate) {
    if (!buildRun.completed()) return;

    // calculate
    int failedTestCount = 0;
    int successfulTestCount = 0;
    int errorTestCount = 0;
    final List attributeList = ConfigurationManager.getInstance().getBuildRunAttributes(buildRun.getBuildRunID());
    for (final Iterator i = attributeList.iterator(); i.hasNext();) {
      final BuildRunAttribute bra = (BuildRunAttribute)i.next();
//      if (log.isDebugEnabled()) log.debug("bra: " + bra);
      if (testCode == PersistentTestStats.TYPE_JUNIT) {
        final String attrName = bra.getName();
        if (attrName.equals(BuildRunAttribute.ATTR_JUNIT_FAILURES)) {
          failedTestCount = bra.getValueAsInteger();
        } else if (attrName.equals(BuildRunAttribute.ATTR_JUNIT_SUCCESSES)) {
          successfulTestCount = bra.getValueAsInteger();
        } else if (attrName.equals(BuildRunAttribute.ATTR_JUNIT_ERRORS)) {
          errorTestCount = bra.getValueAsInteger();
        }
      } else {
        throw new IllegalStateException("Unknown test code: " + testCode);
      }
    }
    // add
    final PersistentTestStats pStat = (PersistentTestStats)sampleToUpdate;
    final TestStatistics testStatistics = new TestStatistics(successfulTestCount, failedTestCount, errorTestCount, 1);
    testStatistics.addErrorTests(pStat.getErrorTestCount());
    testStatistics.addFailedTests(pStat.getFailedTestCount());
    testStatistics.addSuccessfulTests(pStat.getSuccessfulTestCount());
    testStatistics.addBuildCount(pStat.getBuildCount());

    pStat.setErrorTestCount(testStatistics.getErrorTests());
    pStat.setSuccessfulTestCount(testStatistics.getSuccessfulTests());
    pStat.setFailedTestCount(testStatistics.getFailedTests());
    pStat.setBuildCount(testStatistics.getBuildCount());
//    if (log.isDebugEnabled()) log.debug("pStat: " + pStat);
  }


  final byte getTestCode() {
    return testCode;
  }
}
