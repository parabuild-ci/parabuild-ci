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
 * Date formatter specific to Vault
 */
class VaultDateFormat {

  private final DateFormat inputFormat;
  private final DateFormat outputFormat;


  /**
   * Constructor.
   *
   * @param locale
   *
   */
  public VaultDateFormat(final Locale locale) {
    inputFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.MEDIUM, locale);
    outputFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
  }


  /**
   * Formats date to Vault's input date format.
   */
  String formatInput(final Date date) {
    return inputFormat.format(date);
  }


  /**
   * Formats date to Vault's input date format.
   */
  Date parseOutput(final String s) throws ParseException {
    return outputFormat.parse(s);
  }
}
