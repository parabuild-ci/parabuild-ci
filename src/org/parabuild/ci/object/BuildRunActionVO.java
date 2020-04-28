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
package org.parabuild.ci.object;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Build run action log useable for reporting.
 */
public final class BuildRunActionVO implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -4591276259024162394L; // NOPMD

  private String action = null;
  private Date date = null;
  private String description = null;
  private String user = null;


  /**
   */
  public String getAction() {
    return action;
  }


  public void setAction(final String action) {
    this.action = action;
  }


  /**
   */
  public Date getDate() {
    return date;
  }


  public void setDate(final Date date) {
    this.date = date;
  }


  /**
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  public String getUser() {
    return user;
  }


  public void setUser(final String user) {
    this.user = user;
  }


  public String getDateAsString(final SimpleDateFormat dateTimeFormat) {
    return dateTimeFormat.format(date);
  }


  public String toString() {
    return "BuildRunActionVO{" +
      "action='" + action + '\'' +
      ", date=" + date +
      ", description='" + description + '\'' +
      ", user='" + user + '\'' +
      '}';
  }
}
