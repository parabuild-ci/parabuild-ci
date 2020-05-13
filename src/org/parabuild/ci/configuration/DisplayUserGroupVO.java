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

import org.parabuild.ci.object.Group;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Value object to hold displayable user data.
 */
public final class DisplayUserGroupVO implements Serializable {

  private static final long serialVersionUID = -5774736763003128486L; // NOPMD

  /**
   * Comparator that sorts DisplayUserGroupVO by group name ignoring
   * case.
   */
  public static final Comparator GROUP_NAME_ORDER = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      return String.CASE_INSENSITIVE_ORDER.compare(
        ((DisplayUserGroupVO)o1).groupName,
        ((DisplayUserGroupVO)o2).groupName
      );
    }
  };

  private boolean groupMember;
  private final String groupName;
  private int groupID = Group.UNSAVED_ID;


  /**
   * Constructor
   *
   * @param groupMember
   * @param groupID
   * @param groupName
   */
  public DisplayUserGroupVO(final boolean groupMember, final int groupID, final String groupName) {
    this.groupID = groupID;
    this.groupMember = groupMember;
    this.groupName = groupName;
  }


  public boolean isGroupMember() {
    return groupMember;
  }


  public void setGroupMember(final boolean groupMember) {
    this.groupMember = groupMember;
  }


  public String getGroupName() {
    return groupName;
  }


  public int getGroupID() {
    return groupID;
  }


  public String toString() {
    return "DisplayUserGroupVO{" +
      "groupMember=" + groupMember +
      ", groupName='" + groupName + '\'' +
      ", groupID=" + groupID +
      '}';
  }
}
