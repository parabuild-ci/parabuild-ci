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

import java.util.*;

/**
 * A class representing a JNDI user.
 *
 * @see JNDIAuthenticator
 */
public final class JNDIUser {

  private String username;
  private String dn;
  private String password;
  private List roles;
  private String email;


  JNDIUser(final String username, final String dn, final String password, final String email, final List roles) {
    this.username = username;
    this.dn = dn;
    this.password = password;
    this.roles = roles;
    this.email = email;
  }


  public String getUsername() {
    return username;
  }


  public void setUsername(final String username) {
    this.username = username;
  }


  public String getDn() {
    return dn;
  }


  public void setDn(final String dn) {
    this.dn = dn;
  }


  public String getPassword() {
    return password;
  }


  public void setPassword(final String password) {
    this.password = password;
  }


  public List getRoles() {
    return roles;
  }


  public void setRoles(final List roles) {
    this.roles = roles;
  }


  public String getEmail() {
    return email;
  }


  public void setEmail(final String email) {
    this.email = email;
  }


  public String toString() {
    return "JNDIUser{" +
      "username='" + username + '\'' +
      ", dn='" + dn + '\'' +
      ", password='" + (password == null ? null : "*********") + '\'' +
      ", roles=" + roles +
      ", email='" + email + '\'' +
      '}';
  }
}
