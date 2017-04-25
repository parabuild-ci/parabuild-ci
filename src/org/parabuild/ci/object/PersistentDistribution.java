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
 * Persistant distribution.
 */
public interface PersistentDistribution {

  int getID();


  void setID(int ID);


  int getActiveBuildID();


  void setActiveBuildID(int activeBuildID);


  int getTarget();


  void setTarget(int target);


  int getSuccessfulBuildCount();


  void setSuccessfulBuildCount(int successfulBuildCount);


  int getFailedBuildCount();


  void setFailedBuildCount(int failedBuildCount);


  int getTotalBuildCount();


  void setTotalBuildCount(int totalBuildCount);


  int getChangeListCount();


  void setChangeListCount(int changeListCount);


  int getIssueCount();


  void setIssueCount(int issueCount);
}
