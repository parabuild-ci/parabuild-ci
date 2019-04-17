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
package org.parabuild.ci.webui.common;

import org.parabuild.ci.security.SecurityManager;
import viewtier.ui.*;

/**
 * Specialised Password field that operates on encrtpted
 * passwords. Input and output are encrypted.
 */
public final class EncryptingPassword extends Password {


  private static final long serialVersionUID = 1464451197975039573L;


  /**
   * Constructor
   *
   * @param i
   * @param i1
   */
  public EncryptingPassword(final int i, final int i1, final String fieldName) {
    super(i, i1);
    setName(fieldName);
  }


  /**
   * Sets encryped password.
   *
   * @param encriptedPassword
   */
  public void setEncryptedValue(final String encriptedPassword) {
    setValue(SecurityManager.decryptPassword(encriptedPassword));
  }


  /**
   * Returns encryped password.
   */
  public String getEncryptedValue() {
    return SecurityManager.encryptPassword(getValue());
  }


  public String toString() {
    return "EncryptingPassword{}";
  }
}
