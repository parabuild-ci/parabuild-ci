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
package org.parabuild.ci.notification;

import java.util.*;

import org.parabuild.ci.common.*;

/**
 * EmailRecipients is an aggregate holding "To" and "Bcc" lists. Each list
 * contant InternetAddress objects.
 */
public final class EmailRecipients {

  private List toList = null;
  private List bccList = null;


  /**
   * Constructor.
   *
   * @param toList - non-null List
   * @param bccList - non-null List
   */
  public EmailRecipients(final List toList, final List bccList) {
    ArgumentValidator.validateArgumentNotNull(toList, "to list");
    ArgumentValidator.validateArgumentNotNull(bccList, "cc list");
    this.toList = new ArrayList(toList);
    this.bccList = new ArrayList(bccList);
  }


  public List getToList() {
    return toList;
  }


  public List getBccList() {
    return bccList;
  }

  public List getAllAddresses() {
    final List allRecepients = new ArrayList(23);
    allRecepients.addAll(toList);
    allRecepients.addAll(bccList);
    return allRecepients;
  }


  public String toString() {
    return "EmailRecipients{" +
      "toList=" + toList +
      ", bccList=" + bccList +
      '}';
  }
}
