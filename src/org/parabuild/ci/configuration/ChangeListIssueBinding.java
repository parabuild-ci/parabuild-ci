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
package org.parabuild.ci.configuration;

import java.io.*;

import org.parabuild.ci.object.*;

/**
 * This value object contains a pair of a change list and
 * an issue that are bound.
 * <p/>
 * It means that the change list fixes the issue or the
 * issue was fixed by the change list.
 */
public final class ChangeListIssueBinding implements Serializable {

  private static final long serialVersionUID = 6401730691739523463L; // NOPMD

  public ChangeListIssueBinding(final ChangeList changeList, final Issue issue) {
    this.changeList = changeList;
    this.issue = issue;
  }


  private ChangeList changeList = null;
  private Issue issue = null;


  public ChangeList getChangeList() {
    return changeList;
  }


  public Issue getIssue() {
    return issue;
  }


  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof ChangeListIssueBinding)) return false;

    final ChangeListIssueBinding changeListIssueBinding = (ChangeListIssueBinding)o;

    if (!changeList.equals(changeListIssueBinding.changeList)) return false;
    return issue.equals(changeListIssueBinding.issue);

  }


  public int hashCode() {
    int result = changeList.hashCode();
    result = 29 * result + issue.hashCode();
    return result;
  }


  public String toString() {
    return "ChangeListIssueBinding{" +
      "changeList=" + changeList +
      ", issue=" + issue +
      '}';
  }
}
