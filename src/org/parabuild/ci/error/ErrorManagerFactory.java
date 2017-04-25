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
package org.parabuild.ci.error;

import org.parabuild.ci.error.impl.*;

/**
 * This class separates client of ErrorManager interface from its
 * implementation.
 */
public final class ErrorManagerFactory {

  private static ErrorManagerImpl errorManager = new ErrorManagerImpl();


  /**
   * Returns instance implementing ErrorManager interface
   *
   * @return ErrorManager
   * @see ErrorManager
   */
  public static ErrorManager getErrorManager() {
    if (errorManager == null) {
      initErrorManager();
    }
    return errorManager;
  }


  private static synchronized void initErrorManager() {
    if (errorManager == null) {
      errorManager = new ErrorManagerImpl();
    }
  }
}
