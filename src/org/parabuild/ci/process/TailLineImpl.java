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
package org.parabuild.ci.process;

/**
 * Holds log tail line.
 */
public final class TailLineImpl implements TailLine {

  private final long timeStamp;
  private final long lineNumber;
  private final String line;


  /**
   * Constructor
   *
   * @param timeStamp
   @param line
   */
  public TailLineImpl(final long lineNumber, final long timeStamp, final String line) {
    this.timeStamp = timeStamp;
    this.lineNumber = lineNumber;
    this.line = line;
  }


  /**
   * @return line time stamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public String getLine() {
    return line;
  }


  public long getLineNumber() {
    return lineNumber;
  }


  public String toString() {
    return "TailLineImpl{" +
      "timeStamp=" + timeStamp +
      ", lineNumber=" + lineNumber +
      ", line='" + line + '\'' +
      '}';
  }
}
