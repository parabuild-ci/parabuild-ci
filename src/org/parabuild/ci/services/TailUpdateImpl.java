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
package org.parabuild.ci.services;

import java.util.*;

/**
 *  Implementation of TailUpdate interface.
 */
public class TailUpdateImpl implements TailUpdate {

  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  public static final TailUpdate EMPTY_UPDATE = new TailUpdateImpl(0, EMPTY_STRING_ARRAY);

  private final long timeStamp;
  private final String[] logLines;


  public TailUpdateImpl(final long timeStamp, final String[] logLines) {
    this.timeStamp = timeStamp;
    this.logLines = logLines;
  }


  /**
   * @return current time stamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * @return long lines
   */
  public String[] getLogLines() {
    return logLines;
  }


  /**
   * @return number of lines
   */
  public int getLineCount() {
    return logLines.length;
  }


  public String toString() {
    return "TailUpdateImpl{" +
      "timeStamp=" + timeStamp +
      ", logLines=" + (logLines == null ? null : Arrays.asList(logLines)) +
      '}';
  }
}
