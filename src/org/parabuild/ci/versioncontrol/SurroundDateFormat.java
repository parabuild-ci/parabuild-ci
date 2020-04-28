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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Provides date formats according to Locale
 */
final class SurroundDateFormat {

//  static {
//    INPUT_DATE_TIME_FORMATTER_US.setTimeZone(TimeZone.getTimeZone("GMT"));
//    INPUT_DATE_TIME_FORMATTER_EU.setTimeZone(TimeZone.getTimeZone("GMT"));
//  }


  private final DateFormat dateFormat;
  private final Locale locale;
  private final DateFormat timeFormat;


  /**
   * Constructor.
   *
   * @param locale
   */
  public SurroundDateFormat(final Locale locale) {
    this.locale = locale;
    this.dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
    this.timeFormat = new SimpleDateFormat("h:mm a");
  }


  /**
   * Parses given date string and time string from Surround
   * Change log.
   *
   * @param stringDate
   * @param stringTime
   *
   * @return
   *
   * @throws ParseException
   */
  public Date parse(final String stringDate, final String stringTime) throws ParseException {
    final Calendar date = Calendar.getInstance(locale);
    date.setTime(dateFormat.parse(stringDate));
    final Calendar time = Calendar.getInstance(locale);
    time.setTime(timeFormat.parse(stringTime));
    final Calendar result = Calendar.getInstance();
    result.clear();
    result.set(Calendar.YEAR, date.get(Calendar.YEAR));
    result.set(Calendar.MONTH, date.get(Calendar.MONTH));
    result.set(Calendar.DATE, date.get(Calendar.DATE));
    result.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
    result.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
    return result.getTime();
  }


  public String toString() {
    return "SurroundDateFormat{" +
      "dateFormat=" + ((dateFormat instanceof SimpleDateFormat)? ((SimpleDateFormat)dateFormat).toPattern() : dateFormat.toString()) +
      ", locale=" + locale +
      ", timeFormat=" + ((timeFormat instanceof SimpleDateFormat)? ((SimpleDateFormat)timeFormat).toPattern() : timeFormat.toString()) +
      '}';
  }
}
