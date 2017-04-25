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

import java.util.*;

import org.parabuild.ci.common.ArgumentValidator;

/**
 * Cyclic buffer.
 */
final class CyclicBuffer {

  private Object[] buffer;
  private int first;
  private int last;
  private int size;
  private int maxSize;


  /**
   * Instantiate a new CyclicBuffer of at most maxSize
   * elements. The maxSize argument must a positive integer.
   *
   * @param maxSize The maximum number of elements in the
   * buffer.
   */
  public CyclicBuffer(final int maxSize) throws IllegalArgumentException {
    ArgumentValidator.validateArgumentGTZero(maxSize, "max size");
    this.maxSize = maxSize;
    this.buffer = new Object[maxSize];
    this.first = 0;
    this.last = 0;
    this.size = 0;
  }


  /**
   * Add an object as the last object in the buffer.
   */
  public final void add(final Object object) {
    buffer[last] = object;
    if (++last == maxSize) {
      last = 0;
    }
    if (size < maxSize) {
      size++;
    } else if (++first == maxSize) {
      first = 0;
    }
  }


  /**
   * Get the ith oldest object currently in the buffer. If i
   * is outside the range 0 to the number of elements
   * currently in the buffer, then null is returned.
   */
  public final Object get(final int i) {
    if (i < 0 || i >= size) return null;
    return buffer[(first + i) % maxSize];
  }


  public final int getMaxSize() {
    return maxSize;
  }


  /**
   * Get the oldest (first) element in the buffer. The
   * oldest element is removed from the buffer.
   */
  public final Object get() {
    Object r = null;
    if (size > 0) {
      size--;
      r = buffer[first];
      buffer[first] = null;
      if (++first == maxSize) first = 0;
    }
    return r;
  }


  /**
   * Get the number of elements in the buffer. This number
   * is guaranteed to be in the range 0 to maxSize
   * (inclusive).
   */
  public final int length() {
    return size;
  }


  /**
   * Resize the cyclic buffer to newSize.
   *
   * @throws IllegalArgumentException if newSize is negative.
   */
  public final void resize(final int newSize) {
    ArgumentValidator.validateArgumentGTZero(newSize, "new size");
    if (newSize == size) return; // nothing to do

    final Object[] temp = new Object[newSize];

    final int loopLen = newSize < size ? newSize : size;

    for (int i = 0; i < loopLen; i++) {
      temp[i] = buffer[first];
      buffer[first] = null;
      if (++first == size)
        first = 0;
    }
    buffer = temp;
    first = 0;
    size = loopLen;
    maxSize = newSize;
    if (loopLen == newSize) {
      last = 0;
    } else {
      last = loopLen;
    }
  }


  /**
   * @return a copy of elements in the buffer.
   */
  public List getAll() {
    final List result = new ArrayList(size);
    for (int i = 0; i < size; i++) {
      result.add(buffer[(first + i) % maxSize]);
    }
    return result;
  }


  public String toString() {
    return "CyclicBuffer{" +
      "buffer=" + (buffer == null ? null : Arrays.asList(buffer)) +
      ", first=" + first +
      ", last=" + last +
      ", size=" + size +
      ", maxSize=" + maxSize +
      '}';
  }
}