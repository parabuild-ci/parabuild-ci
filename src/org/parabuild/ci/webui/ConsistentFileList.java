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
package org.parabuild.ci.webui;

import java.io.*;
import java.util.*;

import org.parabuild.ci.archive.*;
import org.parabuild.ci.object.*;

/**
 * This class holds a list of files addressable by name and by
 * ID. ID is usable for puposes of diplaying web ui links,
 * particularly when showing log links.
 */
public final class ConsistentFileList implements Serializable {

  private static final long serialVersionUID = 4546658248844898708L; // NOPMD

  private final Map idMap = new HashMap(11);
  private final Map nameIDMap = new HashMap(11);


  /**
   * Constructor.
   *
   * @see LogConfig#LOG_TYPE_TEXT_DIR
   */
  public ConsistentFileList(final ArchiveManager archiveManager, final StepLog stepLog) throws IOException {
    final List entries = archiveManager.getArchivedLogEntries(stepLog);
    for (int i = 0, n = entries.size(); i < n; i++) {
      final String entry = ((ArchiveEntry)entries.get(i)).getEntryName();
      idMap.put(new Integer(i), entry);
      nameIDMap.put(entry, new Integer(i));
    }
  }


  /**
   * @param paramFileID
   *
   * @return assosiated file name
   */
  public String getFileNameByID(final int paramFileID) {
    return (String)idMap.get(new Integer(paramFileID));
  }


  /**
   * Returns encapsulated files list.
   */
  public String[] getFileNames() {
    final Collection files = idMap.values();
    return (String[])files.toArray(new String[files.size()]);
  }


  public int getFileNameID(final String fileName) {
    return getFileID(fileName);
  }


  public int getFileID(final String fileName) {
    final Integer id = (Integer)nameIDMap.get(fileName);
    if (id != null) return id.intValue(); // found
    // try case incensitive search
    final Set set = nameIDMap.entrySet();
    for (final Iterator i = set.iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry)i.next();
      final String nameKey = (String)entry.getKey();
      if (nameKey.equalsIgnoreCase(fileName)) return ((Integer)entry.getValue()).intValue();
    }
    return -1;
  }
}


