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
package org.parabuild.ci.util;

import java.io.*;

/**
 * This input stream will not call it's delegate close() method
 * if it is already closed.
 */
public final class CloseAwareInputStream extends FilterInputStream {

  private boolean closed = false; // NOPMD  (SingularField)


  /**
   * Constructor.
   */
  public CloseAwareInputStream(final InputStream in) {
    super(in);
  }


  /**
   * Closes this input stream and releases any system resources
   * associated with the stream. This method simply performs
   * <code>in.close()</code>.
   *
   * @throws IOException if an I/O error occurs.
   * @see FilterInputStream#in
   */
  public synchronized void close() throws IOException {
    if (!closed) {
      super.close();
    }
    closed = true;
  }


  public String toString() {
    return "CloseAwareInputStream{" +
      "closed=" + closed +
      '}';
  }
}
