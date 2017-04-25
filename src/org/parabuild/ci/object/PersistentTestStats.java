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
package org.parabuild.ci.object;

/**
 */
public interface PersistentTestStats extends StatisticsSample {

  byte TYPE_JUNIT = 1;
  byte TYPE_CPPUNIT = 2;
  byte TYPE_NUNIT = 3;


  /**
   */
  int getSuccessfulTestCount();


  /**
   */
  int getSuccessfulTestPercent();


  /**
   */
  int getFailedTestCount();


  /**
   */
  int getTotalTestCount();


  /**
   */
  int getFailedTestPercent();


  /**
   */
  byte getTestCode();


  /**
   */
  int getErrorTestCount();


  void setErrorTestCount(int errorTestCount);


  /**
   */
  int getErrorTestPercent();


  void setSuccessfulTestCount(int successfulTestCount);


  void setFailedTestCount(int failedTestCount);


  void setFailedTestPercent(int failedTestPercent);


  int getBuildCount();

  void setBuildCount(int count);
}
