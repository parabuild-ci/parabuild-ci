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

import java.io.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;
import EDU.oswego.cs.dl.util.concurrent.*;

/**
 * This class performs copying of a character InputStream to a
 * file. The main intent of the InputStreamToFileCopier is to
 * copy stdout and stderr streams of the execute method to files
 * for future processing.
 * <p/>
 * This class implements Runnable interface and can be used to to
 * copy input stream while running in a thread.
 */
public class InputStreamToFileCopier implements Runnable {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(InputStreamToFileCopier.class); // NOPMD

  public static final int INPUT_BUFFER_SIZE = 512;
  public static final int OUTPUT_BUFFER_SIZE = 512;
  public static final long SLEEP_MILLIS = 5;

  private InputStream is = null;
  protected OutputStream os = null;

  private final Latch finishLatch = new Latch();


  /**
   * Constructor
   *
   * @param is InputStream to read
   * @param outputFile
   */
  public InputStreamToFileCopier(final InputStream is, final File outputFile) throws FileNotFoundException {
    this(is, outputFile != null ? (OutputStream)new FileOutputStream(outputFile) : (OutputStream)new NullOutputStream());
  }


  /**
   * Constructor
   *
   * @param is InputStream to read
   * @param output to write to
   */
  public InputStreamToFileCopier(final InputStream is, final OutputStream output) {
    this.is = is;
    this.os = new BufferedOutputStream(output, OUTPUT_BUFFER_SIZE);
  }


  /**
   * Implementation of the Runnable interface.
   */
  public final void run() {
    try {
      copyInputStreamToFile();
    } catch (IOException e) {
      throw IoUtils.makeIllegalStateException(e);
    } finally {
      finishLatch.release();
    }
  }


  /**
   * Performs copying of the stream to a file. This method exits
   * when the inputs stream doesn't have more data. It does not
   * close input stream and it is responsibility of a caller to
   * close it.
   *
   * @throws IOException
   */
  public void copyInputStreamToFile() throws IOException {
    try {
      final byte[] buffer = new byte[INPUT_BUFFER_SIZE];
      int length;
      while ((length = is.read(buffer)) > 0) {
        os.write(buffer, 0, length);
      }
      os.flush();
    } finally {
      close();
    }
  }


  protected final InputStream getInputStream() {
    return is;
  }


  /**
   * Closes input and output streams if any
   */
  public final void close() {
    IoUtils.closeHard(os);
    IoUtils.closeHard(is);
    os = null;
    is = null;
  }


  public final Latch getFinishLatch() {
    return finishLatch;
  }
}


