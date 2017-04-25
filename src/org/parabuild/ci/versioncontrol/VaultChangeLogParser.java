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
package org.parabuild.ci.versioncontrol;

import java.io.*;
import java.text.*;
import java.util.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;

/**
 *
 */
final class VaultChangeLogParser {

//  private static final Log log = LogFactory.getLog(VaultChangeLogParser.class);

  private final VaultDateFormat dateFormat;
  private final int maxChangeListSize;


  /**
   * Constructor
   *
   * @param locale {@link Locale} to use when parsing Vault
   * dates.
   */
  public VaultChangeLogParser(final Locale locale, final int maxChangeListSize) {
    this.dateFormat = new VaultDateFormat(locale);
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Parces Vault change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param file to parse
   *
   * @return List of ChaneList elements, maybe empty.
   */
  public List parseChangeLog(final File file) throws IOException {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      return parseChangeLog(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * Parces SVN change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param input InputStream to get log data from.
   *
   * @return List of change lists.
   */
  public List parseChangeLog(final InputStream input) throws IOException {
    try {
      // parce vault output
      final VaultOutputParser outputParser = new VaultOutputParser();
      final Vault vault = outputParser.parse(input);
      if (vault.getHistory() == null)
        throw new IOException("Cannot find Vault history");

      // gather change lists
      final List result = new ArrayList(111);
      final Map changeLists = new HashMap(111);
      final List items = vault.getHistory().getItems();
//      if (log.isDebugEnabled()) log.debug("items: " + items);
      for (int i = 0, n = items.size(); i < n; i++) {
        final Vault.Item item = (Vault.Item)items.get(i);
        final int changeListNumber = item.getTxid();
        final Integer key = new Integer(changeListNumber);
        ChangeList changeList = (ChangeList)changeLists.get(key);
        if (changeList == null) {
          // new change list
          changeList = new ChangeList();
          changeList.setCreatedAt(dateFormat.parseOutput(item.getDate()));
          changeList.setDescription(getDescriptionFromItem(item));
          changeList.setNumber(Integer.toString(changeListNumber));
          changeList.setUser(item.getUser());
          changeLists.put(key, changeList);
        }
        if (changeList.getOriginalSize() < maxChangeListSize) {
          final Change change = new Change();
          change.setChangeType(getChangeTypeFromItem(item));
          change.setFilePath(item.getName());
          change.setRevision(Integer.toString(item.getVersion()));
          changeList.getChanges().add(change);
        } else {
          changeList.setTruncated(true);
        }
        changeList.incrementOriginalSize();
      }
      result.addAll(changeLists.values());
      return result;
    } catch (ParseException e) {
      throw IoUtils.createIOException(e);
    }
  }


  /**
   * Helper method.
   *
   * @param item
   *
   * @return change list desciption.
   */
  private String getDescriptionFromItem(final Vault.Item item) {
    return item.getComment() == null ? "No comment was provided" : StringUtils.truncate(item.getComment(), 1023);
  }


  /**
   * Helper method.
   *
   * @param item
   *
   * @return type according to {@link Change}
   */
  private byte getChangeTypeFromItem(final Vault.Item item) {
    byte result = Change.TYPE_UNKNOWN;
    final int itemType = item.getType();
    switch (itemType) {
      case 10:
        result = Change.TYPE_ADDED;
        break;
      case 20:
      case 30:
      case 40:
      case 50:
        result = Change.TYPE_BRANCHED;
        break;
      case 60:
        result = Change.TYPE_CHECKIN;
        break;
      case 70:
        result = Change.TYPE_CREATE_ELEMENT;
        break;
      case 80:
        result = Change.TYPE_DELETED;
        break;
      case 120:
      case 130:
        result = Change.TYPE_MOVED;
        break;
      case 210:
        result = Change.TYPE_UNDELETED;
        break;
      case 230:
        result = Change.TYPE_ROLLEDBACK;
        break;
      default:
        result = Change.TYPE_UNKNOWN;
        break;
    }
    return result;
  }
}
