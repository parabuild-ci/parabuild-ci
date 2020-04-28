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
package org.parabuild.ci.relnotes;

import org.parabuild.ci.object.Issue;

import java.io.Serializable;
import java.sql.Date;

/**
 * This class is returned by BugzillaJDBCConnector
 *
 * @see BugzillaDatabaseConnector#requestBugsFromBugzilla
 */
final class BugzillaBug implements Serializable {

  private static final long serialVersionUID = -8771878086172241969L; // NOPMD

  private int bugID = Issue.UNSAVED_ID;
  private String shortDescr;
  private String product;
  private String version;
  private String status;
  private Date date;


  public BugzillaBug(final int bugID, final String product, final String shortDescr, final String status, final String version, final Date date) {
    this.bugID = bugID;
    this.product = product;
    this.shortDescr = shortDescr;
    this.status = status;
    this.version = version;
    this.date = date;
  }


  public int getBugID() {
    return bugID;
  }


  public String getBugIDAsString() {
    return Integer.toString(bugID);
  }


  public String getProduct() {
    return product;
  }


  public String getShortDescr() {
    return shortDescr;
  }


  public String getStatus() {
    return status;
  }


  public String getVersion() {
    return version;
  }


  public Date getDate() {
    return date;
  }


  public String toString() {
    return "BugzillaBug{" +
      "bugID=" + bugID +
      ", shortDescr='" + shortDescr + '\'' +
      ", product='" + product + '\'' +
      ", version='" + version + '\'' +
      ", status='" + status + '\'' +
      ", date=" + date +
      '}';
  }
}
