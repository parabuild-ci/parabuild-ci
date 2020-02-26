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
package org.parabuild.ci.remote.internal;

import java.io.*;
import java.util.*;

import org.parabuild.ci.util.*;

/**
 * This object holds Locale data to pass via Hessian
 * serilization.
 */
public final class LocaleData implements Serializable {

  private static final long serialVersionUID = -8907298511657059064L; // NOPMD

  private String language = null;
  private String country = null;
  private String variant = null;


  /**
   * Default constructor.
   */
  public LocaleData() {
  }


  /**
   * Constructs locale data from language, country and variant.
   */
  public LocaleData(final String language, final String country, final String variant) {
    this.language = language;
    this.country = country;
    this.variant = variant;
    ArgumentValidator.validateArgumentNotNull(language, "language");
    ArgumentValidator.validateArgumentNotNull(country, "country");
    ArgumentValidator.validateArgumentNotNull(variant, "variant");
  }


  /**
   * @return new Locale object based on this LocaleDate.
   */
  public Locale getLocale() {
    return new Locale(language, country, variant);
  }
}
