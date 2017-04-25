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
package org.parabuild.ci.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ScheduleProperty;

/**
 * Decides when to make a clean checkout.
 */
public final class CleanCheckoutCounter {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(CleanCheckoutCounter.class); // NOPMD
  private static final int DEFAULT_CLEAN_CHECKOUT = 1;

  private int cleanCheckoutCounter = 0; // NOPMD
  private final int activeBuildID;
  private boolean forceNextCheckoutClean = false;


  /**
   * Constructor.
   *
   * @param activeBuildID active build ID
   */
  public CleanCheckoutCounter(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * @return true if the next build run should be clean
   */
  public synchronized boolean increment() {
    if (forceNextCheckoutClean) {
      forceNextCheckoutClean = false;
      return true;
    }

    // adjust counter
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int cleanCheckoutEvery = cm.getScheduleSettingValue(activeBuildID,
            ScheduleProperty.AUTO_CLEAN_CHECKOUT, DEFAULT_CLEAN_CHECKOUT);
//    if (log.isDebugEnabled()) log.debug("cleanCheckoutEvery: " + cleanCheckoutEvery);
//    if (log.isDebugEnabled()) log.debug("cleanCheckoutCounter: " + cleanCheckoutCounter);
    cleanCheckoutCounter++;
    boolean cleanCheckout = false;
    if (cleanCheckoutEvery > 0 && cleanCheckoutCounter >= cleanCheckoutEvery) {
      cleanCheckoutCounter = 0;
      cleanCheckout = true;
    }

    return cleanCheckout;
  }


  public synchronized void forceNextCheckoutClean() {
    this.forceNextCheckoutClean = true;
  }


  public String toString() {
    return "CleanCheckoutCounter{" +
            "cleanCheckoutCounter=" + cleanCheckoutCounter +
            ", activeBuildID=" + activeBuildID +
            ", forceNextCheckoutClean=" + forceNextCheckoutClean +
            '}';
  }
}
