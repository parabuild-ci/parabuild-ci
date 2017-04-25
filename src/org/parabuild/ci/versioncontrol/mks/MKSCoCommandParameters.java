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
package org.parabuild.ci.versioncontrol.mks;

import java.util.Date;

/**
 * Parameters for MKS's co command.
 *
 * @see MKSCommandParameters
 * @see org.parabuild.ci.versioncontrol.mks.MKSCoCommand
 */
public final class MKSCoCommandParameters extends MKSCommandParameters {

  private Date date = null;
  private String inputDateFormat;


  /**
   * Returns date to do check out to
   *
   * @return date to do check out to
   */
  public Date getDate() {
    return date;
  }


  /**
   * Sets sate to do check out to
   */
  public void setDate(final Date date) {
    this.date = date;
  }


  public String getInputDateFormat() {
    return inputDateFormat;
  }


  public void setInputDateFormat(final String inputDateFormat) {
    this.inputDateFormat = inputDateFormat;
  }
}
