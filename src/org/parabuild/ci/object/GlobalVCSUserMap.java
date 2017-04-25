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

import java.io.Serializable;

/**
 * Global version control user to email mapping.
 * <p/>
 *
 * @hibernate.class table="GLOBAL_VCS_USER_EMAIL_MAP" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 * @since Dec 27, 2008 3:53:31 PM
 */
public class GlobalVCSUserMap implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 8999210184552560122L;

  private int ID = UNSAVED_ID;

  private String vcsUserName = null;
  private String email = null;
  private String description = null;


  /**
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * @hibernate.property column = "VCS_USER_NAME" unique="true"
   * null="false"
   */
  public String getVcsUserName() {
    return vcsUserName;
  }


  public void setVcsUserName(final String vcsUserName) {
    this.vcsUserName = vcsUserName;
  }


  /**
   * @hibernate.property column = "EMAIL" unique="false"
   * null="false"
   */
  public String getEmail() {
    return email;
  }


  public void setEmail(final String email) {
    this.email = email;
  }


  /**
   * @hibernate.property column = "DESCRIPTION" unique="false"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  public String toString() {
    return "GlobalVCSUserMap{" +
            "ID=" + ID +
            ", vcsUserName='" + vcsUserName + '\'' +
            ", email='" + email + '\'' +
            ", description='" + description + '\'' +
            '}';
  }
}
