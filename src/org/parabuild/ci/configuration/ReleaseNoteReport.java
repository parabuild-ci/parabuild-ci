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

/**
 * An aggregate that contains information about a release note
 * and assosiated objects.
 */
public final class ReleaseNoteReport {

  private final Integer issueID;
  private final String issueKey;
  private final String issueDescription;
  private final String issueURL;
  private final List chageLists = new ArrayList(1);
  private final Integer relnoteID;


  /**
   * Cosntructor.
   *
   * @param issueID issue ID
   * @param issueKey issue key
   * @param issueDescription issue description
   * @param issueURL issue URL or null if not set
   */
  public ReleaseNoteReport(final Integer relnoteID, final Integer issueID, final String issueKey, final String issueDescription, final String issueURL) {
    this.relnoteID = relnoteID;
    this.issueID = issueID;
    this.issueKey = issueKey;
    this.issueDescription = issueDescription;
    this.issueURL = issueURL;
  }


  /**
   * @return release note ID
   */
  public Integer getRelnoteID() {
    return relnoteID;
  }


  /**
   * @return issue ID
   */
  public int getIssueID() {
    return issueID.intValue();
  }


  /**
   * @return issue key
   */
  public String getIssueKey() {
    return issueKey;
  }


  /**
   * @return issue description.
   */
  public String getIssueDescription() {
    return issueDescription;
  }


  /**
   * @return issue URL.
   */
  public String getIssueURL() {
    return issueURL;
  }


  /**
   * @return list of assosiated change lists.
   */
  public List getChageLists() {
    return Collections.unmodifiableList(chageLists);
  }


  /**
   * @return size of the list of change lists assoiated with this
   *         issue.
   */
  public int chageListsSize() {
    return chageLists.size();
  }


  /**
   * Adds {@link ReleaseNoteChangeList} linked to tis issue.
   */
  public void addChageList(final ReleaseNoteChangeList releaseNoteChangeList) {
    chageLists.add(releaseNoteChangeList);
  }


  /**
   * Adds {@link ReleaseNoteChangeList} linked to tis issue.
   */
  public void addChageLists(final List releaseNoteChangeLists) {
    chageLists.addAll(releaseNoteChangeLists);
  }


  public String toString() {
    return "ReleaseNoteReport{" +
      "issueID=" + issueID +
      ", issueKey='" + issueKey + '\'' +
      ", issueDescription='" + issueDescription + '\'' +
      ", issueURL='" + issueURL + '\'' +
      ", chageLists=" + chageLists +
      ", relnoteID=" + relnoteID +
      '}';
  }
}
