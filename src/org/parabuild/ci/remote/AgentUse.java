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
package org.parabuild.ci.remote;

import org.parabuild.ci.configuration.AgentHost;

import java.util.Comparator;

/**
 * Keeps agent use counters.
 */
final class AgentUse {

  public static final Comparator REVERSE_ORDER_USE_COMPARATOR = new Comparator() {

    public int compare(final Object o, final Object o1) {

      if (o instanceof AgentUse && o1 instanceof AgentUse) {

        final AgentUse au = (AgentUse) o;
        final AgentUse au1 = (AgentUse) o1;
        if ((double) au.checkoutCounter / (double) au.capacity > (double) au1.checkoutCounter / (double) au1.capacity) {
          return 1;
        }
        if ((double) au.checkoutCounter / (double) au.capacity < (double) au1.checkoutCounter / (double) au1.capacity) {
          return -1;
        }
        if ((double) au.totalCheckouts / (double) au.capacity > (double) au1.totalCheckouts / (double) au1.capacity) {
          return 1;
        }
        if ((double) au.totalCheckouts / (double) au.capacity < (double) au1.totalCheckouts / (double) au1.capacity) {
          return -1;
        }
        return 0;
      } else {
        return -1;
      }
    }
  };


  private int checkoutCounter;

  private int totalCheckouts;

  private int capacity = 1;

  /**
   * Max concurrent builds, '0' means unlimited.
   */
  private int maxConcurrentBuilds;

  private final AgentHost agentHost;


  AgentUse(final AgentHost agentHost) {

    this.agentHost = agentHost;
  }


  final void decrementCheckoutCounter() {

    checkoutCounter--;

    if (checkoutCounter < 0) {

      checkoutCounter = 0;
    }
  }


  final void incrementCheckoutCounter() {

    checkoutCounter++;

    totalCheckouts++;
  }


  public int getCheckoutCounter() {

    return checkoutCounter;
  }


  public int getTotalCheckouts() {

    return totalCheckouts;
  }


  public AgentHost getAgentHost() {

    return agentHost;
  }


  public int getCapacity() {
    return capacity;
  }


  public void setCapacity(final int capacity) {
    this.capacity = capacity;
  }


  public void setMaxConcurrentBuilds(final int maxConcurrentBuilds) {

    this.maxConcurrentBuilds = maxConcurrentBuilds;
  }


  public int getMaxConcurrentBuilds() {

    return maxConcurrentBuilds;
  }


  public boolean isUnlimitedConcurrentBuilds() {

    return maxConcurrentBuilds == 0;
  }


  public String toString() {

    return "AgentUse{" +
            "agentHost=" + agentHost +
            ", checkoutCounter=" + checkoutCounter +
            ", totalCheckouts=" + totalCheckouts +
            '}';
  }
}
