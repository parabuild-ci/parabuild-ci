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
 * Persistent running statistics.
 */
public interface PersistentBuildStats extends StatisticsSample {


  int getID();


  void setID(int ID);


  int getFailedBuildPercent();


  void setFailedBuildPercent(int failedBuildPercent);


  int getSuccessfulBuildPercent();


  void setSuccessfulBuildPercent(int failedBuildPercent);


  /**
   */
  int getSuccessfulBuildCount();


  void setSuccessfulBuildCount(int successfulBuildCount);


  /**
   */
  int getFailedBuildCount();


  void setFailedBuildCount(int failedBuildCount);


  /**
   */
  int getTotalBuildCount();


  void setTotalBuildCount(int totalBuildCount);


  /**
   */
  int getChangeListCount();


  void setChangeListCount(int changeListCount);


  /**
   */
  int getIssueCount();


  void setIssueCount(int issueCount);
}
