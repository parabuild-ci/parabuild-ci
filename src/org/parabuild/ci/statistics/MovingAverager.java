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
package org.parabuild.ci.statistics;

import java.util.LinkedList;
import java.util.List;

/**
 * Moving average calculator.
 */
public final class MovingAverager {

  private static final int DEFAULT_WINDOW_SIZE = 20;

  private final int windowSize;
  private final LinkedList window = new LinkedList();
  private long average = 0;
  private double sum = 0;


  public MovingAverager(final int windowSize) {
    this.windowSize = windowSize;
  }


  public MovingAverager() {
    this(DEFAULT_WINDOW_SIZE);
  }


  /**
   * Adds a value to the moving average and returns the current average.
   * @return current average.
   */
  public long add(final long value) {
    if (window.size() < windowSize) {
      window.add(new Long(value));
      sum += value;
      average = (long)(sum / window.size());
    } else {
      window.add(new Long(value));
      final Long first = (Long)window.removeFirst();
      sum += value;
      sum -= first;
      average = (long)(sum / window.size());
    }
    return average;
  }


  /**
   * Adds a window. This method is useful to initialize the
   * averager. For the moving averager adding a window
   * bigger than window size doesn't make sense.
   */
  public long addWindow(final List window) {
    for (int i = 0; i < window.size(); i++) {
      add((Long) window.get(i));
    }
    return average;
  }


  /**
   * @return accumulated average.
   */
  public long getAverage() {
    return average;
  }


  /**
   * @return accumulated average.
   */
  public int getWindowSize() {
    return windowSize;
  }
}
