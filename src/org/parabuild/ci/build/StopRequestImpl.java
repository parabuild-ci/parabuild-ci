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
package org.parabuild.ci.build;

import org.parabuild.ci.object.User;

/**
 * StopRequestImpl
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jul 29, 2008 10:57:13 PM
 */
public final class StopRequestImpl implements StopRequest {

  private int userID = User.UNSAVED_ID;

  public StopRequestImpl(final int userID) {
    this.userID = userID;
  }

  public int getUserID() {
    return userID;
  }

  public boolean isUserSet() {
    return userID != User.UNSAVED_ID;
  }
}
