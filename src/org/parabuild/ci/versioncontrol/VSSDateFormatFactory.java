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

import java.text.*;
import java.util.*;

/**
 * Provides date formats according to Locale
 */
final class VSSDateFormatFactory {

  private static final String VSS_DATE_FORMAT_US = "MM/dd/yy";
  private static final String VSS_TIME_FORMAT_US = "hh:mma";
  private static final String VSS_DATE_FORMAT_EU = "dd/MM/yy";
  private static final String VSS_TIME_FORMAT_EU = "HH:mm";

  private final SimpleDateFormat OUTPUT_DATE_TIME_FORMATTER_US = new SimpleDateFormat(VSS_DATE_FORMAT_US + ';' + VSS_TIME_FORMAT_US, Locale.US); // NOPMD
  private final SimpleDateFormat OUTPUT_DATE_TIME_FORMATTER_EU = new SimpleDateFormat(VSS_DATE_FORMAT_EU + ';' + VSS_TIME_FORMAT_EU, Locale.US); // NOPMD
  private final SimpleDateFormat INPUT_DATE_TIME_FORMATTER_US = new SimpleDateFormat("'Date: '" + VSS_DATE_FORMAT_US + "   'Time: '" + VSS_TIME_FORMAT_US, Locale.US); // NOPMD
  private final SimpleDateFormat INPUT_DATE_TIME_FORMATTER_EU = new SimpleDateFormat("'Date: '" + VSS_DATE_FORMAT_EU + "   'Time: '" + VSS_TIME_FORMAT_EU, Locale.US); // NOPMD
  private final SimpleDateFormat outputDateTimeFormat;
  private final SimpleDateFormat inputDateTimeFormat;
  private final String country;


  /**
   * Constructor.
   *
   * @param locale
   */
  public VSSDateFormatFactory(final Locale locale) {
    country = locale.getCountry();
    if (isEUFormat()) {
      outputDateTimeFormat = OUTPUT_DATE_TIME_FORMATTER_EU;
      inputDateTimeFormat = INPUT_DATE_TIME_FORMATTER_EU;
    } else {
      outputDateTimeFormat = OUTPUT_DATE_TIME_FORMATTER_US;
      inputDateTimeFormat = INPUT_DATE_TIME_FORMATTER_US;
    }
  }


  public boolean isEUFormat() {
    // REVIEWME: simeshev@parabuilci.org -> The list is very limited - we may want to
    // go over the full lists of ISO countries.
    final String countryUpperCase = country.toUpperCase();
    return !(countryUpperCase.equals(Locale.US.getCountry().toUpperCase())
      || countryUpperCase.equals(Locale.CANADA.getCountry().toUpperCase()))
      ;
    //return countryUpperCase.equals(Locale.CANADA_FRENCH.getCountry().toUpperCase())
    //          || countryUpperCase.equals(Locale.FRANCE.getCountry().toUpperCase())
    //          || countryUpperCase.equals(Locale.GERMANY.getCountry().toUpperCase())
    //          || countryUpperCase.equals(Locale.ITALY.getCountry().toUpperCase())
    //  ;
  }


  public SimpleDateFormat outputDateTimeFormat() {
    return outputDateTimeFormat;
  }


  public SimpleDateFormat outputDateTimeFormatUS() {
    return OUTPUT_DATE_TIME_FORMATTER_US;
  }


  public SimpleDateFormat inputDateTimeFormat() {
    return inputDateTimeFormat;
  }
}
