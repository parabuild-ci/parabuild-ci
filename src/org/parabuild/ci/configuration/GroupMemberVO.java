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

import java.io.Serializable;
import java.util.Comparator;

/**
 */
public final class GroupMemberVO implements Serializable {

  private static final long serialVersionUID = 6401730691739523463L; // NOPMD

  /**
   * Comparator that sorts GroupMemberVO by name ignoring
   * case.
   */
  public static final Comparator NAME_ORDER = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      return String.CASE_INSENSITIVE_ORDER.compare(
        ((GroupMemberVO)o1).name,
        ((GroupMemberVO)o2).name
      );
    }
  };

  private int ID = -1;
  private final String name;
  private boolean groupMember;


  public GroupMemberVO(final boolean groupMember, final int ID, final String name) {
    this.ID = ID;
    this.name = name;
    this.groupMember = groupMember;
  }


  public int getID() {
    return ID;
  }


  public String getName() {
    return name;
  }


  public boolean isGroupMember() {
    return groupMember;
  }


  public void setGroupMember(final boolean groupMember) {
    this.groupMember = groupMember;
  }


  public String toString() {
    return "GroupMemberVO{" +
      "ID=" + ID +
      ", name='" + name + '\'' +
      ", groupMember=" + groupMember +
      '}';
  }
}
