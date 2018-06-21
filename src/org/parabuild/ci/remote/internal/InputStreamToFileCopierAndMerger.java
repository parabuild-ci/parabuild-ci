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

import org.parabuild.ci.common.*;

/**
 * This class performs copying of a character InputStream to a file.
 * The main intent of the InputStreamToFileCopier is to copy stdout
 * and stderr streams of the execute method to files for future
 * processing.
 *
 * This class implements Runnable interface and can be used to to copy
 * input stream while running in a thread.
 */
public final class InputStreamToFileCopierAndMerger extends InputStreamToFileCopier {

  private PrintWriter copy = null;


  /**
   * Constructor
   *
   * @param is InputStream to read
   * @param outputFile
   */
  public InputStreamToFileCopierAndMerger(final InputStream is, final File outputFile, final SyncronizedPrintWriter copy) throws FileNotFoundException {
    super(is, outputFile);
    this.copy = copy;
  }


  /**
   * Performs copying of the stream to a file. This method exits when
   * the inputs stream doesn't have more data. It does not close input
   * stream and it is responsibility of a caller to close it.
   *
   * @throws IOException
   */
  public void copyInputStreamToFile() throws IOException {
    final PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(getInputStream()), INPUT_BUFFER_SIZE);
      String read = br.readLine();
      while (read != null) {
        fileWriter.println(read);
        if (copy != null) copy.println(read);
        try {
          Thread.sleep(SLEEP_MILLIS);
        } catch (final InterruptedException e) {
          IoUtils.ignoreExpectedException(e);
        }
        read = br.readLine();
      }
      fileWriter.flush();
    } finally {
      IoUtils.closeHard(fileWriter);
      IoUtils.closeHard(br);
      close();
    }
  }
}


