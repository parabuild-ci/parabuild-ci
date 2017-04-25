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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;

/**
 * LogContentTypeDropDown shows a list of supported issue tracker
 * [connector] types
 */
public final class IssueTrackerTypeDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 4662209824080566402L; // NOPMD


  public IssueTrackerTypeDropDown() {
    addCodeNamePair(IssueTracker.TYPE_PERFORCE, "Perforce jobs");
    addCodeNamePair(IssueTracker.TYPE_BUGZILLA_DIRECT, "Bugzilla - direct connection");
    addCodeNamePair(IssueTracker.TYPE_JIRA_LISTENER, "Jira - Parabuild listener on Jira side");
    setCode(IssueTracker.TYPE_JIRA_LISTENER);
  }
}
