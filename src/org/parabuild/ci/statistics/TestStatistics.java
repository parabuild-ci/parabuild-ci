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

import java.io.*;

/**
 * This class holds build up-to-date stats
 */
public final class TestStatistics implements Serializable {

  private static final long serialVersionUID = -5345515381643142925L; // NOPMD

  private int errorTests = 0;
  private int errorTestsPercent = 0;
  private int failedTests = 0;
  private int failedTestsPercent = 0;
  private int successfulTests = 0;
  private int successfulTestsPercent = 0;
  private int totalTests = 0;
  private boolean isDirty = true;
  private int buildCount = 0;

  private int averageErrorTests = 0;
  private int averageFailedTests = 0;
  private int averageSuccessfulTests = 0;
  private int averageTotalTests = 0;


  /**
   * Copy constructor.
   */
  public TestStatistics(final TestStatistics source) {
    errorTests = source.errorTests;
    errorTestsPercent = source.errorTestsPercent;
    failedTests = source.failedTests;
    successfulTests = source.successfulTests;
    totalTests = source.totalTests;
    failedTestsPercent = source.failedTestsPercent;
    successfulTestsPercent = source.successfulTestsPercent;
    isDirty = source.isDirty;
  }


  /**
   * Creates zero statistics object.
   */
  public TestStatistics() {
  }


  public TestStatistics(final int successfulTests, final int failedTests, final int errorTests, final int buildCount) {
    this.failedTests = failedTests;
    this.errorTests = errorTests;
    this.successfulTests = successfulTests;
    this.buildCount = buildCount;
  }


  public TestStatistics(final int successfulTests, final int failedTests, final int errorTests, final int errorTestsPercent, final int failedPercent, final int successfulBuildPercent, final int totalTests, final int buildCount) {
    this.failedTests = failedTests;
    this.errorTests = errorTests;
    this.successfulTests = successfulTests;
    this.errorTestsPercent = errorTestsPercent;
    this.failedTestsPercent = failedPercent;
    this.totalTests = totalTests;
    this.successfulTestsPercent = successfulBuildPercent;
    this.buildCount = buildCount;
  }


  public int getFailedTests() {
    recalculateIfNecessary();
    return failedTests;
  }


  public int getFailedTestsPercent() {
    recalculateIfNecessary();
    return failedTestsPercent;
  }


  /**
   * @return number of "new" change lists in this build run.
   */
  public int getErrorTests() {
    recalculateIfNecessary();
    return errorTests;
  }


  public int getErrorTestsPercent() {
    recalculateIfNecessary();
    return errorTestsPercent;
  }


  public int getSuccessfulTests() {
    recalculateIfNecessary();
    return successfulTests;
  }


  public int getSuccessfulTestsPercent() {
    recalculateIfNecessary();
    return successfulTestsPercent;
  }


  public int getTotalTests() {
    recalculateIfNecessary();
    return totalTests;
  }


  public void addFailedTests(final int count) {
    failedTests += count;
    isDirty = true;
  }


  public void addSuccessfulTests(final int count) {
    successfulTests += count;
    isDirty = true;
  }


  public void addErrorTests(final int count) {
    errorTests += count;
    isDirty = true;
  }


  private void recalculateIfNecessary() {
    if (isDirty) {
      totalTests = successfulTests + failedTests + errorTests;
      if (totalTests == 0 || buildCount == 0) {
        successfulTestsPercent = 0;
        failedTestsPercent = 0;
        errorTestsPercent = 0;
        averageErrorTests = 0;
        averageFailedTests = 0;
        averageSuccessfulTests = 0;
        averageTotalTests = 0;
      } else {
        // absolute percents
        failedTestsPercent = (failedTests * 100) / totalTests;
        errorTestsPercent = (errorTests * 100) / totalTests;
        successfulTestsPercent = 100 - (failedTestsPercent + errorTestsPercent);
        // averages
        averageErrorTests = errorTests / buildCount;
        averageFailedTests = failedTests / buildCount;
        averageSuccessfulTests = successfulTests / buildCount;
        averageTotalTests = totalTests / buildCount;
      }
      isDirty = false;
    }
  }


  public int getBuildCount() {
    return buildCount;
  }


  public int getAverageErrorTests() {
    recalculateIfNecessary();
    return averageErrorTests;
  }


  public int getAverageFailedTests() {
    recalculateIfNecessary();
    return averageFailedTests;
  }


  public int getAverageSuccessfulTests() {
    recalculateIfNecessary();
    return averageSuccessfulTests;
  }


  public int getAverageTotalTests() {
    recalculateIfNecessary();
    return averageTotalTests;
  }


  public String toString() {
    return "TestStatistics{" +
      "errorTests=" + errorTests +
      ", errorTestsPercent=" + errorTestsPercent +
      ", failedTests=" + failedTests +
      ", failedTestsPercent=" + failedTestsPercent +
      ", successfulTests=" + successfulTests +
      ", successfulTestsPercent=" + successfulTestsPercent +
      ", totalTests=" + totalTests +
      ", isDirty=" + isDirty +
      ", buildCount=" + buildCount +
      ", averageErrorTests=" + averageErrorTests +
      ", averageFailedTests=" + averageFailedTests +
      ", averageSuccessfulTests=" + averageSuccessfulTests +
      ", averageTotalTests=" + averageTotalTests +
      '}';
  }


  public void addBuildCount(final int count) {
    this.buildCount += count;
  }
}
