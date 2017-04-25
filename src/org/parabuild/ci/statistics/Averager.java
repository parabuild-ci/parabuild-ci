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

/**
 *
 */
public final class Averager {

  private double average = 0;
  private long counter = 0;


  public Averager() {
  }


  public Averager(final long average, final long counter) {
    this.average = average;
    this.counter = counter;
  }


  public double add(final long toAdd) {
    if (counter == 0) {
      average = toAdd;
      ++counter;
    } else {
      average = (average * counter + toAdd) / ++counter;
    }
    return average;
  }


  public long getAverage() {
    return (long)average;
  }
}
