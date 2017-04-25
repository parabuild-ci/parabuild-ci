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

import java.util.*;

import org.parabuild.ci.object.ChangeList;


/**
 * List these objects is returned by P4ChangeLogParser
 */
public final class ChangeListsAndIssuesImpl implements ChangeListsAndIssues {

  private static final long serialVersionUID = 6401730691739523463L; // NOPMD

  private final List changeLists = new ArrayList(11);

  private final List changeListIssueBindings = new ArrayList(11);


  public void addChangelist(final ChangeList changeList) {
    changeLists.add(changeList);
  }


  public void addBinding(final ChangeListIssueBinding binding) {
    changeListIssueBindings.add(binding);
  }


  public List getChangeListIssueBindings() {
    return changeListIssueBindings;
  }


  public List getChangeLists() {
    return changeLists;
  }


  public String toString() {
    return "ChangeListsAndIssuesImpl{" +
      "changeLists=" + changeLists +
      ", changeListIssueBindings=" + changeListIssueBindings +
      '}';
  }
}


