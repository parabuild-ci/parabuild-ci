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

import org.parabuild.ci.object.*;

/**
 */
abstract class AbstractPersistentTestStatsRetriever extends AbstractPersistentStatsRetriever { // NOPMD AbstractClassWithoutAbstractMethod

  private final byte testCode;


  AbstractPersistentTestStatsRetriever(final int activeBuildID, final byte testToolCode) {
    super(activeBuildID);
    this.testCode = testToolCode;
  }


  /**
   * @return a statistics object that contains all zeroes
   */
  protected Object createZeroStatistics() {
    return new TestStatistics();
  }


  /**
   * @return creates a statistics obect based on (copy) of the given sample
   */
  protected Object createStatisticsFromSample(final StatisticsSample sample) {
    final PersistentTestStats pst = (PersistentTestStats)sample;
    return new TestStatistics(pst.getSuccessfulTestCount(), pst.getFailedTestCount(), pst.getErrorTestCount(), pst.getErrorTestPercent(), pst.getFailedTestPercent(), pst.getSuccessfulTestPercent(), pst.getTotalTestCount(), pst.getBuildCount());
  }


  /**
   * @return test tool code
   */
  byte getTestCode() {
    return testCode;
  }
}
