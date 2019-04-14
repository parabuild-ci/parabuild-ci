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
 */
public final class TailBufferSizeImpl implements TailBufferSize {

  private static final int DEFAULT_MAX_LINE_COUNT = 100;
  private static final int DEFAULT_MAX_LINE_LENGTH = 200;

  private final int maxLineCount; // required by Hessian to staty non-final
  private final int maxLineLength;  // required by Hessian to staty non-final


  public TailBufferSizeImpl() {
    this(DEFAULT_MAX_LINE_COUNT, DEFAULT_MAX_LINE_LENGTH);
  }


  public TailBufferSizeImpl(final int maxLineCount, final int maxLineLength) {
    this.maxLineCount = maxLineCount;
    this.maxLineLength = maxLineLength;
  }


  public int getMaxLineCount() {
    return maxLineCount;
  }


  public int getMaxLineLength() {
    return maxLineLength;
  }


  public String toString() {
    return "TailBufferSizeImpl{" +
      "maxLineCount=" + maxLineCount +
      ", maxLineLength=" + maxLineLength +
      '}';
  }
}
