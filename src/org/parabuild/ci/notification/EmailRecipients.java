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

import org.parabuild.ci.util.ArgumentValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * EmailRecipients is an aggregate holding "To" and "Bcc" lists. Each list
 * constant InternetAddress objects.
 */
public final class EmailRecipients {

  private final List toList;
  private final List bccList;


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


  /**
   * Returns a copy of the internal "To:" list.
   *
   * @return a copy of the internal "To:" list.
   */
  public List getToList() {
    return new ArrayList(toList);
  }


  /**
   * Returns a copy of the internal "Bcc:" list.
   *
   * @return a copy of the internal "Bcc:" list.
   */
  public List getBccList() {
    return new ArrayList(bccList);
  }

  public List getAllAddresses() {
    final List allRecipients = new ArrayList(23);
    allRecipients.addAll(toList);
    allRecipients.addAll(bccList);
    return allRecipients;
  }


  public String toString() {
    return "EmailRecipients{" +
      "toList=" + toList +
      ", bccList=" + bccList +
      '}';
  }
}
