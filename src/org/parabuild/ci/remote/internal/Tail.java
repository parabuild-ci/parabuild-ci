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

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.process.TailLine;
import org.parabuild.ci.process.OsCommand;
import org.parabuild.ci.services.TailUpdate;
import org.parabuild.ci.services.TailUpdateImpl;

/**
 */
public final class Tail {

  /** @noinspection UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(Tail.class); // NOPMD

  private static final Comparator LOG_TAIL_LINE_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      final TailLine line1 = (TailLine)o1;
      final TailLine line2 = (TailLine)o2;
      return Long.compare(line1.getTimeStamp(), line2.getTimeStamp());
    }
  };

  private final int commandHandle;


  public Tail(final int commandHandle) {
    this.commandHandle = commandHandle;
  }


  public TailUpdate getTailUpdate(final long sinceServerTimeMs) {
//    if (log.isDebugEnabled()) log.debug("commandHandle: " + commandHandle);
//    if (log.isDebugEnabled()) log.debug("sinceServerTimeMs: " + sinceServerTimeMs);
    // get command
    final OsCommand command = OsCommand.getCommand(commandHandle);
//    if (log.isDebugEnabled()) log.debug("command: " + command);
    if (command == null) return TailUpdateImpl.EMPTY_UPDATE;

    // get lines
    final List logTailLines = command.getLogTailLines();
    final int lineCount = logTailLines.size();
    if (lineCount == 0) return TailUpdateImpl.EMPTY_UPDATE;

    // search
    int index = Collections.binarySearch(logTailLines, new TailLine() {

      public long getTimeStamp() {
        return sinceServerTimeMs; /* Important */
      }


      public String getLine() {
        return null;  /* Not used in comparison */
      }


      public long getLineNumber() {
        return 0; /* Not used in comparison */
      }
    }, LOG_TAIL_LINE_COMPARATOR);

    if (index == -1) index = 0; // not found, start from the beginning

    // adjust index one step forward if this is the same time stamp
    if (((TailLine)logTailLines.get(index)).getTimeStamp() == sinceServerTimeMs) {
      index++;
    }

    // copy lines from tail lines to the resulting string array
    final String[] result = new String[lineCount - index];
    for (int i = index; i < lineCount; i++) {
      result[i - index] = ((TailLine)logTailLines.get(i)).getLine();
    }

    return new TailUpdateImpl(((TailLine)logTailLines.get(lineCount - 1)).getTimeStamp(), result);
  }

}
