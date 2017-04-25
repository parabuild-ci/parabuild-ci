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
 * This exception is thrown by methods when user input validation fails.
 */
public final class ValidationException extends Exception {

  /**
   * Constructs a new exception with the specified user friendly message.
   */
  public ValidationException(final String userFriendlyMessage) {
    super(userFriendlyMessage);
  }
}
