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
package org.parabuild.ci.security;

import java.io.*;

/**
 * Set of rights a user has on a given result group.
 */
public final class ResultGroupRights implements Serializable {

  private static final long serialVersionUID = -4372844970766934869L; // NOPMD

  private boolean allowedToCreateResultGroup;
  private boolean allowedToDeleteResultGroup;
  private boolean allowedToUpdateResultGroup;
  private boolean allowedToViewResultGroup;

  public static final ResultGroupRights ALL_RIGHTS = new ResultGroupRights(true, true, true, true);
  public static final ResultGroupRights NO_RIGHTS = new ResultGroupRights(false, false, false, false);
  public static final ResultGroupRights VIEW_ONLY_RIGHTS = new ResultGroupRights(false, false, false, true);


  ResultGroupRights(final boolean allowedToCreateBuild, final boolean allowedToDeleteBuild,
    final boolean allowedToUpdateBuild, final boolean allowedToViewBuild) {
    //
    this.allowedToCreateResultGroup = allowedToCreateBuild;
    this.allowedToDeleteResultGroup = allowedToDeleteBuild;
    this.allowedToUpdateResultGroup = allowedToUpdateBuild;
    this.allowedToViewResultGroup = allowedToViewBuild;
  }


  public boolean isAllowedToCreateResultGroup() {
    return allowedToCreateResultGroup;
  }


  public boolean isAllowedToDeleteResultGroup() {
    return allowedToDeleteResultGroup;
  }


  public boolean isAllowedToUpdateResultGroup() {
    return allowedToUpdateResultGroup;
  }


  public boolean isAllowedToViewResultGroup() {
    return allowedToViewResultGroup;
  }


  public boolean isAllowedToListCommands() {
    return allowedToCreateResultGroup
      || allowedToDeleteResultGroup
      || allowedToUpdateResultGroup;
  }


  public String toString() {
    return "ResultGroupRights{" +
      "allowedToCreateBuild=" + allowedToCreateResultGroup +
      ", allowedToDeleteBuild=" + allowedToDeleteResultGroup +
      ", allowedToUpdateBuild=" + allowedToUpdateResultGroup +
      ", allowedToViewBuild=" + allowedToViewResultGroup +
      '}';
  }


  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final ResultGroupRights buildRights = (ResultGroupRights)o;
    if (allowedToCreateResultGroup != buildRights.allowedToCreateResultGroup) return false;
    if (allowedToDeleteResultGroup != buildRights.allowedToDeleteResultGroup) return false;
    if (allowedToUpdateResultGroup != buildRights.allowedToUpdateResultGroup) return false;
    return allowedToViewResultGroup == buildRights.allowedToViewResultGroup;
  }


  public int hashCode() {
    int result = allowedToCreateResultGroup ? 1 : 0;
    result = 29 * result + (allowedToDeleteResultGroup ? 1 : 0);
    result = 29 * result + (allowedToUpdateResultGroup ? 1 : 0);
    result = 29 * result + (allowedToViewResultGroup ? 1 : 0);
    return result;
  }
}
