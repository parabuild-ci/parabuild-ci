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
package org.parabuild.ci.versioncontrol.clearcase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ClearCaseStartDate
 * <p/>
 *
 * @author Slava Imeshev
 * @since Sep 16, 2008 6:55:56 PM
 */
public final class ClearCaseStartDate {

  private static final Log LOG = LogFactory.getLog(ClearCaseStartDate.class); // NOPMD

  private static final String START_DATE_FORMAT = "yyyy-MM-dd";
  private final String value;

  /**
   * Constructor.
   */
  public ClearCaseStartDate() {
    final SimpleDateFormat startDateFormat = createStartDateFormat();
    final Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.YEAR, -1);
    value = startDateFormat.format(calendar.getTime());
  }


  /**
   * Parses the given start date using the start date format defined by {@link #START_DATE_FORMAT}.
   *
   * @param stringStartDate
   * @return
   * @throws ParseException
   */
  public static Date parse(final String stringStartDate) throws ParseException {
    final SimpleDateFormat startDateFormat = createStartDateFormat();
    return startDateFormat.parse(stringStartDate);
  }

  /**
   * Returns string value.
   *
   * @return string value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Creates start date format object.
   *
   * @return start date format object.
   */
  private static SimpleDateFormat createStartDateFormat() {
    return new SimpleDateFormat(START_DATE_FORMAT);
  }

  public String toString() {
    return "ClearCaseStartDate{" +
            "value='" + value + '\'' +
            '}';
  }
}
