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
package org.parabuild.ci.merge;

import java.util.*;

/**
 * Value object containing a status report for a merge.
 */
public interface MergeReport {

  /**
   * @return status of the change list
   */
  String getStringStatus();


  /**
   * @return number
   */
  String getNumber();


  /**
   * @return date
   */
  Date getDate();


  /**
   * @return description
   */
  String getUser();


  /**
   * @return description
   */
  String getDescription();


  byte getStatus();


  int getChangeListID();


  int getBranchChangeListID();
}
