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
package org.parabuild.ci.webui.common;


/**
 * Access dropdown contains a list of build VCS types
 */
public final class DaysAgoDropDown extends CodeNameDropDown {

  public static final int DAYS_ANYTIME = 99999;
  public static final int DAYS_PAST_WEEK = 7;
  public static final int DAYS_PAST_1_MONTH = 31;
  public static final int DAYS_PAST_3_MONTHS = 93;
  public static final int DAYS_PAST_6_MONTHS = 186;
  public static final int DAYS_PAST_12_MONTHS = 365;

  private static final long serialVersionUID = -2982477204607607075L; // NOPMD


  public DaysAgoDropDown() {
    super.addCodeNamePair(DAYS_ANYTIME, "Anytime");
    super.addCodeNamePair(DAYS_PAST_WEEK, "Past week");
    super.addCodeNamePair(DAYS_PAST_1_MONTH, "Past month");
    super.addCodeNamePair(DAYS_PAST_3_MONTHS, "Past 3 months");
    super.addCodeNamePair(DAYS_PAST_6_MONTHS, "Past 6 months");
    super.addCodeNamePair(DAYS_PAST_12_MONTHS, "Past 12 months");
    super.setCode(DAYS_ANYTIME);
  }
}
