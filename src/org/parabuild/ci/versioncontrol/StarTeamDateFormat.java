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
import java.util.Date;
import java.util.Locale;

/**
 * Date formatter specific to StarTeam
 */
final class StarTeamDateFormat {

//  private final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
//  private final SimpleDateFormat OUTPUT_FORMAT_1 = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
//  private final SimpleDateFormat OUTPUT_FORMAT_2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

  private final SimpleDateFormat longInputFormatter = new SimpleDateFormat(LONG_INPUT_FORMAT);
  private final DateFormat inputFormat;
  private final DateFormat outputFormat;
  private static final String LONG_INPUT_FORMAT = "MMM dd, yyyy hh:mm:ss a";


  /**
   * Constructor.
   *
   * @param locale
   */
  public StarTeamDateFormat(final Locale locale) {
    inputFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.FULL, locale);
    outputFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.FULL, locale);

//    if (locale.getCountry().equals(Locale.US.getCountry().toUpperCase())
//      || locale.getCountry().equals(Locale.CANADA.getCountry().toUpperCase())) {
//      inputFormat = INPUT_FORMAT;
//      outputFormat = OUTPUT_FORMAT_1;
//    } else {
//      inputFormat = INPUT_FORMAT;
//      outputFormat = OUTPUT_FORMAT_2;
//    }
  }


  /**
   * Formats date to StartTeam input date format.
   */
  String formatInput(final Date date) {
    return inputFormat.format(date);
  }


  /**
   * Formats date to StartTeam long input date format.
   */
  String formatLongInput(final Date date) {
    return longInputFormatter.format(date);
  }


  /**
   * Formats date to StartTeam input date format.
   */
  Date parseOutput(final String s) throws ParseException {
    return outputFormat.parse(s);
  }
}
