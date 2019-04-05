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


/**
 * This value object defines a specialized parameter set to
 * pass to the statistics retriever.
 */
final class StatisticsRetrieverConfiguration {

  private final int rollerInitTruncateTo;
  private final int statisticsSize;
  private final int rollerStep;
  private final int cutOffBefore;


  public StatisticsRetrieverConfiguration(final int rollerInitTruncateTo, final int statisticsSize, final int rollerStep, final int cutOffBefore) {
    this.rollerInitTruncateTo = rollerInitTruncateTo;
    this.statisticsSize = statisticsSize;
    this.rollerStep = rollerStep;
    this.cutOffBefore = cutOffBefore;
  }


  public int getRollerInitTruncateTo() {
    return rollerInitTruncateTo;
  }


  public int getStatisticsSize() {
    return statisticsSize;
  }


  public int getRollerStep() {
    return rollerStep;
  }


  public int getCutOffBefore() {
    return cutOffBefore;
  }
}
