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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;

import javax.mail.internet.InternetAddress;
import java.util.Iterator;
import java.util.List;

/**
 * DisabledEmailRemover
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jan 13, 2010 9:40:27 PM
 */
final class DisabledEmailRemover {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(DisabledEmailRemover.class); // NOPMD


  public void removeDisabled(final List addresses) {
    for (final Iterator i = addresses.iterator(); i.hasNext();) {
      final String email = ((InternetAddress) i.next()).getAddress().toLowerCase();
      final List users = SecurityManager.getInstance().findUsersByEmail(email);
      for (int j = 0; j < users.size(); j++) {
        if (((User) users.get(j)).isDisableAllEmail()) {
          i.remove();
          break;
        }
      }
    }
  }
}
