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
package org.parabuild.ci.configuration;

import java.io.*;
import java.util.*;

import org.parabuild.ci.object.*;

/**
 */
public final class DisplayGroupBuildVO implements Serializable {

  private static final long serialVersionUID = 6401730691739523463L; // NOPMD

  /**
   * Comparator that sorts DisplayUserGroupVO by group name ignoring
   * case.
   */
  public static final Comparator BUILD_NAME_ORDER = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      return String.CASE_INSENSITIVE_ORDER.compare(
        ((DisplayGroupBuildVO)o1).buildName,
        ((DisplayGroupBuildVO)o2).buildName
      );
    }
  };

  private int buildID = BuildConfig.UNSAVED_ID;
  private String buildName = null;
  private boolean groupMember = false;


  public DisplayGroupBuildVO(final boolean groupMember, final int buildID, final String buildName) {
    this.buildID = buildID;
    this.buildName = buildName;
    this.groupMember = groupMember;
  }


  public int getBuildID() {
    return buildID;
  }


  public String getBuildName() {
    return buildName;
  }


  public boolean isGroupMember() {
    return groupMember;
  }


  public void setGroupMember(final boolean groupMember) {
    this.groupMember = groupMember;
  }


  public String toString() {
    return "DisplayGroupBuildVO{" +
      "buildID=" + buildID +
      ", buildName='" + buildName + '\'' +
      ", groupMember=" + groupMember +
      '}';
  }
}
