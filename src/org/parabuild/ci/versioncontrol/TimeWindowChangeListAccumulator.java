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

import java.util.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;


/**
 * Change list accumulator. This class is to be used to
 * accumulate synthetic change lists for RCS that don't support
 * formal change lists.
 * <p/>
 * Allows to add change lists taking in account possible time
 * dispersion of change lists.
 * <p/>
 * When one commited changes to RCS, it's possible that the times
 * of some changes in the changelog will be different. The
 * solution is to round the time to before composing changelist
 * lookup hash. To mintain correct change list date, when the
 * change list is found, we update its time to the latest change
 * date that gets into this change list.
 */
public final class TimeWindowChangeListAccumulator {

//  private static final Log log = LogFactory.getLog(SurroundChangeLogParser.class);

  /**
   * Holds ChangeList with {@link ChangeListKey} objects as a
   * map.
   */
  private final SortedMap changeListMap = new TreeMap();

  /**
   * Time window within that changes are considered belonging to
   * the same change list.
   */
  private final long timeWindowMillis;

  /**
   * Maximum number of change lists.
   */
  private final int maxChangeLists;

  /**
   * Maximum size of a change list, files.
   */
  private final int maxChangeListSize;


  /**
   * Constructor
   *
   * @param timeWindowMillis
   */
  public TimeWindowChangeListAccumulator(final long timeWindowMillis, final int maxChangeLists, final int maxChangeListSize) {
    this.timeWindowMillis = timeWindowMillis;
    this.maxChangeLists = maxChangeLists;
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Adds change. Allows to use StringBuffer as sbMessage.
   *
   * @param createdAt - created at
   * @param sbMessage - sbMessage
   * @param user - user name
   * @param branch - branch
   * @param change - change to add
   */
  public void add(final Date createdAt, final StringBuffer sbMessage, final String user, final String branch, final Change change) {
    add(createdAt, sbMessage.toString(), user, branch, change);
  }


  /**
   * Adds change. Allows to use StringBuffer as stringMessage.
   *
   * @param createdAt - created at
   * @param stringMessage - stringMessage
   * @param user - user name
   * @param branch - branch
   * @param change - change to add
   */
  public void add(final Date createdAt, final String stringMessage, final String user, final String branch, final Change change) {
//    if (log.isDebugEnabled()) log.debug("-------------------------------------------------------------------");
    final String message = StringUtils.truncate(stringMessage.trim(), 1023);
    final ChangeListKey key = new ChangeListKey(timeWindowMillis, createdAt, message, user);

//    // get rounded time
//    long time = createdAt.getTime();
//    if (timeWindowMillis != 0) time -= (time % timeWindowMillis);
//
//    // get date hash from it
//    int dateHashCode = (int)time ^ (int)(time >> 32);
//
//    // lookup change list
//    Integer changeListHash = new Integer(user.hashCode() ^ message.hashCode() ^ dateHashCode);
    ChangeList changeList = (ChangeList)changeListMap.get(key);
//    if (log.isDebugEnabled()) log.debug("changeListMap.size() before: " + changeListMap.size());
    if (changeList != null) { // found
//      if (log.isDebugEnabled()) log.debug("found key: " + key);
      // if necessary, set changelist date to an older date
      if (createdAt.compareTo(changeList.getCreatedAt()) > 0) {
//        changeListMap.remove(key);
        changeList.setCreatedAt(createdAt);
//        ChangeListKey updatedKey = new ChangeListKey(timeWindowMillis, createdAt, message, user);
//        changeListMap.put(updatedKey, changeList); // put back
      }
    } else { // not found
//      if (log.isDebugEnabled()) log.debug("NOT found key: " + key);
      // decide if we have to add change list to result
      if (changeListMap.size() < maxChangeLists) {
        // note reached limit, just add
        changeList = addToResult(key, createdAt, message, user, branch);
      } else {
        // map is at limit
        final ChangeListKey lastKey = (ChangeListKey)changeListMap.lastKey();
        if (createdAt.compareTo(lastKey.createdAt()) > 0) { // newer or equal oldest?
          // yes, have to add and remove last
          changeListMap.remove(lastKey);
          changeList = addToResult(key, createdAt, message, user, branch);
        }
      }
//      if (log.isDebugEnabled()) log.debug("added changeList: " + changeList);
    }


    if (changeList != null) { // was change list for this change selected?
      if (changeList.getChanges().size() < maxChangeListSize) {
        changeList.getChanges().add(change);
      } else {
        changeList.setTruncated(true);
      }
      changeList.incrementOriginalSize();
    }
//    if (log.isDebugEnabled()) log.debug("changeListMap.size() after: " + changeListMap.size());
//    if (log.isDebugEnabled()) log.debug("processed key: " + key + ", changeList: " + changeList);
  }


  private ChangeList addToResult(final ChangeListKey key, final Date createdAt, final String message, final String user, final String branch) {
    final ChangeList changeList = new ChangeList();
    changeList.setCreatedAt(createdAt);
    changeList.setDescription(message);
    changeList.setUser(user);
    changeList.setChanges(new HashSet(11));
    changeList.setBranch(branch);
    changeListMap.put(key, changeList);

    /*
    [junit] DEBUG: changeList at: Sat Aug 09 20:41:57 PDT 2003: ChangeList [branch='null', changeListID=-1, createdAt=Sat Aug 09 20:41:57 PDT 2003, description='Continued working on creating new build', user='slava', client='null', email='null', number='null', changes=[org.parabuild.ci.object.Change@1da669c]]
    [junit] DEBUG: changeList at: Sat Aug 09 20:41:58 PDT 2003: ChangeList [branch='null', changeListID=-1, createdAt=Sat Aug 09 20:41:58 PDT 2003, description='Continued working on creating new build', user='slava', client='null', email='null', number='null', changes=[org.parabuild.ci.object.Change@1fd6bea, org.parabuild.ci.object.Change@110c31, org.parabuild.ci.object.Change@b03be0, org.parabuild.ci.object.Change@7736bd, org.parabuild.ci.object.Change@16a38b5, org.parabuild.ci.object.Change@19bfb30, org.parabuild.ci.object.Change@18bbc5a, org.parabuild.ci.object.Change@eac5a, org.parabuild.ci.object.Change@12a55aa, org.parabuild.ci.object.Change@dc67e, org.parabuild.ci.object.Change@1e328e0, org.parabuild.ci.object.Change@a77106, org.parabuild.ci.object.Change@c44b88, org.parabuild.ci.object.Change@13c7378, org.parabuild.ci.object.Change@1c6572b]]
    */
    return changeList;
  }


  /**
   * Returns change lists
   *
   * @return List of {@link ChangeList} objects.
   */
  public List getChangeLists() {
    return new ArrayList(changeListMap.values());
  }


  /**
   * This class holed ChangeList key and change l
   */
  public static final class ChangeListKey implements Comparable {

    private final int hashCode;
    private final Date createdAt;


    /**
     * Constructor.
     *
     * @param timeWindowMillis
     * @param createdAt
     * @param stringMessage
     * @param user
     */
    public ChangeListKey(final long timeWindowMillis, final Date createdAt, final String stringMessage, final String user) {
      // get date hash from it
      long time = createdAt.getTime();
//      if (log.isDebugEnabled()) log.debug("time before: " + time);
      if (timeWindowMillis != 0) {
        final long rest = time % timeWindowMillis;
//        if (log.isDebugEnabled()) log.debug("       rest: " + rest);
        time -= rest;
//        if (log.isDebugEnabled()) log.debug("       rest2: " + (time % timeWindowMillis));
      }
//      if (log.isDebugEnabled()) log.debug(" time after: " + time);
      final int dateHashCode = (int)time ^ (int)(time >> 32);
      // finish calculation
      this.hashCode = user.hashCode() ^ stringMessage.hashCode() ^ dateHashCode;
      this.createdAt = createdAt;
    }


    public int compareTo(final Object o) {
      if (!(o instanceof ChangeListKey)) return 1;
      final ChangeListKey changeListKey = (ChangeListKey)o;
      if (hashCode == changeListKey.hashCode) return 0; // we ignore date's non-equality if our "cut" dates are the same.
      return this.createdAt.compareTo(changeListKey.createdAt);
    }


    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof ChangeListKey)) return false;
      final ChangeListKey changeListKey = (ChangeListKey)o;
      return hashCode == changeListKey.hashCode;
    }


    public int hashCode() {
      return hashCode;
    }


    public Date createdAt() {
      return createdAt;
    }


    public String toString() {
      return "ChangeListKey{" +
        "hashCode=" + hashCode +
        ", createdAt=" + createdAt +
        '}';
    }
  }
}


