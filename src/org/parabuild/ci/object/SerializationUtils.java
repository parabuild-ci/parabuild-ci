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
package org.parabuild.ci.object;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * SerializationUtils
 * <p/>
 *
 * @author Slava Imeshev
 * @since Nov 20, 2009 3:06:56 PM
 */
final class SerializationUtils {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SerializationUtils.class); // NOPMD


  private SerializationUtils() {
  }


  /**
   * Reads a nullable UTF string from the <code>in</code> stream. The string should be writtern by the matching <code>writeNullableUTF()</code>
   *
   * @param in <code>ObjectInput</code> input stream
   * @return a nullable UTF string from the <code>in</code> stream
   * @throws IOException if an IO error occurred.
   * @see #writeNullableUTF(String, ObjectOutput)
   */
  static String readNullableUTF(final ObjectInput in) throws IOException {
    if (in.readBoolean()) {
      return null;
    } else {
      return in.readUTF();
    }
  }


  static void writeNullableUTF(final String utf, final ObjectOutput out) throws IOException {
    if (utf == null) {
      out.writeBoolean(true);
    } else {
      out.writeBoolean(false);
      out.writeUTF(utf);
    }
  }
}
