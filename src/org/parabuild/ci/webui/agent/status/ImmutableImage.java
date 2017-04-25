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
package org.parabuild.ci.webui.agent.status;

import java.io.IOException;
import java.io.OutputStream;

/**
 * ImmutableImage
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 24, 2009 7:47:16 PM
 */
public final class ImmutableImage {

  public static final ImmutableImage ZERO_SIZE_IMAGE = new ImmutableImage(new byte[]{}, 0, 0);

  private final byte[] bytes;
  private int width;
  private int height;
  private int imageLegth;


  public ImmutableImage(final byte[] bytes, final int width, final int height) {
    this.width = width;
    this.height = height;
    this.bytes = new byte[bytes.length];
    System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
    this.imageLegth = bytes.length;
  }


  public int getWidth() {
    return width;
  }


  public int getHeight() {
    return height;
  }


  public void write(final OutputStream out) throws IOException {
    out.write(bytes);
  }


  public int getImageLegth() {
    return imageLegth;
  }


  public String toString() {
    return "ImmutableImage{" +
            "bytes=" + (bytes == null ? null : Integer.toString(bytes.length)) +
            '}';
  }
}
