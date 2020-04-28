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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Tails log.
 */
final class TailerImpl implements Tailer {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(TailerImpl.class); // NOPMD

  private final CyclicBuffer cyclicBuffer;
  private final TailBufferSize tailBufferSize;
  private long lineNumber;


  public TailerImpl(final TailBufferSize tailBufferSize) {
    this.tailBufferSize = tailBufferSize;
    this.cyclicBuffer = new CyclicBuffer(tailBufferSize.getMaxLineCount());
  }


  /**
   * @param line line to add
   */
  public synchronized void addLine(final String line) {
    // REVIEWME: consider r/w lock
    final String lineToAdd;
    if (line.length() >= tailBufferSize.getMaxLineLength()) {
      lineToAdd = line.substring(0, tailBufferSize.getMaxLineLength());
    } else {
      lineToAdd = line;
    }
    cyclicBuffer.add(new TailLineImpl(lineNumber++, System.currentTimeMillis(), lineToAdd));
//    if (log.isDebugEnabled()) log.debug("line: " + line);
  }


  /**
   * @return a copy of the lines stored in the cyclic buffer.
   */
  public synchronized List getLines() {
    return cyclicBuffer.getAll();
  }


  /**
   * @return current line number
   */
  public long getLineNumber() {
    return lineNumber;
  }


  public String toString() {
    return "TailerImpl{" +
      "cyclicBuffer=" + cyclicBuffer +
      ", tailBufferSize=" + tailBufferSize +
      ", lineNumber=" + lineNumber +
      '}';
  }
}
