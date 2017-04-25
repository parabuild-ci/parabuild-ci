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
package org.parabuild.ci.versioncontrol.perforce;

/**
 * Integration object
 */
public class Integration {

  private final Revision from;
  private final Revision to;
  private final String operation;


  public Integration(final Revision from, final Revision to, final String operation) {
    this.from = from;
    this.to = to;
    this.operation = operation;
  }


  public Revision getFrom() {
    return from;
  }


  /**
   * Can be null.
   *
   * @return target revision or null if not set.
   */
  public Revision getTo() {
    return to;
  }


  public String getOperation() {
    return operation;
  }


  public String toString() {
    return "Integration{" +
      "from=" + from +
      ", to=" + to +
      ", operation='" + operation + '\'' +
      '}';
  }
}
