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

import org.parabuild.ci.webui.common.CommonDropDown;

/**
 * Dropdown to show available date formats
 */
public final class DateFormatDropdown extends CommonDropDown {

  private static final long serialVersionUID = -3638947672551947334L; // NOPMD

  private static final String[] FORMATS = {
    "dd/MM/yyyy",
    "MM/dd/yyyy",
    "dd-MM-yyyy",
    "dd MMM yyyy",
    "MMM dd, yyyy",
    "yyyyMMdd",
    "MM-dd-yyyy",
    "dd MM yyyy",
    "MM dd yyyy",
  };


  public DateFormatDropdown() {
    for (int i = 0; i < FORMATS.length; i++) {
      super.addItem(FORMATS[i]);
    }
  }


  /**
   * @return an array of available formats
   */
  public static String[] getFormats() {
    final String[] result = new String[FORMATS.length];
    System.arraycopy(FORMATS, 0, result, 0, FORMATS.length);
    return result;
  }
}


/*
 Symbol   Meaning                 Presentation        Example
 ------   -------                 ------------        -------
 G        era designator          (Text)              AD
 y        year                    (Number)            1996
 M        month in year           (Text & Number)     July & 07
 d        day in month            (Number)            10
 h        hour in am/pm (1~12)    (Number)            12
 H        hour in day (0~23)      (Number)            0
 m        minute in hour          (Number)            30
 s        second in minute        (Number)            55
 NAME_RUNNER_TYPE        millisecond             (Number)            978
 E        day in week             (Text)              Tuesday
 D        day in year             (Number)            189
 F        day of week in month    (Number)            2 (2nd Wed in July)
 w        week in year            (Number)            27
 W        week in month           (Number)            2
 a        am/pm marker            (Text)              PM
 k        hour in day (1~24)      (Number)            24
 K        hour in am/pm (0~11)    (Number)            0
 z        time zone               (Text)              Pacific Standard Time
 '        escape for text         (Delimiter)
 ''       single quote            (Literal)           '
*/