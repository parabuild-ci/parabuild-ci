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
 * Date formatter specific to PVCS
 */
final class PVCSDateFormat {

  private final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("MMM dd, yyyy hh:mm a"); // NOPMD SingularField
  private final SimpleDateFormat LONG_INPUT_FORMAT = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a"); // NOPMD SingularField
  private final SimpleDateFormat OUTPUT_FORMAT_1 = new SimpleDateFormat("MMM dd yyyy HH:mm:ss"); // NOPMD SingularField
  private final SimpleDateFormat OUTPUT_FORMAT_2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss"); // NOPMD SingularField

  private final DateFormat inputFormat;
  private final DateFormat outputFormat;


  /**
   * Constructor.
   *
   * @param locale
   */
  public PVCSDateFormat(final Locale locale) {
//    inputFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM, locale);
    if (locale.getCountry().equals(Locale.US.getCountry().toUpperCase())
      || locale.getCountry().equals(Locale.CANADA.getCountry().toUpperCase())) {
      inputFormat = INPUT_FORMAT;
      outputFormat = OUTPUT_FORMAT_1;
    } else {
      inputFormat = INPUT_FORMAT;
      outputFormat = OUTPUT_FORMAT_2;
    }
  }


  /**
   * Formats date to PVCS's input date format.
   */
  String formatInput(final Date date) {
    return inputFormat.format(date);
  }


  /**
   * Formats date to PVCS's long input date format.
   */
  String formatLongInput(final Date date) {
    return LONG_INPUT_FORMAT.format(date);
  }


  /**
   * Formats date to PVCS's input date format.
   */
  Date parseOutput(final String s) throws ParseException {
    return outputFormat.parse(s);
  }
}
