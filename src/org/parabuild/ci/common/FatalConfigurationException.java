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
package org.parabuild.ci.common;



/**
 * This exception is thrown when the application faces unrecoverable
 * configuration errors.
 */
public final class FatalConfigurationException extends Exception {

  private static final long serialVersionUID = 5147709992364635613L;


  /**
   */
  public FatalConfigurationException(final String message) {
    super(message);
  }


  /**
   */
  public FatalConfigurationException(final String message, final Throwable cause) {
    super(message, cause);
  }


  /**
   */
  public FatalConfigurationException(final Throwable cause) {
    this(StringUtils.toString(cause), cause);
  }
}
