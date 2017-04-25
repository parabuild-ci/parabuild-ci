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
import java.util.List;

/**
 * BT user
 *
 * @hibernate.class table="USERS" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class User implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5237501290837750448L; // NOPMD

  public static final String DEFAULT_ADMIN_USER = "admin";
  public static final String DEFAULT_ADMIN_PASSW = "admin";

  public static final String ROLE_ADMIN = "admin";
  public static final String ROLE_COMMAND = "command";
  public static final String ROLE_RELNOTES = "relnotes";
  public static final String ROLE_USER = "user";
  public static final String ROLE_WEBSERVICE = "webservice";

  private boolean admin = false;
  private boolean enabled = true;
  private int userID = UNSAVED_ID;
  private long timeStamp = 0;
  private String email = null;
  private String fullName = "";
  private String name = null;
  private String password = null;
  private String userRoles = ROLE_ADMIN;
  private byte imType = IM_TYPE_NONE;
  private String imAddress = "";
  private boolean authenticateUsingLDAP = false;
  private boolean disableAllEmail = false;


  /**
   * Default constructor.
   */
  public User() {
  }


  /**
   * Constructor.
   *
   * @param name
   * @param password
   * @param roles
   */
  public User(final String name, final String password, final List roles) {
    this.name = name;
    this.password = password;
    // convert role list to a coma-separated list
    final StringBuffer sb = new StringBuffer(100);
    for (int i = 0, n = roles.size(); i < n; i++) {
      final String role = (String) roles.get(i);
      sb.append(role);
      if (i != n - 1) {
        sb.append(", ");
      }
    }
    this.userRoles = sb.toString();
  }


  /**
   * The getter method for this build ID
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getUserID() {
    return userID;
  }


  public void setUserID(final int userID) {
    this.userID = userID;
  }


  /**
   * @hibernate.property column = "NAME" unique="true"
   * null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @hibernate.property column = "FNAME" unique="true"
   * null="false"
   */
  public String getFullName() {
    return fullName;
  }


  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }


  public void setAdmin(final boolean admin) {
    this.admin = admin;
  }


  /**
   * @hibernate.property column = "IS_ADMIN" type="yes_no"
   * unique="false" null="false"
   */
  public boolean isAdmin() {
    return admin;
  }


  /**
   * @return String
   * @hibernate.property column = "EMAIL" unique="true"
   * null="false"
   */
  public String getEmail() {
    return email;
  }


  public void setEmail(final String email) {
    this.email = email;
  }


  /**
   * Returns timestamp
   *
   * @return long
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * @return String password
   * @hibernate.property column="PASSWORD" null="false"
   */
  public String getPassword() {
    return password;
  }


  public void setPassword(final String password) {
    this.password = password;
  }


  /**
   * @return String role
   * @hibernate.property column="ROLES" null="false"
   */
  public String getUserRoles() {
    return userRoles;
  }


  public void setUserRoles(final String userRoles) {
    this.userRoles = userRoles;
  }


  /**
   * Returns true if this user is enabled
   *
   * @return String
   * @hibernate.property column="ENABLED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isEnabled() {
    return enabled;
  }


  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * @hibernate.property column="IM_TYPE"
   * unique="false" null="false"
   */
  public byte getImType() {
    return imType;
  }


  public void setImType(final byte imType) {
    this.imType = imType;
  }


  /**
   * @hibernate.property column="IM_ADDRESS"
   * unique="false" null="false"
   */
  public String getImAddress() {
    return imAddress;
  }


  public void setImAddress(final String imAddress) {
    this.imAddress = imAddress;
  }


  /**
   * Returns true if this user is enabled
   *
   * @return String
   * @hibernate.property column="LDAP_AUTH"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isAuthenticateUsingLDAP() {
    return authenticateUsingLDAP;
  }


  public void setAuthenticateUsingLDAP(final boolean authenticateUsingLDAP) {
    this.authenticateUsingLDAP = authenticateUsingLDAP;
  }


  /**
   * Returns true if this user is enabled
   *
   * @return String
   * @hibernate.property column="DISABLE_ALL_EMAIL"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isDisableAllEmail() {
    return disableAllEmail;
  }


  public void setDisableAllEmail(final boolean disableAllEmail) {
    this.disableAllEmail = disableAllEmail;
  }


  public String toString() {
    return "User{" +
            "admin=" + admin +
            ", enabled=" + enabled +
            ", userID=" + userID +
            ", timeStamp=" + timeStamp +
            ", email='" + email + '\'' +
            ", fullName='" + fullName + '\'' +
            ", name='" + name + '\'' +
            ", password='" + password + '\'' +
            ", userRoles='" + userRoles + '\'' +
            ", authenticateUsingLDAP=" + authenticateUsingLDAP +
            ", imType=" + imType +
            ", imAddress='" + imAddress + '\'' +
            '}';
  }
}
