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
package org.parabuild.ci.security;

/**
 * BadRequestException means that a request for secured
 * information was malformed.
 */
public final class BadRequestException extends Exception {

  private static final long serialVersionUID = 403406408226285556L;


  /**
   * Constructs a new exception with the specified detail
   * message.  The cause is not initialized, and may subsequently
   * be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is
   * saved for later retrieval by the {@link #getMessage()}
   * method.
   */
  public BadRequestException(final String message) {
    super(message);
  }
}
