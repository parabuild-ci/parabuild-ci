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
 * Set of rights a user has on a given build.
 */
public final class BuildRights implements Serializable {

  private static final long serialVersionUID = -4372844970766934869L; // NOPMD

  private boolean allowedToCreateBuild = false;
  private boolean allowedToDeleteBuild = false;
  private boolean allowedToStartBuild = false;
  private boolean allowedToStopBuild = false;
  private boolean allowedToUpdateBuild = false;
  private boolean allowedToViewBuild = true;
  private boolean allowedToActivateBuild = false;
  private boolean allowedToPublishResults = false;
  private boolean allowedToDeleteResults = false;

  public static final BuildRights ALL_RIGHTS = new BuildRights(true, true, true, true, true, true, true, true, true);
  public static final BuildRights NO_RIGHTS = new BuildRights(false, false, false, false, false, false, false, false, false);
  public static final BuildRights VIEW_ONLY_RIGHTS = new BuildRights(false, false, false, false, false, true, false, false, false);


  BuildRights(final boolean allowedToCreateBuild, final boolean allowedToDeleteBuild,
    final boolean allowedToStartBuild, final boolean allowedToStopBuild,
    final boolean allowedToUpdateBuild, final boolean allowedToViewBuild,
    final boolean allowedToActivateBuild, final boolean allowedToPublishResults,
    final boolean allowedToDeleteResults) {
    //
    this.allowedToCreateBuild = allowedToCreateBuild;
    this.allowedToDeleteBuild = allowedToDeleteBuild;
    this.allowedToStartBuild = allowedToStartBuild;
    this.allowedToStopBuild = allowedToStopBuild;
    this.allowedToUpdateBuild = allowedToUpdateBuild;
    this.allowedToViewBuild = allowedToViewBuild;
    this.allowedToActivateBuild = allowedToActivateBuild;
    this.allowedToPublishResults = allowedToPublishResults;
    this.allowedToDeleteResults = allowedToDeleteResults;
  }


  public boolean isAllowedToCreateBuild() {
    return allowedToCreateBuild;
  }


  public boolean isAllowedToDeleteBuild() {
    return allowedToDeleteBuild;
  }


  public boolean isAllowedToStartBuild() {
    return allowedToStartBuild;
  }


  public boolean isAllowedToStopBuild() {
    return allowedToStopBuild;
  }


  public boolean isAllowedToResumeBuild() {
    return allowedToStopBuild; // reverse action
  }


  public boolean isAllowedToUpdateBuild() {
    return allowedToUpdateBuild;
  }


  public boolean isAllowedToViewBuild() {
    return allowedToViewBuild;
  }


  public boolean isAllowedToActivateBuild() {
    return allowedToActivateBuild;
  }


  public boolean isAllowedToDeactivateBuild() {
    return allowedToActivateBuild; // backword right
  }


  public boolean isAllowedToPublishResults() {
    return allowedToPublishResults;
  }


  public boolean isAllowedToDeleteResults() {
    return allowedToDeleteResults;
  }


  public boolean isAllowedToListCommands() {
    return allowedToActivateBuild
      || allowedToCreateBuild
      || allowedToDeleteBuild
      || allowedToActivateBuild
      || allowedToStartBuild
      || allowedToStopBuild
      || allowedToUpdateBuild;
  }


  public boolean isAllowedToListResultCommands() {
    return allowedToDeleteResults
      || allowedToPublishResults;
  }


  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final BuildRights that = (BuildRights)o;

    if (allowedToActivateBuild != that.allowedToActivateBuild) return false;
    if (allowedToCreateBuild != that.allowedToCreateBuild) return false;
    if (allowedToDeleteBuild != that.allowedToDeleteBuild) return false;
    if (allowedToDeleteResults != that.allowedToDeleteResults) return false;
    if (allowedToPublishResults != that.allowedToPublishResults) return false;
    if (allowedToStartBuild != that.allowedToStartBuild) return false;
    if (allowedToStopBuild != that.allowedToStopBuild) return false;
    if (allowedToUpdateBuild != that.allowedToUpdateBuild) return false;
    return allowedToViewBuild == that.allowedToViewBuild;

  }


  public int hashCode() {
    int result = allowedToCreateBuild ? 1 : 0;
    result = 29 * result + (allowedToDeleteBuild ? 1 : 0);
    result = 29 * result + (allowedToStartBuild ? 1 : 0);
    result = 29 * result + (allowedToStopBuild ? 1 : 0);
    result = 29 * result + (allowedToUpdateBuild ? 1 : 0);
    result = 29 * result + (allowedToViewBuild ? 1 : 0);
    result = 29 * result + (allowedToActivateBuild ? 1 : 0);
    result = 29 * result + (allowedToPublishResults ? 1 : 0);
    result = 29 * result + (allowedToDeleteResults ? 1 : 0);
    return result;
  }


  public String toString() {
    return "BuildRight{" +
      "allowedToCreateBuild=" + allowedToCreateBuild +
      ", allowedToDeleteBuild=" + allowedToDeleteBuild +
      ", allowedToStartBuild=" + allowedToStartBuild +
      ", allowedToStopBuild=" + allowedToStopBuild +
      ", allowedToUpdateBuild=" + allowedToUpdateBuild +
      ", allowedToViewBuild=" + allowedToViewBuild +
      ", allowedToActivateBuild=" + allowedToActivateBuild +
      ", allowedToPublishResults=" + allowedToPublishResults +
      ", allowedToDeleteResults=" + allowedToDeleteResults +
      '}';
  }


  
}
