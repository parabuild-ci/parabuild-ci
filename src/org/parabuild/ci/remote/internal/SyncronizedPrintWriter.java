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
package org.parabuild.ci.remote.internal;

import org.parabuild.ci.process.Tailer;
import org.parabuild.ci.process.ZeroLengthTailer;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 */
public final class SyncronizedPrintWriter extends PrintWriter {

  private final Tailer tailer;


  public SyncronizedPrintWriter(final OutputStream out) {
    this(out, new ZeroLengthTailer());
  }


  public SyncronizedPrintWriter(final OutputStream out, final Tailer tailer) {
    super(out);
    this.tailer = tailer;
  }


  public synchronized void println(final String line) { // NOPMD
    super.println(line);
    tailer.addLine(line);
  }
}
