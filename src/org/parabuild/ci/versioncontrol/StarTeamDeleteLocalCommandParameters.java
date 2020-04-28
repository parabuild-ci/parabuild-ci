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
package org.parabuild.ci.versioncontrol;

import java.util.Date;

/**
 *
 */
final class StarTeamDeleteLocalCommandParameters extends StarTeamCommandParameters {

  private Date viewConfigDate = null;


  public Date getConfigDate() {
    return viewConfigDate;
  }


  public void setViewConfigDate(final Date viewConfigDate) {
    this.viewConfigDate = (Date)viewConfigDate.clone();
  }
}
