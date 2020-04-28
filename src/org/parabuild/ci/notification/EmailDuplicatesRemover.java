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

import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility class to remove duplicates from a list of
 * InternetAddresses.
 */
public final class EmailDuplicatesRemover {


  /**
   * Removes duplicates from a given tracker.
   *
   * @param addresses list to clean up
   */
  public void removeDuplicates(final List addresses) {
    final Map alreadyExistsTracker = new HashMap(23);
    for (final Iterator i = addresses.iterator(); i.hasNext();) {
      final String email = ((InternetAddress)i.next()).getAddress().toLowerCase();
      if (alreadyExistsTracker.get(email) != null) {
        // remove from list
        i.remove();
      } else {
        // register
        alreadyExistsTracker.put(email, Boolean.TRUE);
      }
    }
  }
}
